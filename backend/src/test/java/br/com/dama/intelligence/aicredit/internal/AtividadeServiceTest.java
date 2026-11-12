package br.com.dama.intelligence.aicredit.internal;

import br.com.dama.intelligence.aicredit.api.*;
import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorView;
import br.com.dama.intelligence.organizacao.api.OrganizacaoFacade;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.error.ValidationException;
import br.com.dama.intelligence.shared.event.DesempenhoAtualizadoEvent;
import br.com.dama.intelligence.shared.security.EscopoEmpresa;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AtividadeServiceTest {

    private AtividadeRepository repo;
    private RegraCreditoFacade regras;
    private AiCreditFacade creditos;
    private ColaboradorFacade colaboradores;
    private ApplicationEventPublisher eventos;
    private AtividadeService service;

    @BeforeEach
    void setUp() {
        repo = mock(AtividadeRepository.class);
        regras = mock(RegraCreditoFacade.class);
        creditos = mock(AiCreditFacade.class);
        colaboradores = mock(ColaboradorFacade.class);
        eventos = mock(ApplicationEventPublisher.class);
        service = new AtividadeService(repo, regras, creditos, colaboradores, mock(OrganizacaoFacade.class),
                mock(AuditoriaFacade.class), eventos, new EscopoEmpresa());
    }

    @Test
    void atividadeElegivelGeraUmCreditoFixoPorRegraSemMultiplicarQuantidade() {
        AtividadeRegistroView registro = new AtividadeRegistroView(9L, 1L, 10L, new BigDecimal("4"), LocalDateTime.now());
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, true));
        when(repo.buscarAtividade(1L)).thenReturn(Optional.of(atividade(1L, 1L, true)));
        when(repo.inserirRegistro(1L, 10L, new BigDecimal("4"))).thenReturn(registro);
        when(regras.regrasAtivasDaAtividade(1L)).thenReturn(List.of(regra(3L, new BigDecimal("5")), regra(4L, new BigDecimal("2.5"))));

        RegistroAtividadeResultadoView resultado = service.registrarAtividade(10L, new RegistrarAtividadeCommand(1L, new BigDecimal("4")));

        assertThat(resultado.elegivel()).isTrue();
        assertThat(resultado.creditosConcedidos()).isEqualByComparingTo("7.5");
        verify(repo).inserirRegistro(1L, 10L, new BigDecimal("4"));
        verify(creditos).creditarPorRegra(10L, 3L, 9L, new BigDecimal("5"), "Atividade elegível: Curso");
        verify(creditos).creditarPorRegra(10L, 4L, 9L, new BigDecimal("2.5"), "Atividade elegível: Curso");
        verify(eventos).publishEvent(new DesempenhoAtualizadoEvent(10L));
    }

    @Test
    void atividadeSemRegraContinuaRegistradaEQuantidadeAusenteValeUm() {
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, true));
        when(repo.buscarAtividade(1L)).thenReturn(Optional.of(atividade(1L, 1L, true)));
        when(repo.inserirRegistro(1L, 10L, BigDecimal.ONE)).thenReturn(new AtividadeRegistroView(9L, 1L, 10L, BigDecimal.ONE, LocalDateTime.now()));
        when(regras.regrasAtivasDaAtividade(1L)).thenReturn(List.of());

        RegistroAtividadeResultadoView resultado = service.registrarAtividade(10L, new RegistrarAtividadeCommand(1L, null));

        assertThat(resultado.elegivel()).isFalse();
        assertThat(resultado.creditosConcedidos()).isEqualByComparingTo("0");
        verify(creditos, never()).creditarPorRegra(any(), any(), any(), any(), any());
    }

    @Test
    void registroRecusaColaboradorInativoOuAtividadeDeOutraEmpresaOuInativa() {
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, false));
        assertThatThrownBy(() -> service.registrarAtividade(10L, new RegistrarAtividadeCommand(1L, null)))
                .isInstanceOf(ValidationException.class).hasMessageContaining("inativo");

        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, true));
        when(repo.buscarAtividade(1L)).thenReturn(Optional.of(atividade(1L, 2L, true)));
        assertThatThrownBy(() -> service.registrarAtividade(10L, new RegistrarAtividadeCommand(1L, null)))
                .isInstanceOf(ValidationException.class).hasMessageContaining("não pertence");

        when(repo.buscarAtividade(1L)).thenReturn(Optional.of(atividade(1L, 1L, false)));
        assertThatThrownBy(() -> service.registrarAtividade(10L, new RegistrarAtividadeCommand(1L, null)))
                .isInstanceOf(ValidationException.class).hasMessageContaining("inativa");

        when(repo.buscarAtividade(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.registrarAtividade(10L, new RegistrarAtividadeCommand(1L, null)))
                .isInstanceOf(NotFoundException.class);
    }

    static AtividadeProdutivaView atividade(long id, long empresa, boolean ativa) {
        return new AtividadeProdutivaView(id, empresa, "Curso", "Descrição", ativa);
    }

    static RegraCreditoView regra(long id, BigDecimal credito) {
        return new RegraCreditoView(id, 1L, 1L, "Regra " + id, "critério", credito, true);
    }

    static ColaboradorView colaborador(long id, long empresa, boolean ativo) {
        return new ColaboradorView(id, empresa, 1L, "Time", "Ana", "ana@empresa.com", "Analista", "Pleno",
                LocalDate.of(2025, 1, 1), ativo);
    }
}
