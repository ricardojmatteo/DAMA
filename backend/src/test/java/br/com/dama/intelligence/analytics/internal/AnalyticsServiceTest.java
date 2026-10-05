package br.com.dama.intelligence.analytics.internal;

import br.com.dama.intelligence.analytics.api.*;
import br.com.dama.intelligence.analytics.internal.AnaliseGestaoRegras.ColaboradorSemAtividade;
import br.com.dama.intelligence.analytics.internal.AnaliseGestaoRegras.ValorIndicador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnalyticsServiceTest {
    private AnalyticsRepository repo;
    private AlertaGestaoClient alertas;
    private AnalyticsService service;

    @BeforeEach
    void setUp() {
        repo = mock(AnalyticsRepository.class);
        alertas = mock(AlertaGestaoClient.class);
        when(repo.existeEmpresa(1L)).thenReturn(true);
        service = new AnalyticsService(repo, Clock.fixed(Instant.parse("2026-10-05T12:00:00Z"), ZoneOffset.UTC), alertas);
    }

    @Test
    void consultasExigemEmpresaERetornamZeroQuandoNaoHaVisaoConsolidada() {
        assertThatThrownBy(() -> service.rankingColaboradores(2L)).isInstanceOf(br.com.dama.intelligence.shared.error.NotFoundException.class);
        when(repo.visaoConsolidada(1L)).thenReturn(Optional.empty());
        assertThat(service.visaoConsolidada(1L)).isEqualTo(new VisaoConsolidadaView(1L, 0, 0, 0, 0, BigDecimal.ZERO));
        RankingDepartamentoView departamento = new RankingDepartamentoView(2L, "TI", 2, BigDecimal.TEN, BigDecimal.TEN, 1);
        RankingColaboradorView colaborador = new RankingColaboradorView(3L, "Ana", 2L, BigDecimal.TEN, 1);
        when(repo.rankingDepartamentos(1L)).thenReturn(List.of(departamento));
        when(repo.rankingColaboradores(1L)).thenReturn(List.of(colaborador));
        when(repo.indicadoresPorDepartamento(1L, null)).thenReturn(List.of());
        assertThat(service.rankingDepartamentos(1L)).containsExactly(departamento);
        assertThat(service.rankingColaboradores(1L)).containsExactly(colaborador);
        assertThat(service.indicadoresPorDepartamento(1L, null)).isEmpty();
    }

    @Test
    void analiseAgrupaSomenteValoresDaMesmaSerieEOrdenaPorSeveridade() {
        ValorIndicador atual = valor(1L, 10L, null, LocalDate.of(2026, 10, 1), "70");
        ValorIndicador anterior = valor(1L, 10L, null, LocalDate.of(2026, 9, 1), "90");
        ValorIndicador outraSerie = valor(2L, 10L, null, LocalDate.of(2026, 10, 1), "1");
        when(repo.ultimosDoisValoresPorSerie(1L)).thenReturn(List.of(atual, anterior, outraSerie));
        when(repo.metasAbertas(1L)).thenReturn(List.of());
        when(repo.equipes(1L)).thenReturn(List.of());
        when(repo.colaboradoresSemAtividade(1L, LocalDate.of(2026, 9, 5))).thenReturn(List.of(new ColaboradorSemAtividade(9L, "Bruno", 2L)));
        AlertaGestaoView baixa = alerta("SEM_ATIVIDADE_RECENTE", "BAIXA");
        AlertaGestaoView alta = alerta("INDICADOR_EM_QUEDA", "ALTA");
        AlertaGestaoView media = alerta("META_EM_RISCO", "MEDIA");
        when(alertas.avaliar(any())).thenReturn(List.of(baixa, alta, media));

        AnaliseGestaoView resultado = service.analisarGestao(1L);

        assertThat(resultado.dataReferencia()).isEqualTo(LocalDate.of(2026, 10, 5));
        assertThat(resultado.alertas()).containsExactly(alta, media, baixa);
        assertThat(resultado.totalPorSeveridade()).containsEntry("ALTA", 1L).containsEntry("MEDIA", 1L).containsEntry("BAIXA", 1L);
        var captor = org.mockito.ArgumentCaptor.forClass(AlertaGestaoClient.EntradaAnalise.class);
        verify(alertas).avaliar(captor.capture());
        assertThat(captor.getValue().tendencias()).hasSize(1);
        assertThat(captor.getValue().tendencias().get(0).anterior()).isEqualTo(anterior);
    }

    private static ValorIndicador valor(Long indicador, Long colaborador, Long departamento, LocalDate data, String valor) {
        return new ValorIndicador(indicador, "Qualidade", "%", true, colaborador, "Ana", departamento, null, data, new BigDecimal(valor));
    }

    private static AlertaGestaoView alerta(String tipo, String severidade) {
        return new AlertaGestaoView(tipo, severidade, tipo, "d", "r", null, null, null);
    }
}
