package br.com.dama.intelligence.pontuacao.internal;

import br.com.dama.intelligence.aicredit.api.AiCreditFacade;
import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorView;
import br.com.dama.intelligence.organizacao.api.OrganizacaoFacade;
import br.com.dama.intelligence.pontuacao.api.*;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.error.ValidationException;
import br.com.dama.intelligence.shared.event.DesempenhoAtualizadoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PontuacaoServiceTest {
    private PontuacaoRepository repo;
    private ColaboradorFacade colaboradores;
    private AiCreditFacade aiCredits;
    private ApplicationEventPublisher eventos;
    private PontuacaoService service;

    @BeforeEach
    void setUp() {
        repo = mock(PontuacaoRepository.class);
        colaboradores = mock(ColaboradorFacade.class);
        aiCredits = mock(AiCreditFacade.class);
        eventos = mock(ApplicationEventPublisher.class);
        OrganizacaoFacade organizacao = mock(OrganizacaoFacade.class);
        when(organizacao.existeEmpresa(1L)).thenReturn(true);
        service = new PontuacaoService(repo, colaboradores, organizacao, aiCredits, mock(AuditoriaFacade.class), eventos);
    }

    @Test
    void criarRegraExigeEmpresaEAtividadeDaMesmaEmpresa() {
        assertThatThrownBy(() -> service.listarRegras(2L)).isInstanceOf(NotFoundException.class);
        when(aiCredits.atividadePertenceAEmpresa(9L, 1L)).thenReturn(false);
        assertThatThrownBy(() -> service.criarRegra(1L, new CreateRegraPontuacaoCommand(9L, "Entrega", BigDecimal.TEN)))
                .isInstanceOf(ValidationException.class);

        RegraPontuacaoView regra = regra(1L, true);
        when(repo.inserirRegra(1L, null, "Entrega", BigDecimal.TEN)).thenReturn(regra);
        assertThat(service.criarRegra(1L, new CreateRegraPontuacaoCommand(null, "Entrega", BigDecimal.TEN))).isEqualTo(regra);
    }

    @Test
    void regraPodeSerAtivadaOuDesativadaEInexistenteGera404() {
        when(repo.buscarRegra(1L)).thenReturn(Optional.of(regra(1L, true)), Optional.of(regra(1L, false)));
        assertThat(service.alterarRegraAtiva(1L, false).ativa()).isFalse();
        verify(repo).atualizarRegraAtiva(1L, false);
        when(repo.buscarRegra(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.alterarRegraAtiva(9L, true)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void lancamentoExigeColaboradorAtivoRegraAtivaEDaMesmaEmpresa() {
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, false));
        assertThatThrownBy(() -> service.lancar(10L, new LancarPontosCommand(1L, null))).isInstanceOf(ValidationException.class);

        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, true));
        when(repo.buscarRegra(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.lancar(10L, new LancarPontosCommand(1L, null))).isInstanceOf(NotFoundException.class);
        when(repo.buscarRegra(1L)).thenReturn(Optional.of(new RegraPontuacaoView(1L, 2L, null, "Entrega", BigDecimal.TEN, true)));
        assertThatThrownBy(() -> service.lancar(10L, new LancarPontosCommand(1L, null))).isInstanceOf(ValidationException.class).hasMessageContaining("não pertence");
        when(repo.buscarRegra(1L)).thenReturn(Optional.of(regra(1L, false)));
        assertThatThrownBy(() -> service.lancar(10L, new LancarPontosCommand(1L, null))).isInstanceOf(ValidationException.class).hasMessageContaining("inativa");
    }

    @Test
    void lancamentoUsaDescricaoDaRegraQuandoAusenteEPublIcaEvento() {
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, true));
        when(repo.buscarRegra(1L)).thenReturn(Optional.of(regra(1L, true)));
        PontuacaoView lancada = pontuacao(10L, 1L, BigDecimal.TEN, "Entrega");
        when(repo.inserirPontuacao(10L, 1L, BigDecimal.TEN, "Entrega")).thenReturn(lancada);
        assertThat(service.lancar(10L, new LancarPontosCommand(1L, " "))).isEqualTo(lancada);
        verify(eventos).publishEvent(new DesempenhoAtualizadoEvent(10L));

        when(repo.inserirPontuacao(10L, 1L, BigDecimal.TEN, "extra")).thenReturn(lancada);
        service.lancar(10L, new LancarPontosCommand(1L, "extra"));
        verify(repo).inserirPontuacao(10L, 1L, BigDecimal.TEN, "extra");
    }

    @Test
    void resumoECreditoAutomaticoUsamDadosDoRepositorio() {
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, true));
        when(repo.total(10L)).thenReturn(new BigDecimal("22"));
        when(repo.historico(10L)).thenReturn(List.of(pontuacao(10L, null, BigDecimal.TEN, "bônus")));
        assertThat(service.resumo(10L).pontosTotais()).isEqualByComparingTo("22");
        PontuacaoView automatico = pontuacao(10L, null, BigDecimal.ONE, "Desafio");
        when(repo.inserirPontuacao(10L, null, BigDecimal.ONE, "Desafio")).thenReturn(automatico);
        assertThat(service.creditarPontos(10L, BigDecimal.ONE, "Desafio")).isEqualTo(automatico);
    }

    private static RegraPontuacaoView regra(long id, boolean ativa) {
        return new RegraPontuacaoView(id, 1L, null, "Entrega", BigDecimal.TEN, ativa);
    }

    private static PontuacaoView pontuacao(long colaborador, Long regra, BigDecimal pontos, String descricao) {
        return new PontuacaoView(1L, colaborador, regra, pontos, descricao, LocalDateTime.now());
    }

    private static ColaboradorView colaborador(long id, long empresa, boolean ativo) {
        return new ColaboradorView(id, empresa, 1L, "Time", "Ana", "ana@x.com", "Analista", "Pleno", LocalDate.now(), ativo);
    }
}
