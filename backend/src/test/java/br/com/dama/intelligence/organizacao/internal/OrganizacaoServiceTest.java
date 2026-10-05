package br.com.dama.intelligence.organizacao.internal;

import br.com.dama.intelligence.organizacao.api.*;
import br.com.dama.intelligence.shared.error.ConflictException;
import br.com.dama.intelligence.shared.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrganizacaoServiceTest {
    private OrganizacaoRepository repo;
    private OrganizacaoService service;

    @BeforeEach
    void setUp() {
        repo = mock(OrganizacaoRepository.class);
        service = new OrganizacaoService(repo);
    }

    @Test
    void criaListaEConsultaEmpresasEDepartamentos() {
        when(repo.inserirEmpresa("DAMA", "TI", "Médio")).thenReturn(1L);
        assertThat(service.criarEmpresa(new CreateEmpresaCommand("DAMA", "TI", "Médio")))
                .isEqualTo(new EmpresaView(1L, "DAMA", "TI", "Médio"));
        when(repo.listarEmpresas()).thenReturn(List.of(new EmpresaView(1L, "DAMA", "TI", "Médio")));
        assertThat(service.listarEmpresas()).hasSize(1);
        when(repo.existeEmpresa(1L)).thenReturn(true);
        when(repo.listarDepartamentos(1L)).thenReturn(List.of(new DepartamentoView(2L, 1L, "TI")));
        assertThat(service.listarDepartamentos(1L)).hasSize(1);
        assertThatThrownBy(() -> service.listarDepartamentos(9L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void criaDepartamentoTrataDuplicidadeEEmpresaInexistente() {
        assertThatThrownBy(() -> service.criarDepartamento(1L, new CreateDepartamentoCommand("TI"))).isInstanceOf(NotFoundException.class);
        when(repo.existeEmpresa(1L)).thenReturn(true);
        when(repo.inserirDepartamento(1L, "TI")).thenThrow(new DuplicateKeyException("dup"));
        assertThatThrownBy(() -> service.criarDepartamento(1L, new CreateDepartamentoCommand("TI"))).isInstanceOf(ConflictException.class);
        reset(repo);
        when(repo.existeEmpresa(1L)).thenReturn(true);
        when(repo.inserirDepartamento(1L, "TI")).thenReturn(2L);
        assertThat(service.criarDepartamento(1L, new CreateDepartamentoCommand("TI"))).isEqualTo(new DepartamentoView(2L, 1L, "TI"));
    }

    @Test
    void delegaConsultasDePertencimentoEBuscaDepartamento() {
        when(repo.existeEmpresa(1L)).thenReturn(true);
        when(repo.departamentoPertenceAEmpresa(2L, 1L)).thenReturn(true);
        assertThat(service.existeEmpresa(1L)).isTrue();
        assertThat(service.departamentoPertenceAEmpresa(2L, 1L)).isTrue();
        when(repo.buscarDepartamento(2L)).thenReturn(Optional.of(new DepartamentoView(2L, 1L, "TI")));
        assertThat(service.buscarDepartamento(2L).nome()).isEqualTo("TI");
        when(repo.buscarDepartamento(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarDepartamento(9L)).isInstanceOf(NotFoundException.class);
    }
}
