package br.com.dama.intelligence.aicredit.api;

import java.math.BigDecimal;
import java.util.List;

/**
 * Carteira de AI Credits: transações (RF22/RF24), saldo (RF23) e níveis de autonomia (RF25/RF26).
 * Muda quando muda como créditos são movimentados ou como o saldo vira autonomia.
 */
public interface AiCreditFacade {

    /** Lançamento manual por um gestor: CREDITO ou DEBITO (o débito exige saldo). */
    TransacaoCreditoView lancarTransacao(Long colaboradorId, LancarTransacaoCommand comando);

    /** Crédito automático gerado por uma regra ao registrar uma atividade (RF22). Chamado pelo módulo de atividades. */
    TransacaoCreditoView creditarPorRegra(Long colaboradorId, Long regraId, Long registroId, BigDecimal quantidade, String motivo);

    List<TransacaoCreditoView> historico(Long colaboradorId);

    SaldoCreditosView saldo(Long colaboradorId);

    List<NivelAutonomiaView> listarNiveis();

    NivelAutonomiaView criarNivel(CreateNivelCommand comando);
}
