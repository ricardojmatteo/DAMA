package br.com.dama.intelligence.pontuacao.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Tarefa concluída no Jira. {@code chave} é o identificador lá (ex.: DAMA-123) e não se repete para o mesmo colaborador.
 * {@code urgencia} é BAIXA, MEDIA (padrão), ALTA ou CRITICA; {@code tokensIa} são os tokens gastos na tarefa (padrão 0);
 * {@code entregueEm} assume o momento do registro.
 */
public record RegistrarTarefaCommand(
        @NotBlank(message = "chave é obrigatória") String chave,
        String descricao,
        @NotNull(message = "storyPoints é obrigatório") @Positive(message = "storyPoints deve ser maior que zero") BigDecimal storyPoints,
        @Pattern(regexp = "(?i)BAIXA|MEDIA|ALTA|CRITICA", message = "urgencia deve ser BAIXA, MEDIA, ALTA ou CRITICA") String urgencia,
        @PositiveOrZero(message = "tokensIa não pode ser negativo") Long tokensIa,
        LocalDateTime entregueEm
) {
}
