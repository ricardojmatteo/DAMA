package br.com.dama.intelligence.analytics.internal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Parâmetros do ranking (application.yml, chaves dama.ranking.*); os padrões estão em docs/requisitos/ranking.md. */
@Component
class RankingConfig {

    private final RankingRegras.Parametros parametros;
    private final int janelaPadraoDias;

    RankingConfig(@Value("${dama.ranking.tokens-por-ponto:20000}") double tokensPorPonto,
                  @Value("${dama.ranking.franquia-gratis:0.25}") double franquiaGratis,
                  @Value("${dama.ranking.bonus-maximo:0.15}") double bonusMaximo,
                  @Value("${dama.ranking.janela-padrao-dias:30}") int janelaPadraoDias) {
        if (tokensPorPonto <= 0 || janelaPadraoDias <= 0 || franquiaGratis < 0 || franquiaGratis >= 1 || bonusMaximo < 0) {
            throw new IllegalArgumentException("dama.ranking inválido: tokens-por-ponto e janela devem ser positivos, "
                    + "franquia-gratis deve estar em [0, 1) e bonus-maximo não pode ser negativo.");
        }
        this.parametros = new RankingRegras.Parametros(tokensPorPonto, franquiaGratis, bonusMaximo);
        this.janelaPadraoDias = janelaPadraoDias;
    }

    RankingRegras.Parametros parametros() {
        return parametros;
    }

    int janelaPadraoDias() {
        return janelaPadraoDias;
    }
}
