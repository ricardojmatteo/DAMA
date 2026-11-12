package br.com.dama.intelligence.auth.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Troca da própria senha: exige a senha atual. */
public record AlterarSenhaCommand(
        @NotBlank(message = "senhaAtual é obrigatória") String senhaAtual,
        @NotBlank(message = "novaSenha é obrigatória") @Size(min = 8, max = 72, message = "novaSenha deve ter entre 8 e 72 caracteres") String novaSenha
) {
}
