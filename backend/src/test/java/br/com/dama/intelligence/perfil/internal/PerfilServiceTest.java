package br.com.dama.intelligence.perfil.internal;

import br.com.dama.intelligence.perfil.api.*;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.error.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PerfilServiceTest {
    private PerfilRepository repo;
    private PerfilService service;

    @BeforeEach
    void setUp() {
        repo = mock(PerfilRepository.class);
        service = new PerfilService(repo);
    }

    @Test
    void criarPerfilRejeitaPermissaoDesconhecidaECriaComPermissoesResolvidas() {
        CreatePerfilCommand comando = new CreatePerfilCommand("Gestor", "d", List.of("LER", "ESCREVER"));
        when(repo.resolverPermissoes(comando.permissoes())).thenReturn(List.of(1L));
        assertThatThrownBy(() -> service.criarPerfil(comando)).isInstanceOf(ValidationException.class);
        when(repo.resolverPermissoes(comando.permissoes())).thenReturn(List.of(1L, 2L));
        when(repo.inserirPerfil("Gestor", "d")).thenReturn(3L);
        when(repo.permissoesDoPerfil(3L)).thenReturn(List.of("LER", "ESCREVER"));
        assertThat(service.criarPerfil(comando).permissoes()).containsExactly("LER", "ESCREVER");
        verify(repo).vincularPermissoes(3L, List.of(1L, 2L));
    }

    @Test
    void atribuirExigePerfilExistenteEOsDemaisMetodosDelegam() {
        when(repo.existePerfil(1L)).thenReturn(false);
        assertThatThrownBy(() -> service.atribuirPerfil(9L, 1L)).isInstanceOf(NotFoundException.class);
        when(repo.existePerfil(1L)).thenReturn(true);
        service.atribuirPerfil(9L, 1L);
        service.removerPerfil(9L, 1L);
        verify(repo).atribuirPerfil(9L, 1L);
        verify(repo).removerPerfil(9L, 1L);
        when(repo.listarPerfis()).thenReturn(List.of());
        when(repo.listarPermissoes()).thenReturn(List.of());
        when(repo.perfisDoColaborador(9L)).thenReturn(List.of());
        when(repo.permissoesDoColaborador(9L)).thenReturn(Set.of("LER"));
        assertThat(service.listarPerfis()).isEmpty();
        assertThat(service.listarPermissoes()).isEmpty();
        assertThat(service.perfisDoColaborador(9L)).isEmpty();
        assertThat(service.permissoesDoColaborador(9L)).containsExactly("LER");
    }
}
