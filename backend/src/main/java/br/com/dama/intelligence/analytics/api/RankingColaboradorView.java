package br.com.dama.intelligence.analytics.api;

import java.math.BigDecimal;

/**
 * RF08/RF28 — ranking de colaboradores dentro da empresa, para uma janela de tempo (docs/requisitos/ranking.md).
 * {@code pontuacao = pontosTarefas − penalidadeIa + bonusGestor} (piso 0); {@code nota} vai de 0 a 10 (10 = a maior pontuação da empresa).
 */
public record RankingColaboradorView(
        Long colaboradorId,
        String nome,
        Long departamentoId,
        long tarefasEntregues,
        BigDecimal pontosTarefas,
        BigDecimal penalidadeIa,
        BigDecimal bonusGestor,
        BigDecimal pontuacao,
        BigDecimal nota,
        long posicao
) {
}
