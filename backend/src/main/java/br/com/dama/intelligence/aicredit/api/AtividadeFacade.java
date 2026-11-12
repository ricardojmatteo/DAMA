package br.com.dama.intelligence.aicredit.api;

import java.util.List;

/**
 * Catálogo de atividades produtivas (RF19) e registro do que cada colaborador realizou (RF20).
 * Muda quando muda o que a empresa reconhece como atividade; não conhece saldo nem níveis de autonomia.
 */
public interface AtividadeFacade {

    List<AtividadeProdutivaView> listarAtividades(Long empresaId);

    AtividadeProdutivaView criarAtividade(Long empresaId, CreateAtividadeCommand comando);

    /** Usado por outros módulos (pontuacao) para validar uma atividade sem conhecer o repositório. */
    boolean atividadePertenceAEmpresa(Long atividadeId, Long empresaId);

    /** Registra a atividade e, se houver regra ativa (RF21), concede os AI Credits (RF22) via {@link AiCreditFacade}. */
    RegistroAtividadeResultadoView registrarAtividade(Long colaboradorId, RegistrarAtividadeCommand comando);

    List<AtividadeRegistroView> listarRegistros(Long colaboradorId);
}
