package br.com.dama.intelligence.maturidade.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * AMS da empresa: média ponderada pelos colaboradores ativos dos departamentos, mais o detalhe por departamento.
 * pesos: participação de cada dimensão no total (somam 100). janela: período das tarefas e atividades considerado.
 */
public record AmsEmpresaView(
        Long empresaId,
        LocalDate janelaInicio,
        LocalDate janelaFim,
        Map<String, BigDecimal> pesos,
        BigDecimal ams,
        String faixa,
        List<AmsDepartamentoView> departamentos
) {
}
