package br.com.dama.intelligence.pontuacao.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TarefaEntregueView(Long tarefaId, Long colaboradorId, String chave, String descricao, BigDecimal storyPoints,
                                 String urgencia, long tokensIa, LocalDateTime entregueEm) {
}
