package br.com.dama.intelligence.shared.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * Emite e valida os tokens de acesso (JWT HS256, ADR-0010). O token carrega só a identidade (sub = id do colaborador)
 * e a validade; perfis e permissões são lidos do banco a cada requisição pelo {@link JwtAuthenticationFilter}, para
 * que desligar um colaborador ou trocar seu perfil tenha efeito imediato.
 */
@Component
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    static final String SEGREDO_PADRAO_DEV = "dama-intelligence-segredo-de-desenvolvimento-troque-em-producao";

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final Duration validade;
    private final Clock clock;

    public JwtService(@Value("${dama.auth.segredo:}") String segredo,
                      @Value("${dama.auth.validade-horas:8}") long validadeHoras,
                      Clock clock) {
        String chave = segredo == null || segredo.isBlank() ? SEGREDO_PADRAO_DEV : segredo;
        if (chave.equals(SEGREDO_PADRAO_DEV)) {
            log.warn("dama.auth.segredo não configurado: usando o segredo de desenvolvimento. Defina DAMA_JWT_SEGREDO em produção.");
        }
        if (chave.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("dama.auth.segredo precisa ter ao menos 32 bytes para HS256.");
        }
        SecretKey key = new SecretKeySpec(chave.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        this.decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        this.validade = Duration.ofHours(validadeHoras);
        this.clock = clock;
    }

    public record TokenEmitido(String token, Instant expiraEm) {
    }

    public TokenEmitido emitir(Long colaboradorId) {
        Instant agora = Instant.now(clock);
        Instant expira = agora.plus(validade);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("dama-intelligence")
                .subject(colaboradorId.toString())
                .issuedAt(agora)
                .expiresAt(expira)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return new TokenEmitido(encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue(), expira);
    }

    /** Id do colaborador do token, ou lança {@link JwtException} se inválido ou expirado. */
    public Long validar(String token) {
        Jwt jwt = decoder.decode(token);
        try {
            return Long.valueOf(jwt.getSubject());
        } catch (NumberFormatException e) {
            throw new BadJwtException("Token sem identificação válida do colaborador.");
        }
    }
}
