package br.com.dama.intelligence.indicador.internal;

import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorView;
import br.com.dama.intelligence.indicador.api.*;
import br.com.dama.intelligence.organizacao.api.OrganizacaoFacade;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.error.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class IndicadorServiceTest {
    private IndicadorRepository repo;
    private ColaboradorFacade colaboradores;
    private OrganizacaoFacade organizacao;
    private IndicadorService service;

    @BeforeEach
    void setUp() {
        repo = mock(IndicadorRepository.class);
        colaboradores = mock(ColaboradorFacade.class);
        organizacao = mock(OrganizacaoFacade.class);
        service = new IndicadorService(repo, colaboradores, organizacao, mock(AuditoriaFacade.class));
    }

    @Test
    void criarAplicaMaiorMelhorComoPadraoEExigeEmpresa() {
        when(organizacao.existeEmpresa(1L)).thenReturn(true);
        when(repo.inserirIndicador(1L, "Entrega", "d", "%", true)).thenReturn(3L);
        when(repo.buscar(3L)).thenReturn(Optional.of(indicador(3L, true)));
        assertThat(service.criar(1L, new CreateIndicadorCommand("Entrega", "d", "%", null)).maiorMelhor()).isTrue();
        assertThatThrownBy(() -> service.listarPorEmpresa(2L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void registrarValorExigeExatamenteUmDonoEQueElePertençaAEmpresa() {
        when(repo.buscar(3L)).thenReturn(Optional.of(indicador(3L, true)));
        RegistrarValorCommand nenhum = new RegistrarValorCommand(null, null, LocalDate.now(), BigDecimal.ONE, null);
        RegistrarValorCommand ambos = new RegistrarValorCommand(10L, 2L, LocalDate.now(), BigDecimal.ONE, null);
        assertThatThrownBy(() -> service.registrarValor(3L, nenhum)).isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> service.registrarValor(3L, ambos)).isInstanceOf(ValidationException.class);

        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 2L));
        assertThatThrownBy(() -> service.registrarValor(3L, new RegistrarValorCommand(10L, null, LocalDate.now(), BigDecimal.ONE, 99L)))
                .isInstanceOf(ValidationException.class).hasMessageContaining("não pertence");
        when(organizacao.departamentoPertenceAEmpresa(2L, 1L)).thenReturn(false);
        assertThatThrownBy(() -> service.registrarValor(3L, new RegistrarValorCommand(null, 2L, LocalDate.now(), BigDecimal.ONE, 99L)))
                .isInstanceOf(ValidationException.class).hasMessageContaining("não pertence");
    }

    @Test
    void registrarValorAceitaColaboradorOuDepartamentoEHistoricoValidaPeriodo() {
        when(repo.buscar(3L)).thenReturn(Optional.of(indicador(3L, false)));
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador(10L, 1L));
        when(repo.inserirValor(3L, 10L, null, LocalDate.of(2026, 1, 1), BigDecimal.TEN, 99L)).thenReturn(8L);
        assertThat(service.registrarValor(3L, new RegistrarValorCommand(10L, null, LocalDate.of(2026, 1, 1), BigDecimal.TEN, 99L)).indicadorValorId())
                .isEqualTo(8L);

        when(organizacao.departamentoPertenceAEmpresa(2L, 1L)).thenReturn(true);
        when(repo.inserirValor(3L, null, 2L, LocalDate.of(2026, 1, 2), BigDecimal.ONE, null)).thenReturn(9L);
        assertThat(service.registrarValor(3L, new RegistrarValorCommand(null, 2L, LocalDate.of(2026, 1, 2), BigDecimal.ONE, null)).departamentoId())
                .isEqualTo(2L);
        assertThatThrownBy(() -> service.historico(3L, null, null, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1)))
                .isInstanceOf(ValidationException.class);
        when(repo.historico(3L, 10L, null, null, null)).thenReturn(List.of());
        assertThat(service.historico(3L, 10L, null, null, null)).isEmpty();
    }

    @Test
    void consultasDelegamEIndicadorAusenteDa404() {
        when(repo.indicadorPertenceAEmpresa(3L, 1L)).thenReturn(true);
        assertThat(service.pertenceAEmpresa(3L, 1L)).isTrue();
        when(repo.buscar(3L)).thenReturn(Optional.of(indicador(3L, false)));
        assertThat(service.maiorMelhor(3L)).isFalse();
        when(repo.ultimosValoresDoColaborador(10L)).thenReturn(List.of());
        assertThat(service.ultimosValoresDoColaborador(10L)).isEmpty();
        when(repo.buscar(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.maiorMelhor(9L)).isInstanceOf(NotFoundException.class);
    }

    private static IndicadorView indicador(long id, boolean maiorMelhor) {
        return new IndicadorView(id, 1L, "Entrega", "d", "%", maiorMelhor, true);
    }

    private static ColaboradorView colaborador(long id, long empresa) {
        return new ColaboradorView(id, empresa, 1L, "Time", "Ana", "ana@x.com", "Analista", "Pleno", LocalDate.now(), true);
    }
}
