package br.com.dama.intelligence.analytics.api;

import java.math.BigDecimal;

/**
 * RF14/RF36 — comparação entre equipes (departamentos) DENTRO de uma empresa, numa janela de tempo.
 * A posição vem da {@code pontuacaoPorPessoa} (soma das pontuações dos membros ÷ membros ativos), para não favorecer equipes grandes;
 * {@code nota} vai de 0 a 10 (10 = a maior pontuação por pessoa da empresa).
 */
public record RankingDepartamentoView(
        Long departamentoId,
        String departamentoNome,
        long colaboradoresAtivos,
        long tarefasEntregues,
        BigDecimal pontuacaoTotal,
        BigDecimal pontuacaoPorPessoa,
        BigDecimal nota,
        long posicao
) {
}
