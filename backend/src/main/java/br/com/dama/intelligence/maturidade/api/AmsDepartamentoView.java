package br.com.dama.intelligence.maturidade.api;

import java.math.BigDecimal;

/**
 * AI Maturity Score de um departamento (0 a 100) e suas cinco dimensões, cada uma também de 0 a 100.
 * faixa: ADOCAO_BAIXA (0–39), EM_DESENVOLVIMENTO (40–69) ou AVANCADO (70–100).
 */
public record AmsDepartamentoView(
        Long departamentoId,
        String departamentoNome,
        long colaboradoresAtivos,
        BigDecimal adocao,
        BigDecimal capacitacao,
        BigDecimal governanca,
        BigDecimal produtividade,
        BigDecimal eficiencia,
        BigDecimal ams,
        String faixa,
        String leitura,
        Evidencias evidencias
) {
    /** Contagens brutas que geraram as dimensões, para o gestor conferir o número. */
    public record Evidencias(long comUsoDeIa, long capacitados, long tarefasEntregues, long tarefasConformes,
                             BigDecimal storyPointsPorColaborador, BigDecimal fracaoMediaPenalizada) {
    }
}
