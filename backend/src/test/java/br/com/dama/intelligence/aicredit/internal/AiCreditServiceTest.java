package br.com.dama.intelligence.aicredit.internal;

import br.com.dama.intelligence.aicredit.api.*;
import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorFacade;
import br.com.dama.intelligence.organizacao.api.OrganizacaoFacade;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.error.ValidationException;
import br.com.dama.intelligence.shared.event.DesempenhoAtualizadoEvent;
import br.com.dama.intelligence.shared.security.EscopoEmpresa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static br.com.dama.intelligence.aicredit.internal.AtividadeServiceTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AiCreditServiceTest {

    private AiCreditRepository repo;
    private ColaboradorFacade colaboradores;
    private ApplicationEventPublisher eventos;
    private AiCreditService service;

    private RegraCreditoRepository regrasRepo;
    private AtividadeRepository atividades;
    private OrganizacaoFacade organizacao;
    private RegraCreditoService regrasService;

    @BeforeEach
    void setUp() {
        repo = mock(AiCreditRepository.class);
        colaboradores = mock(ColaboradorFacade.class);
        eventos = mock(ApplicationEventPublisher.class);
        service = new AiCreditService(repo, colaboradores, mock(AuditoriaFacade.class), eventos, new EscopoEmpresa());

        regrasRepo = mock(RegraCreditoRepository.class);
        atividades = mock(AtividadeRepository.class);
        organizacao = mock(OrganizacaoFacade.class);
        regrasService = new RegraCreditoService(regrasRepo, atividades, organizacao, mock(AuditoriaFacade.class), new EscopoEmpresa());
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
    void creditoPorRegraGuardaOrigemENaoPublicaEventoPorConta() {
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, true));
        when(repo.inserirTransacao(10L, 3L, 9L, "CREDITO", BigDecimal.TEN, "Atividade elegível: Curso"))
                .thenReturn(transacao(40L, "CREDITO", BigDecimal.TEN));

        assertThat(service.creditarPorRegra(10L, 3L, 9L, BigDecimal.TEN, "Atividade elegível: Curso").transacaoId()).isEqualTo(40L);
        verifyNoInteractions(eventos); // quem publica é o registro da atividade, uma vez só
    }

    @Test
    void saldoENiveisVemDoRepositorio() {
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, true));
        NivelAutonomiaView nivel = new NivelAutonomiaView(1L, "Autônomo", new BigDecimal("10"), 2, null);
        when(repo.saldo(10L)).thenReturn(new BigDecimal("12"));
        when(repo.nivelParaSaldo(new BigDecimal("12"))).thenReturn(Optional.of(nivel));
        assertThat(service.saldo(10L).nivelAtual()).isEqualTo(nivel);
        when(repo.inserirNivel("Autônomo", BigDecimal.TEN, 2, "ok")).thenReturn(nivel);
        assertThat(service.criarNivel(new CreateNivelCommand("Autônomo", BigDecimal.TEN, 2, "ok"))).isEqualTo(nivel);
    }

    @Test
    void regraEElegibilidadeRespeitamEmpresaEAtividade() {
        when(organizacao.existeEmpresa(1L)).thenReturn(true);
        when(atividades.buscarAtividade(1L)).thenReturn(Optional.of(atividade(1L, 1L, true)));
        RegraCreditoView regra = regra(3L, BigDecimal.TEN);
        when(regrasRepo.inserirRegra(1L, 1L, "Curso concluído", "certificado", BigDecimal.TEN)).thenReturn(regra);
        assertThat(regrasService.criarRegra(1L, new CreateRegraCreditoCommand(1L, "Curso concluído", "certificado", BigDecimal.TEN)))
                .isEqualTo(regra);

        when(atividades.buscarAtividade(2L)).thenReturn(Optional.of(atividade(2L, 2L, true)));
        assertThatThrownBy(() -> regrasService.criarRegra(1L, new CreateRegraCreditoCommand(2L, "X", "x", BigDecimal.ONE)))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> regrasService.listarRegras(9L)).isInstanceOf(NotFoundException.class);

        when(regrasRepo.regrasAtivasDaAtividade(1L)).thenReturn(List.of(regra));
        assertThat(regrasService.avaliarElegibilidade(1L).elegivel()).isTrue();
        when(atividades.buscarAtividade(8L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> regrasService.avaliarElegibilidade(8L)).isInstanceOf(NotFoundException.class);
    }

    private static TransacaoCreditoView transacao(long id, String tipo, BigDecimal quantidade) {
        return new TransacaoCreditoView(id, 10L, null, null, tipo, quantidade, "motivo", LocalDateTime.now());
    }
}
