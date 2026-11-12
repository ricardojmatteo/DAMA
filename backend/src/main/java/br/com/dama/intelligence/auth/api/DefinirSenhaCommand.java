package br.com.dama.intelligence.auth.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Definição ou redefinição da senha de um colaborador por quem administra pessoas (RF02). */
public record DefinirSenhaCommand(
        @NotBlank(message = "senha é obrigatória") @Size(min = 8, max = 72, message = "senha deve ter entre 8 e 72 caracteres") String senha
) {
}
