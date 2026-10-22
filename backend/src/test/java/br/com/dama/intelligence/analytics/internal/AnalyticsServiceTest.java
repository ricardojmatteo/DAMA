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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AnalyticsServiceTest {
    private AnalyticsRepository repo;
    private AlertaGestaoClient alertas;
    private AnalyticsService service;
    private static final RankingConfig RANKING = new RankingConfig(20000, 0.25, 0.15, 30);

    @BeforeEach
    void setUp() {
        repo = mock(AnalyticsRepository.class);
        alertas = mock(AlertaGestaoClient.class);
        when(repo.existeEmpresa(1L)).thenReturn(true);
        service = new AnalyticsService(repo, Clock.fixed(Instant.parse("2026-10-05T12:00:00Z"), ZoneOffset.UTC), alertas, RANKING);
    }

    @Test
    void consultasExigemEmpresaERetornamZeroQuandoNaoHaVisaoConsolidada() {
        assertThatThrownBy(() -> service.rankingColaboradores(2L, null, null)).isInstanceOf(br.com.dama.intelligence.shared.error.NotFoundException.class);
        when(repo.visaoConsolidada(1L)).thenReturn(Optional.empty());
        assertThat(service.visaoConsolidada(1L)).isEqualTo(new VisaoConsolidadaView(1L, 0, 0, 0, 0, BigDecimal.ZERO));
        when(repo.departamentos(1L)).thenReturn(List.of(new RankingRegras.Departamento(2L, "TI")));
        when(repo.colaboradoresAtivos(1L)).thenReturn(List.of(new RankingRegras.Colaborador(3L, "Ana", 2L)));
        when(repo.tarefasEntregues(eq(1L), any(), any())).thenReturn(List.of(
                new RankingRegras.Tarefa(3L, BigDecimal.TEN, "BAIXA", 0)));
        when(repo.indicadoresPorDepartamento(1L, null)).thenReturn(List.of());
        assertThat(service.rankingDepartamentos(1L, null, null)).extracting(RankingDepartamentoView::departamentoNome, RankingDepartamentoView::posicao)
                .containsExactly(tuple("TI", 1L));
        assertThat(service.rankingColaboradores(1L, null, null)).extracting(RankingColaboradorView::nome, RankingColaboradorView::pontuacao)
                .containsExactly(tuple("Ana", new BigDecimal("10.00")));
        assertThat(service.indicadoresPorDepartamento(1L, null)).isEmpty();
    }

    @Test
    void analiseAgrupaSomenteValoresDaMesmaSerieEOrdenaPorSeveridade() {
        ValorIndicador atual = valor(1L, 10L, null, LocalDate.of(2026, 10, 1), "70");
        ValorIndicador anterior = valor(1L, 10L, null, LocalDate.of(2026, 9, 1), "90");
        ValorIndicador outraSerie = valor(2L, 10L, null, LocalDate.of(2026, 10, 1), "1");
        when(repo.ultimosDoisValoresPorSerie(1L)).thenReturn(List.of(atual, anterior, outraSerie));
        when(repo.metasAbertas(1L)).thenReturn(List.of());
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

    @Test
    void janelaPadraoSaoOsUltimos30DiasTerminandoHojeEPeriodoInvertidoEhRecusado() {
        service.rankingColaboradores(1L, null, null);
        verify(repo).tarefasEntregues(1L, LocalDate.of(2026, 9, 6), LocalDate.of(2026, 10, 5));
        verify(repo).bonusDoGestor(1L, LocalDate.of(2026, 9, 6), LocalDate.of(2026, 10, 5));

        service.rankingColaboradores(1L, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
        verify(repo).tarefasEntregues(1L, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        assertThatThrownBy(() -> service.rankingDepartamentos(1L, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1)))
                .isInstanceOf(br.com.dama.intelligence.shared.error.ValidationException.class);
    }

    private static ValorIndicador valor(Long indicador, Long colaborador, Long departamento, LocalDate data, String valor) {
        return new ValorIndicador(indicador, "Qualidade", "%", true, colaborador, "Ana", departamento, null, data, new BigDecimal(valor));
    }

    private static AlertaGestaoView alerta(String tipo, String severidade) {
        return new AlertaGestaoView(tipo, severidade, tipo, "d", "r", null, null, null);
    }
}
