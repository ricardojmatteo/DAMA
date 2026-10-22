package br.com.dama.intelligence.pontuacao.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/** Bônus do gestor/coordenador. No ranking vale no máximo 15% dos pontos líquidos das tarefas do período. */
public record ConcederBonusCommand(
        @NotNull(message = "pontos é obrigatório") @Positive(message = "pontos deve ser maior que zero") BigDecimal pontos,
        @NotBlank(message = "motivo é obrigatório") String motivo
) {
}
