package br.com.dama.intelligence.auth.api;

import java.time.Instant;

/** Token de acesso emitido no login: vai em {@code Authorization: Bearer <token>} nas demais chamadas. */
public record TokenView(String token, String tipo, Instant expiraEm, Long colaboradorId) {
}
