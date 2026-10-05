package br.com.dama.intelligence.reconhecimento.internal;

import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorView;
import br.com.dama.intelligence.organizacao.api.OrganizacaoFacade;
import br.com.dama.intelligence.reconhecimento.api.CreateReconhecimentoCommand;
import br.com.dama.intelligence.reconhecimento.api.ReconhecimentoView;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.error.ValidationException;
import br.com.dama.intelligence.shared.event.DesempenhoAtualizadoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReconhecimentoServiceTest {
    private ReconhecimentoRepository repo;
    private ColaboradorFacade colaboradores;
    private OrganizacaoFacade organizacao;
    private ApplicationEventPublisher eventos;
    private ReconhecimentoService service;

    @BeforeEach
    void setUp() {
        repo = mock(ReconhecimentoRepository.class);
        colaboradores = mock(ColaboradorFacade.class);
        organizacao = mock(OrganizacaoFacade.class);
        eventos = mock(ApplicationEventPublisher.class);
        service = new ReconhecimentoService(repo, colaboradores, organizacao, mock(AuditoriaFacade.class), eventos);
    }

    @Test
    void listarExigeEmpresaEMantemConsultaDoColaborador() {
        assertThatThrownBy(() -> service.listarPorEmpresa(1L)).isInstanceOf(NotFoundException.class);
        when(organizacao.existeEmpresa(1L)).thenReturn(true);
        when(repo.listarPorEmpresa(1L)).thenReturn(List.of());
        assertThat(service.listarPorEmpresa(1L)).isEmpty();
        when(repo.listarDoColaborador(10L)).thenReturn(List.of());
        assertThat(service.listarDoColaborador(10L)).isEmpty();
        verify(colaboradores).buscarPorId(10L);
    }

    @Test
    void criarExigeEmpresaColaboradorDoMesmoTenantEAtivoEDisparaEvento() {
        CreateReconhecimentoCommand comando = new CreateReconhecimentoCommand(10L, "Destaque", "Entrega");
        assertThatThrownBy(() -> service.criar(1L, comando)).isInstanceOf(NotFoundException.class);
        when(organizacao.existeEmpresa(1L)).thenReturn(true);
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 2L, true));
        assertThatThrownBy(() -> service.criar(1L, comando)).isInstanceOf(ValidationException.class).hasMessageContaining("não pertence");
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, false));
        assertThatThrownBy(() -> service.criar(1L, comando)).isInstanceOf(ValidationException.class).hasMessageContaining("inativo");
        ReconhecimentoView criado = new ReconhecimentoView(4L, 1L, 10L, null, "Destaque", "Entrega", LocalDateTime.now());
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L, true));
        when(repo.inserir(eq(1L), eq(10L), isNull(), eq("Destaque"), eq("Entrega"))).thenReturn(criado);
        assertThat(service.criar(1L, comando)).isEqualTo(criado);
        verify(eventos).publishEvent(new DesempenhoAtualizadoEvent(10L));
    }

    private static ColaboradorView colaborador(long id, long empresa, boolean ativo) {
        return new ColaboradorView(id, empresa, 1L, "TI", "Ana", "ana@x.com", "Dev", "Pleno", LocalDate.now(), ativo);
    }
}
