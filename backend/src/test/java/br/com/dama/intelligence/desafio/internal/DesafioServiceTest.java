package br.com.dama.intelligence.desafio.internal;

import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorView;
import br.com.dama.intelligence.desafio.api.CreateDesafioCommand;
import br.com.dama.intelligence.desafio.api.DesafioView;
import br.com.dama.intelligence.desafio.api.ParticipanteView;
import br.com.dama.intelligence.organizacao.api.OrganizacaoFacade;
import br.com.dama.intelligence.pontuacao.api.PontuacaoFacade;
import br.com.dama.intelligence.shared.error.ConflictException;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.error.ValidationException;
import br.com.dama.intelligence.shared.event.DesempenhoAtualizadoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DesafioServiceTest {
    private DesafioRepository repo;
    private ColaboradorFacade colaboradores;
    private PontuacaoFacade pontuacao;
    private ApplicationEventPublisher eventos;
    private DesafioService service;

    @BeforeEach
    void setUp() {
        repo = mock(DesafioRepository.class);
        colaboradores = mock(ColaboradorFacade.class);
        pontuacao = mock(PontuacaoFacade.class);
        eventos = mock(ApplicationEventPublisher.class);
        OrganizacaoFacade organizacao = mock(OrganizacaoFacade.class);
        when(organizacao.existeEmpresa(1L)).thenReturn(true);
        service = new DesafioService(repo, colaboradores, organizacao, pontuacao, mock(AuditoriaFacade.class), eventos);
    }

    @Test
    void validaEmpresaStatusEPeriodoAoListarOuCriar() {
        assertThatThrownBy(() -> service.listar(1L, "Invalido")).isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> service.listar(2L, null)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> service.criar(1L, comando(LocalDate.now(), LocalDate.now().minusDays(1))))
                .isInstanceOf(ValidationException.class).hasMessageContaining("dataFim");

        DesafioView criado = desafio(1L, "Planejado", BigDecimal.TEN);
        when(repo.inserir(eq(1L), anyString(), any(), any(), any(), any())).thenReturn(1L);
        when(repo.buscar(1L)).thenReturn(Optional.of(criado));
        assertThat(service.criar(1L, comando(LocalDate.now(), LocalDate.now().plusDays(1)))).isEqualTo(criado);
        when(repo.buscar(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscar(9L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void inscricaoExigeStatusAbertoMesmoEmpresaColaboradorAtivoENaoDuplicidade() {
        when(repo.buscar(1L)).thenReturn(Optional.of(desafio(1L, "Encerrado", BigDecimal.TEN)));
        assertThatThrownBy(() -> service.inscrever(1L, 10L)).isInstanceOf(ValidationException.class);

        when(repo.buscar(1L)).thenReturn(Optional.of(desafio(1L, "Planejado", BigDecimal.TEN)));
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 2L, true));
        assertThatThrownBy(() -> service.inscrever(1L, 10L)).isInstanceOf(ValidationException.class).hasMessageContaining("não pertence");
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, false));
        assertThatThrownBy(() -> service.inscrever(1L, 10L)).isInstanceOf(ValidationException.class).hasMessageContaining("inativo");
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, true));
        when(repo.buscarParticipante(1L, 10L)).thenReturn(Optional.of(participante("Inscrito")));
        assertThatThrownBy(() -> service.inscrever(1L, 10L)).isInstanceOf(ConflictException.class);

        when(repo.buscarParticipante(1L, 10L)).thenReturn(Optional.empty());
        when(repo.inserirParticipante(1L, 10L)).thenReturn(participante("Inscrito"));
        assertThat(service.inscrever(1L, 10L).status()).isEqualTo("Inscrito");
    }

    @Test
    void concluirCreditaRecompensaPositivaENaoCreditaZeroEImpedeParticipacaoFinalizada() {
        when(repo.buscar(1L)).thenReturn(Optional.of(desafio(1L, "Em andamento", new BigDecimal("15"))));
        when(repo.buscarParticipante(1L, 10L)).thenReturn(Optional.of(participante("Inscrito")));
        when(repo.buscarParticipante(1L, 10L)).thenReturn(Optional.of(participante("Inscrito")), Optional.of(participante("Concluido")));
        assertThat(service.concluir(1L, 10L).status()).isEqualTo("Concluido");
        verify(pontuacao).creditarPontos(10L, new BigDecimal("15"), "Desafio concluído: Desafio");
        verify(eventos).publishEvent(new DesempenhoAtualizadoEvent(10L));

        when(repo.buscar(2L)).thenReturn(Optional.of(desafio(2L, "Em andamento", BigDecimal.ZERO)));
        when(repo.buscarParticipante(2L, 10L)).thenReturn(Optional.of(new ParticipanteView(2L, 10L, "Inscrito", BigDecimal.ZERO)));
        service.concluir(2L, 10L);
        verify(pontuacao, times(1)).creditarPontos(anyLong(), any(), anyString());

        when(repo.buscar(3L)).thenReturn(Optional.of(desafio(3L, "Planejado", BigDecimal.ZERO)));
        assertThatThrownBy(() -> service.concluir(3L, 10L)).isInstanceOf(ValidationException.class);
        when(repo.buscarParticipante(1L, 11L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.concluir(1L, 11L)).isInstanceOf(NotFoundException.class);
        when(repo.buscarParticipante(1L, 12L)).thenReturn(Optional.of(new ParticipanteView(1L, 12L, "Desistente", BigDecimal.ZERO)));
        assertThatThrownBy(() -> service.concluir(1L, 12L)).isInstanceOf(ConflictException.class);
    }

    @Test
    void alteraStatusEDesistenciaRetornamEstadoAtualizado() {
        when(repo.buscar(1L)).thenReturn(Optional.of(desafio(1L, "Planejado", BigDecimal.ZERO)), Optional.of(desafio(1L, "Em andamento", BigDecimal.ZERO)));
        assertThat(service.alterarStatus(1L, "Em andamento").status()).isEqualTo("Em andamento");
        assertThatThrownBy(() -> service.alterarStatus(1L, "Nada")).isInstanceOf(ValidationException.class);

        when(repo.buscar(2L)).thenReturn(Optional.of(desafio(2L, "Planejado", BigDecimal.ZERO)));
        when(repo.buscarParticipante(2L, 10L)).thenReturn(Optional.of(new ParticipanteView(2L, 10L, "Inscrito", BigDecimal.ZERO)),
                Optional.of(new ParticipanteView(2L, 10L, "Desistente", BigDecimal.ZERO)));
        assertThat(service.desistir(2L, 10L).status()).isEqualTo("Desistente");
        verify(repo).atualizarParticipante(2L, 10L, "Desistente", BigDecimal.ZERO);
    }

    private static CreateDesafioCommand comando(LocalDate inicio, LocalDate fim) {
        return new CreateDesafioCommand("Desafio", "desc", inicio, fim, BigDecimal.TEN);
    }

    private static DesafioView desafio(long id, String status, BigDecimal recompensa) {
        return new DesafioView(id, 1L, "Desafio", "desc", LocalDate.now(), LocalDate.now().plusDays(3), recompensa, status, 0);
    }

    private static ParticipanteView participante(String status) {
        return new ParticipanteView(1L, 10L, status, BigDecimal.ZERO);
    }

    private static ColaboradorView colaborador(long id, long empresa, boolean ativo) {
        return new ColaboradorView(id, empresa, 1L, "Time", "Ana", "ana@x.com", "Analista", "Pleno", LocalDate.now(), ativo);
    }
}
