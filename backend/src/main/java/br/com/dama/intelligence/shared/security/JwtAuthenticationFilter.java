package br.com.dama.intelligence.shared.security;

import br.com.dama.intelligence.colaborador.api.ColaboradorFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorView;
import br.com.dama.intelligence.perfil.api.PerfilFacade;
import br.com.dama.intelligence.shared.error.NotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Autenticação por token (ADR-0010): lê {@code Authorization: Bearer <jwt>}, valida assinatura e validade com o
 * {@link JwtService} e carrega do banco o colaborador e suas permissões. Colaborador desligado ou inexistente não
 * é autenticado, mesmo com token válido. Substitui o antigo cabeçalho X-Colaborador-Id (ADR-0006), que não provava
 * identidade.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String PREFIXO = "Bearer ";

    private final JwtService jwt;
    private final ColaboradorFacade colaboradores;
    private final PerfilFacade perfis;

    public JwtAuthenticationFilter(JwtService jwt, ColaboradorFacade colaboradores, PerfilFacade perfis) {
        this.jwt = jwt;
        this.colaboradores = colaboradores;
        this.perfis = perfis;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String cabecalho = request.getHeader("Authorization");
        if (cabecalho != null && cabecalho.startsWith(PREFIXO)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            autenticar(cabecalho.substring(PREFIXO.length()).trim());
        }
        chain.doFilter(request, response);
    }

    private void autenticar(String token) {
        Long colaboradorId;
        try {
            colaboradorId = jwt.validar(token);
        } catch (JwtException e) {
            log.debug("Token recusado: {}", e.getMessage());
            return; // segue sem autenticação; a cadeia de segurança devolve 401
        }
        ColaboradorView colaborador;
        try {
            colaborador = colaboradores.buscarPorId(colaboradorId);
        } catch (NotFoundException e) {
            return;
        }
        if (!colaborador.ativo()) {
            log.debug("Token de colaborador desligado ({}) recusado.", colaboradorId);
            return;
        }
        List<GrantedAuthority> authorities = perfis.permissoesDoColaborador(colaboradorId).stream()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
        var principal = new UsuarioAtual.Principal(colaboradorId, colaborador.empresaId(), colaborador.nome());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, authorities));
    }
}
