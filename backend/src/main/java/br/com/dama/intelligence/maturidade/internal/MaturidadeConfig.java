package br.com.dama.intelligence.maturidade.internal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Parâmetros do AMS (application.yml, chaves dama.ams.*); a justificativa dos padrões está em docs/produto/ams.md. */
@Component
class MaturidadeConfig {

    private final MaturidadeRegras.Pesos pesos;
    private final int janelaDias;
    private final int capacitacaoValidadeMeses;
    private final double tokensPorPonto;
    private final double franquiaGratis;

    MaturidadeConfig(@Value("${dama.ams.pesos.adocao:20}") double adocao,
                     @Value("${dama.ams.pesos.capacitacao:20}") double capacitacao,
                     @Value("${dama.ams.pesos.governanca:20}") double governanca,
                     @Value("${dama.ams.pesos.produtividade:20}") double produtividade,
                     @Value("${dama.ams.pesos.eficiencia:20}") double eficiencia,
                     @Value("${dama.ams.janela-dias:90}") int janelaDias,
                     @Value("${dama.ams.capacitacao-validade-meses:12}") int capacitacaoValidadeMeses,
                     @Value("${dama.ranking.tokens-por-ponto:20000}") double tokensPorPonto,
                     @Value("${dama.ranking.franquia-gratis:0.25}") double franquiaGratis) {
        if (janelaDias <= 0 || capacitacaoValidadeMeses <= 0) {
            throw new IllegalArgumentException("dama.ams: janela-dias e capacitacao-validade-meses devem ser positivos.");
        }
        this.pesos = new MaturidadeRegras.Pesos(adocao, capacitacao, governanca, produtividade, eficiencia);
        this.janelaDias = janelaDias;
        this.capacitacaoValidadeMeses = capacitacaoValidadeMeses;
        this.tokensPorPonto = tokensPorPonto;
        this.franquiaGratis = franquiaGratis;
    }

    MaturidadeRegras.Pesos pesos() {
        return pesos;
    }

    int janelaDias() {
        return janelaDias;
    }

    int capacitacaoValidadeMeses() {
        return capacitacaoValidadeMeses;
    }

    double tokensPorPonto() {
        return tokensPorPonto;
    }

    double franquiaGratis() {
        return franquiaGratis;
    }
}
