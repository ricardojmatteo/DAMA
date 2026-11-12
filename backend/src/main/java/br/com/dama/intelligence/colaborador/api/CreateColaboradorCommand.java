package br.com.dama.intelligence.colaborador.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

/** RF01 — cadastrar colaborador. */
public record CreateColaboradorCommand(
        @NotNull(message = "departamentoId é obrigatório") Long departamentoId,
        @NotBlank(message = "nome é obrigatório") String nome,
        @NotBlank(message = "email é obrigatório") @Email(message = "email inválido") String email,
        String cargo,
        String senioridade,
        @NotNull(message = "dataAdmissao é obrigatória") @PastOrPresent(message = "dataAdmissao não pode ser futura") LocalDate dataAdmissao,
        /** Senha inicial (opcional, 8 a 72 caracteres). Sem ela o colaborador só entra depois que um administrador definir uma. */
        @Size(min = 8, max = 72, message = "senha deve ter entre 8 e 72 caracteres") String senha
) {
}
