package br.com.dama.intelligence.analytics.api;

import java.time.LocalDate;
import java.util.List;

/**
 * Módulo analytics readaptado: substitui a versão anterior, que comparava
 * empresas-cliente entre si (sem correspondência em nenhum RF). Aqui toda
 * comparação acontece DENTRO de uma única empresa — colaboradores e
 * departamentos, nunca entre empresas (RF13, RF14, RF15, RF36; também usado
 * por RF08/RF28 de ranking de colaboradores).
 */
public interface AnalyticsFacade {
    VisaoConsolidadaView visaoConsolidada(Long empresaId);

    /**
     * Ranking de equipes na janela [inicio, fim], por pontuação por pessoa (docs/requisitos/ranking.md).
     * Datas nulas assumem os últimos dias da janela padrão, terminando hoje.
     */
    List<RankingDepartamentoView> rankingDepartamentos(Long empresaId, LocalDate inicio, LocalDate fim);

    /** Ranking de colaboradores na janela [inicio, fim], por pontos ajustados pelo uso de IA. */
    List<RankingColaboradorView> rankingColaboradores(Long empresaId, LocalDate inicio, LocalDate fim);

    /** RF33/RF36: valor mais recente de cada indicador por equipe; indicadorId opcional para comparar um indicador só. */
    List<IndicadorDepartamentoView> indicadoresPorDepartamento(Long empresaId, Long indicadorId);

    /** RF15/US24: metas vencidas ou em risco, indicadores em queda, equipes abaixo da média e colaboradores sem atividade. */
    AnaliseGestaoView analisarGestao(Long empresaId);
}
