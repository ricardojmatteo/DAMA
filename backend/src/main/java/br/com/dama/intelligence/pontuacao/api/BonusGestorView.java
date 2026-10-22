package br.com.dama.intelligence.pontuacao.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BonusGestorView(Long bonusId, Long colaboradorId, Long concedidoPor, BigDecimal pontos, String motivo,
                              LocalDateTime registradoEm) {
}
