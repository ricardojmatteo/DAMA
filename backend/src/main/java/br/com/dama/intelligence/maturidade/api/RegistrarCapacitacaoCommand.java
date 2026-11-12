package br.com.dama.intelligence.maturidade.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Treinamento em IA concluído por um colaborador. */
public record RegistrarCapacitacaoCommand(
        @NotBlank(message = "nome é obrigatório") String nome,
        @Positive(message = "cargaHoraria deve ser maior que zero") BigDecimal cargaHoraria,
        @NotNull(message = "concluidaEm é obrigatória") @PastOrPresent(message = "concluidaEm não pode ser futura") LocalDate concluidaEm
) {
}
