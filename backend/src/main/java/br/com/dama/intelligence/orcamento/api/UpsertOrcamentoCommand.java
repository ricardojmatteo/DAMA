package br.com.dama.intelligence.orcamento.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Cria ou atualiza o orçamento de IA de um departamento no mês (um registro por departamento e mês).
 * A previsão é opcional: quem a carrega é o notebook de demanda, via API, informando a origem.
 */
public record UpsertOrcamentoCommand(
        @NotNull(message = "departamentoId é obrigatório") Long departamentoId,
        @NotNull(message = "mesReferencia é obrigatório") LocalDate mesReferencia,
        @NotNull(message = "valorOrcado é obrigatório") @PositiveOrZero(message = "valorOrcado não pode ser negativo") BigDecimal valorOrcado,
        @PositiveOrZero(message = "gastoAtual não pode ser negativo") BigDecimal gastoAtual,
        @PositiveOrZero(message = "consumoPrevisto não pode ser negativo") BigDecimal consumoPrevisto,
        @PositiveOrZero(message = "previstoInferior não pode ser negativo") BigDecimal previstoInferior,
        @PositiveOrZero(message = "previstoSuperior não pode ser negativo") BigDecimal previstoSuperior,
        String origemPrevisao
) {
}
