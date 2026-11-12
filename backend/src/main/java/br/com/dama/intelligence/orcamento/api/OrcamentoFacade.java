package br.com.dama.intelligence.orcamento.api;

import java.time.LocalDate;
import java.util.List;

/** Orçamento de IA por centro de custo: alimenta o alerta ORCAMENTO_IA_EM_RISCO e a tela de análises. */
public interface OrcamentoFacade {

    /** Orçamentos da empresa no mês (dia 1). Sem mês, o mês corrente. */
    List<OrcamentoIaView> listar(Long empresaId, LocalDate mes);

    OrcamentoIaView salvar(Long empresaId, UpsertOrcamentoCommand comando);
}
