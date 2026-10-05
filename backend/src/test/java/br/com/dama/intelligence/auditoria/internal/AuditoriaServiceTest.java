package br.com.dama.intelligence.auditoria.internal;

import br.com.dama.intelligence.auditoria.api.AuditoriaEventoView;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuditoriaServiceTest {
    @Test
    void registraJsonVazioQuandoDetalhesSaoNulosEMantemJsonInformado() {
        AuditoriaRepository repo = mock(AuditoriaRepository.class);
        AuditoriaService service = new AuditoriaService(repo);
        service.registrar(1L, 2L, "meta", "3", "CRIACAO", null);
        service.registrar(1L, 2L, "meta", "3", "EDICAO", "{\"antes\":1}");
        verify(repo).inserir(1L, 2L, "meta", "3", "CRIACAO", "{}");
        verify(repo).inserir(1L, 2L, "meta", "3", "EDICAO", "{\"antes\":1}");
        AuditoriaEventoView evento = new AuditoriaEventoView(1L, 2L, "meta", "3", "CRIACAO", "{}", LocalDateTime.now());
        when(repo.listar(1L, "meta", "3")).thenReturn(List.of(evento));
        assertThat(service.listar(1L, "meta", "3")).containsExactly(evento);
    }
}
