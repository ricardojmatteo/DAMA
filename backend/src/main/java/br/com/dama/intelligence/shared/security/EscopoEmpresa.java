package br.com.dama.intelligence.shared.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Autorização contextual (ADR-0011): além da permissão do perfil (@PreAuthorize), todo recurso pertence a uma
 * empresa, e o usuário só pode ler ou alterar recursos da própria empresa. A exceção é quem tem a permissão
 * {@value #PERMISSAO_PLATAFORMA} (perfil Administrador), que opera a plataforma para todas as empresas-cliente.
 *
 * Os services chamam {@link #exigir(Long)} depois de descobrir a empresa do recurso (pelo id da URL ou pela
 * entidade carregada). A violação vira 403, nunca 404: o recurso existe, mas não é desta empresa.
 */
@Component
public class EscopoEmpresa {

    public static final String PERMISSAO_PLATAFORMA = "PLATAFORMA_ADMINISTRAR";

    /** Lança 403 se a empresa informada não for a do usuário autenticado e ele não administrar a plataforma. */
    public void exigir(Long empresaId) {
        if (!permite(empresaId)) {
            throw new AccessDeniedException("O recurso pertence a outra empresa.");
        }
    }

    /**
     * true se o usuário autenticado pode acessar recursos desta empresa. Sem usuário autenticado (processamento interno
     * fora de uma requisição HTTP, como testes de unidade dos services) não há escopo a aplicar e o acesso é permitido:
     * toda rota /api/** já exige autenticação na cadeia de filtros, então este caso nunca ocorre numa requisição.
     */
    public boolean permite(Long empresaId) {
        if (UsuarioAtual.id() == null || UsuarioAtual.administraPlataforma()) {
            return true;
        }
        return Objects.equals(UsuarioAtual.empresaId(), empresaId);
    }

    /** Empresa que o usuário pode ver em listagens sem empresa na URL: a própria, ou null (todas) para o operador. */
    public Long empresaVisivel() {
        return UsuarioAtual.administraPlataforma() ? null : UsuarioAtual.empresaId();
    }
}
