package br.com.dama.intelligence.colaborador.api;

/** O que o módulo auth precisa para autenticar: identidade, hash da senha (pode ser nulo) e se está ativo. */
public record CredencialView(Long colaboradorId, Long empresaId, String nome, String senhaHash, boolean ativo) {
}
