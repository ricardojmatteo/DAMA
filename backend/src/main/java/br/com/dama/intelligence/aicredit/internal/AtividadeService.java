package br.com.dama.intelligence.aicredit.internal;

import br.com.dama.intelligence.aicredit.api.*;
import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorView;
import br.com.dama.intelligence.organizacao.api.OrganizacaoFacade;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.error.ValidationException;
import br.com.dama.intelligence.shared.event.DesempenhoAtualizadoEvent;
import br.com.dama.intelligence.shared.security.EscopoEmpresa;
import br.com.dama.intelligence.shared.security.UsuarioAtual;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/** RF19/RF20. Orquestra o registro de atividade: valida, grava e pede os créditos às regras e à carteira. */
@Service
class AtividadeService implements AtividadeFacade {

    private final AtividadeRepository repo;
    private final RegraCreditoFacade regras;
    private final AiCreditFacade creditos;
    private final ColaboradorFacade colaboradores;
    private final OrganizacaoFacade organizacao;
    private final AuditoriaFacade auditoria;
    private final ApplicationEventPublisher eventos;
    private final EscopoEmpresa escopo;

    AtividadeService(AtividadeRepository repo, RegraCreditoFacade regras, AiCreditFacade creditos,
                     ColaboradorFacade colaboradores, OrganizacaoFacade organizacao, AuditoriaFacade auditoria,
                     ApplicationEventPublisher eventos, EscopoEmpresa escopo) {
        this.repo = repo;
        this.regras = regras;
        this.creditos = creditos;
        this.colaboradores = colaboradores;
        this.organizacao = organizacao;
        this.auditoria = auditoria;
        this.eventos = eventos;
        this.escopo = escopo;
    }

    @Override
    public List<AtividadeProdutivaView> listarAtividades(Long empresaId) {
        exigirEmpresa(empresaId);
        return repo.listarAtividades(empresaId);
    }

    @Override
    @Transactional
    public AtividadeProdutivaView criarAtividade(Long empresaId, CreateAtividadeCommand comando) {
        exigirEmpresa(empresaId);
        AtividadeProdutivaView criada = repo.inserirAtividade(empresaId, comando.nome(), comando.descricao());
        auditoria.registrar(empresaId, UsuarioAtual.id(), "atividade_produtiva", criada.atividadeId().toString(), "CRIACAO", null);
        return criada;
    }

    @Override
    public boolean atividadePertenceAEmpresa(Long atividadeId, Long empresaId) {
        return repo.buscarAtividade(atividadeId).map(a -> a.empresaId().equals(empresaId)).orElse(false);
    }

    /**
     * A atividade é elegível quando existe ao menos uma regra ativa para ela (RF21). Para cada regra aplicável é lançado
     * um CREDITO (RF22) com o valor fixo da regra, uma vez por registro: a quantidade da atividade não multiplica o
     * crédito. O critério textual da regra é documentação para gestores; não é interpretado automaticamente.
     */
    @Override
    @Transactional
    public RegistroAtividadeResultadoView registrarAtividade(Long colaboradorId, RegistrarAtividadeCommand comando) {
        ColaboradorView colaborador = colaboradores.buscarPorId(colaboradorId);
        escopo.exigir(colaborador.empresaId());
        if (!colaborador.ativo()) {
            throw new ValidationException("O colaborador está inativo.");
        }

        AtividadeProdutivaView atividade = repo.buscarAtividade(comando.atividadeId())
                .orElseThrow(() -> new NotFoundException("Atividade inexistente: " + comando.atividadeId()));
        if (!atividade.empresaId().equals(colaborador.empresaId())) {
            throw new ValidationException("A atividade informada não pertence à empresa do colaborador.");
        }
        if (!atividade.ativa()) {
            throw new ValidationException("A atividade informada está inativa.");
        }

        BigDecimal quantidade = comando.quantidade() != null ? comando.quantidade() : BigDecimal.ONE;
        AtividadeRegistroView registro = repo.inserirRegistro(atividade.atividadeId(), colaboradorId, quantidade);
        auditoria.registrar(colaborador.empresaId(), UsuarioAtual.id(), "atividade_registro",
                registro.registroId().toString(), "CRIACAO", null);

        List<RegraCreditoView> aplicaveis = regras.regrasAtivasDaAtividade(atividade.atividadeId());
        BigDecimal totalConcedido = BigDecimal.ZERO;
        for (RegraCreditoView regra : aplicaveis) {
            creditos.creditarPorRegra(colaboradorId, regra.regraId(), registro.registroId(), regra.creditosConcedidos(),
                    "Atividade elegível: " + atividade.nome());
            totalConcedido = totalConcedido.add(regra.creditosConcedidos());
        }

        eventos.publishEvent(new DesempenhoAtualizadoEvent(colaboradorId));
        return new RegistroAtividadeResultadoView(registro, !aplicaveis.isEmpty(), totalConcedido);
    }

    @Override
    public List<AtividadeRegistroView> listarRegistros(Long colaboradorId) {
        escopo.exigir(colaboradores.buscarPorId(colaboradorId).empresaId()); // 404 se não existir, 403 se de outra empresa
        return repo.listarRegistros(colaboradorId);
    }

    private void exigirEmpresa(Long empresaId) {
        if (!organizacao.existeEmpresa(empresaId)) {
            throw new NotFoundException("Empresa inexistente: " + empresaId);
        }
        escopo.exigir(empresaId);
    }
}
