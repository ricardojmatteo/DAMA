package br.com.dama.intelligence.auth.api;

/** Contrato público do módulo auth (ADR-0010): login por e-mail e senha, troca e redefinição de senha. */
public interface AuthFacade {

    /** Valida e-mail e senha e emite o token. Falha com 401 em credencial inválida ou colaborador desligado. */
    TokenView login(LoginCommand comando);

    /** Troca a senha do usuário autenticado, conferindo a atual. */
    void alterarPropriaSenha(AlterarSenhaCommand comando);

    /** Define ou redefine a senha de um colaborador da empresa do usuário (ou de qualquer empresa, para o operador). */
    void definirSenha(Long colaboradorId, DefinirSenhaCommand comando);
}
