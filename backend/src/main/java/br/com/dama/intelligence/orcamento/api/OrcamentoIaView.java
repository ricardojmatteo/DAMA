package br.com.dama.intelligence.orcamento.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Orçamento de IA de um departamento (centro de custo) no mês. gastoAtual é retrato; consumoPrevisto e a faixa de 80%
 * (previstoInferior/previstoSuperior) vêm do modelo de demanda e podem ser nulos. situacao: DENTRO, RISCO (faixa
 * superior ultrapassa) ou ACIMA (projeção central ultrapassa).
 */
public record OrcamentoIaView(
        Long orcamentoId,
        Long empresaId,
        Long departamentoId,
        String departamentoNome,
        LocalDate mesReferencia,
        BigDecimal valorOrcado,
        BigDecimal gastoAtual,
        BigDecimal consumoPrevisto,
        BigDecimal previstoInferior,
        BigDecimal previstoSuperior,
        BigDecimal projetado,
        BigDecimal percentualProjetado,
        String situacao,
        String origemPrevisao,
        LocalDateTime atualizadoEm
) {
}
