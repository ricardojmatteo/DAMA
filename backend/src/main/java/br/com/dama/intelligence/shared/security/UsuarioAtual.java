package br.com.dama.intelligence.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Quem está autenticado na requisição atual (ver {@link JwtAuthenticationFilter}). */
public final class UsuarioAtual {

    /** Identidade colocada no SecurityContext depois de validar o token. */
    public record Principal(Long colaboradorId, Long empresaId, String nome) {
    }

    private UsuarioAtual() {
    }

    private static Principal principal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Principal p) {
            return p;
        }
        return null;
    }

    /** Id do colaborador autenticado, ou null fora de uma requisição autenticada. */
    public static Long id() {
        Principal p = principal();
        return p == null ? null : p.colaboradorId();
    }

    /** Empresa do colaborador autenticado, ou null fora de uma requisição autenticada. */
    public static Long empresaId() {
        Principal p = principal();
        return p == null ? null : p.empresaId();
    }

    /** true quando o usuário tem a permissão PLATAFORMA_ADMINISTRAR (operador da plataforma, enxerga todas as empresas). */
    public static boolean administraPlataforma() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> EscopoEmpresa.PERMISSAO_PLATAFORMA.equals(a.getAuthority()));
    }
}
