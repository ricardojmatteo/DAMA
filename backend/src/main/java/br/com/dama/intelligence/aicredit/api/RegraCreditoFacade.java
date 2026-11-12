package br.com.dama.intelligence.aicredit.api;

import java.util.List;

/**
 * Regras de elegibilidade (RF21, RN04): quais atividades geram AI Credits e quantos.
 * Muda quando a política de créditos da empresa muda.
 */
public interface RegraCreditoFacade {

    List<RegraCreditoView> listarRegras(Long empresaId);

    RegraCreditoView criarRegra(Long empresaId, CreateRegraCreditoCommand comando);

    ElegibilidadeView avaliarElegibilidade(Long atividadeId);

    /** Regras ativas de uma atividade, na ordem de criação. Usado ao registrar uma atividade. */
    List<RegraCreditoView> regrasAtivasDaAtividade(Long atividadeId);
}
