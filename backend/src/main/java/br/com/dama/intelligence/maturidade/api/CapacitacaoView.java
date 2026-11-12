package br.com.dama.intelligence.maturidade.api;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CapacitacaoView(Long capacitacaoId, Long colaboradorId, String nome, BigDecimal cargaHoraria,
                              LocalDate concluidaEm, Long registradoPor) {
}
