package br.com.dama.intelligence.aicredit.internal;

import br.com.dama.intelligence.aicredit.api.*;
import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorView;
import br.com.dama.intelligence.organizacao.api.OrganizacaoFacade;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AiCreditServiceTest {

    private AiCreditRepository repo;
    private ColaboradorFacade colaboradores;
    private OrganizacaoFacade organizacao;
    private AuditoriaFacade auditoria;
    private ApplicationEventPublisher eventos;
    private AiCreditService service;

    @BeforeEach
    void setUp() {
        repo = mock(AiCreditRepository.class);
        colaboradores = mock(ColaboradorFacade.class);
        organizacao = mock(OrganizacaoFacade.class);
        auditoria = mock(AuditoriaFacade.class);
        eventos = mock(ApplicationEventPublisher.class);
        service = new AiCreditService(repo, colaboradores, organizacao, auditoria, eventos);
    }

    @Test
    void atividadeElegivelGeraUmCreditoFixoPorRegraSemMultiplicarQuantidade() {
        AtividadeProdutivaView atividade = atividade(1L, 1L, true);
        AtividadeRegistroView registro = new AtividadeRegistroView(9L, 1L, 10L, new BigDecimal("4"), LocalDateTime.now());
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, true));
        when(repo.buscarAtividade(1L)).thenReturn(Optional.of(atividade));
        when(repo.inserirRegistro(1L, 10L, new BigDecimal("4"))).thenReturn(registro);
        RegraCreditoView r1 = regra(3L, new BigDecimal("5"));
        RegraCreditoView r2 = regra(4L, new BigDecimal("2.5"));
        when(repo.regrasAtivasDaAtividade(1L)).thenReturn(List.of(r1, r2));
        when(repo.inserirTransacao(eq(10L), anyLong(), eq(9L), eq("CREDITO"), any(), anyString()))
                .thenReturn(transacao(20L, "CREDITO", new BigDecimal("5")))
                .thenReturn(transacao(21L, "CREDITO", new BigDecimal("2.5")));

        RegistroAtividadeResultadoView resultado = service.registrarAtividade(10L, new RegistrarAtividadeCommand(1L, new BigDecimal("4")));

        assertThat(resultado.elegivel()).isTrue();
        assertThat(resultado.creditosConcedidos()).isEqualByComparingTo("7.5");
        verify(repo).inserirRegistro(1L, 10L, new BigDecimal("4"));
        verify(repo).inserirTransacao(10L, 3L, 9L, "CREDITO", new BigDecimal("5"), "Atividade elegível: Curso");
        verify(repo).inserirTransacao(10L, 4L, 9L, "CREDITO", new BigDecimal("2.5"), "Atividade elegível: Curso");
        verify(eventos).publishEvent(new DesempenhoAtualizadoEvent(10L));
    }

    @Test
    void atividadeSemRegraContinuaRegistradaEQuantidadeAusenteValeUm() {
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, true));
        when(repo.buscarAtividade(1L)).thenReturn(Optional.of(atividade(1L, 1L, true)));
        AtividadeRegistroView registro = new AtividadeRegistroView(9L, 1L, 10L, BigDecimal.ONE, LocalDateTime.now());
        when(repo.inserirRegistro(1L, 10L, BigDecimal.ONE)).thenReturn(registro);
        when(repo.regrasAtivasDaAtividade(1L)).thenReturn(List.of());

        RegistroAtividadeResultadoView resultado = service.registrarAtividade(10L, new RegistrarAtividadeCommand(1L, null));

        assertThat(resultado.elegivel()).isFalse();
        assertThat(resultado.creditosConcedidos()).isEqualByComparingTo("0");
        verify(repo, never()).inserirTransacao(any(), any(), any(), any(), any(), any());
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

    @Test
    void debitoExigeSaldoMasCreditoRequerColaboradorAtivoEPossuiEvento() {
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, true));
        when(repo.saldo(10L)).thenReturn(new BigDecimal("2"));
        assertThatThrownBy(() -> service.lancarTransacao(10L, new LancarTransacaoCommand("DEBITO", new BigDecimal("3"), "uso")))
                .isInstanceOf(ValidationException.class).hasMessageContaining("insuficiente");
        verify(repo).bloquearSaldo(10L);

        when(repo.inserirTransacao(10L, null, null, "DEBITO", new BigDecimal("2"), "uso"))
                .thenReturn(transacao(30L, "DEBITO", new BigDecimal("2")));
        assertThat(service.lancarTransacao(10L, new LancarTransacaoCommand("DEBITO", new BigDecimal("2"), "uso")).tipo())
                .isEqualTo("DEBITO");
        verify(eventos, never()).publishEvent(new DesempenhoAtualizadoEvent(10L));

        when(repo.inserirTransacao(10L, null, null, "CREDITO", new BigDecimal("6"), "bônus"))
                .thenReturn(transacao(31L, "CREDITO", new BigDecimal("6")));
        service.lancarTransacao(10L, new LancarTransacaoCommand("CREDITO", new BigDecimal("6"), "bônus"));
        verify(eventos).publishEvent(new DesempenhoAtualizadoEvent(10L));

        when(colaboradores.buscarPorId(11L)).thenReturn(colaborador(11L, 1L, false));
        assertThatThrownBy(() -> service.lancarTransacao(11L, new LancarTransacaoCommand("CREDITO", BigDecimal.ONE, "bônus")))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void regraSaldoElegibilidadeENiveisRespeitamEmpresaEValoresDoRepositorio() {
        when(organizacao.existeEmpresa(1L)).thenReturn(true);
        when(repo.buscarAtividade(1L)).thenReturn(Optional.of(atividade(1L, 1L, true)));
        RegraCreditoView regra = regra(3L, BigDecimal.TEN);
        when(repo.inserirRegra(1L, 1L, "Curso concluído", "certificado", BigDecimal.TEN)).thenReturn(regra);
        assertThat(service.criarRegra(1L, new CreateRegraCreditoCommand(1L, "Curso concluído", "certificado", BigDecimal.TEN)))
                .isEqualTo(regra);

        when(repo.buscarAtividade(2L)).thenReturn(Optional.of(atividade(2L, 2L, true)));
        assertThatThrownBy(() -> service.criarRegra(1L, new CreateRegraCreditoCommand(2L, "X", "x", BigDecimal.ONE)))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> service.listarRegras(9L)).isInstanceOf(NotFoundException.class);

        when(repo.regrasAtivasDaAtividade(1L)).thenReturn(List.of(regra));
        assertThat(service.avaliarElegibilidade(1L).elegivel()).isTrue();
        when(repo.buscarAtividade(8L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.avaliarElegibilidade(8L)).isInstanceOf(NotFoundException.class);

        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, true));
        NivelAutonomiaView nivel = new NivelAutonomiaView(1L, "Autônomo", new BigDecimal("10"), 2, null);
        when(repo.saldo(10L)).thenReturn(new BigDecimal("12"));
        when(repo.nivelParaSaldo(new BigDecimal("12"))).thenReturn(Optional.of(nivel));
        assertThat(service.saldo(10L).nivelAtual()).isEqualTo(nivel);
        when(repo.inserirNivel("Autônomo", BigDecimal.TEN, 2, "ok")).thenReturn(nivel);
        assertThat(service.criarNivel(new CreateNivelCommand("Autônomo", BigDecimal.TEN, 2, "ok"))).isEqualTo(nivel);
    }

    private static AtividadeProdutivaView atividade(long id, long empresa, boolean ativa) {
        return new AtividadeProdutivaView(id, empresa, "Curso", "Descrição", ativa);
    }

    private static RegraCreditoView regra(long id, BigDecimal credito) {
        return new RegraCreditoView(id, 1L, 1L, "Regra " + id, "critério", credito, true);
    }

    private static TransacaoCreditoView transacao(long id, String tipo, BigDecimal quantidade) {
        return new TransacaoCreditoView(id, 10L, null, null, tipo, quantidade, "motivo", LocalDateTime.now());
    }

    private static ColaboradorView colaborador(long id, long empresa, boolean ativo) {
        return new ColaboradorView(id, empresa, 1L, "Time", "Ana", "ana@empresa.com", "Analista", "Pleno",
                LocalDate.of(2025, 1, 1), ativo);
    }
}
