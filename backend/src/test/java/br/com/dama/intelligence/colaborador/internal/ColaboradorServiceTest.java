package br.com.dama.intelligence.colaborador.internal;

import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.colaborador.api.*;
import br.com.dama.intelligence.shared.error.ConflictException;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.error.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ColaboradorServiceTest {
    private ColaboradorRepository repo;
    private ColaboradorService service;

    @BeforeEach
    void setUp() {
        repo = mock(ColaboradorRepository.class);
        service = new ColaboradorService(repo, mock(AuditoriaFacade.class));
    }

    @Test
    void listaEBuscaExigemExistencia() {
        when(repo.existeEmpresa(1L)).thenReturn(true);
        when(repo.listarPorEmpresa(1L)).thenReturn(List.of(colaborador(1L, 1L)));
        assertThat(service.listarPorEmpresa(1L)).hasSize(1);
        assertThatThrownBy(() -> service.listarPorEmpresa(2L)).isInstanceOf(NotFoundException.class);
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(colaborador(1L, 1L)));
        assertThat(service.buscarPorId(1L).nome()).isEqualTo("Ana");
        when(repo.buscarPorId(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarPorId(2L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void criarValidaEmpresaDepartamentoEEmailAntesDeAuditar() {
        CreateColaboradorCommand comando = criar(2L, "ana@x.com");
        assertThatThrownBy(() -> service.criar(1L, comando)).isInstanceOf(NotFoundException.class);
        when(repo.existeEmpresa(1L)).thenReturn(true);
        assertThatThrownBy(() -> service.criar(1L, comando)).isInstanceOf(ValidationException.class);
        when(repo.departamentoPertenceAEmpresa(2L, 1L)).thenReturn(true);
        when(repo.emailJaCadastrado(1L, "ana@x.com", null)).thenReturn(true);
        assertThatThrownBy(() -> service.criar(1L, comando)).isInstanceOf(ConflictException.class);
        when(repo.emailJaCadastrado(1L, "ana@x.com", null)).thenReturn(false);
        when(repo.inserir(eq(1L), eq(2L), anyString(), anyString(), any(), any(), any())).thenReturn(7L);
        when(repo.buscarPorId(7L)).thenReturn(Optional.of(colaborador(7L, 1L)));
        assertThat(service.criar(1L, comando).colaboradorId()).isEqualTo(7L);
    }

    @Test
    void atualizarEStatusValidamEDepoisRetornamEstadoAtual() {
        when(repo.buscarPorId(7L)).thenReturn(Optional.of(colaborador(7L, 1L)), Optional.of(colaborador(7L, 1L)), Optional.of(colaborador(7L, 1L)));
        UpdateColaboradorCommand update = new UpdateColaboradorCommand(3L, "Ana Nova", "nova@x.com", "Dev", "Senior");
        assertThatThrownBy(() -> service.atualizar(7L, update)).isInstanceOf(ValidationException.class);
        when(repo.departamentoPertenceAEmpresa(3L, 1L)).thenReturn(true);
        when(repo.emailJaCadastrado(1L, "nova@x.com", 7L)).thenReturn(true);
        assertThatThrownBy(() -> service.atualizar(7L, update)).isInstanceOf(ConflictException.class);
        when(repo.emailJaCadastrado(1L, "nova@x.com", 7L)).thenReturn(false);
        assertThat(service.atualizar(7L, update).nome()).isEqualTo("Ana");
        assertThat(service.alterarStatus(7L, false).ativo()).isTrue();
        verify(repo).alterarStatus(7L, false);
        when(repo.existe(7L)).thenReturn(true);
        assertThat(service.existe(7L)).isTrue();
    }

    private static CreateColaboradorCommand criar(long departamento, String email) {
        return new CreateColaboradorCommand(departamento, "Ana", email, "Dev", "Pleno", LocalDate.now());
    }

    private static ColaboradorView colaborador(long id, long empresa) {
        return new ColaboradorView(id, empresa, 2L, "TI", "Ana", "ana@x.com", "Dev", "Pleno", LocalDate.now(), true);
    }
}
