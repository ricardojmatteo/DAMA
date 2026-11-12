package br.com.dama.intelligence.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Credenciais de entrada (US01). */
public record LoginCommand(
        @NotBlank(message = "email é obrigatório") @Email(message = "email inválido") String email,
        @NotBlank(message = "senha é obrigatória") String senha
) {
}
