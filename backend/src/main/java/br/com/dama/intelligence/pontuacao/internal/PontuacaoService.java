package br.com.dama.intelligence.pontuacao.internal;

import br.com.dama.intelligence.aicredit.api.AtividadeFacade;
import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorView;
import br.com.dama.intelligence.organizacao.api.OrganizacaoFacade;
import br.com.dama.intelligence.pontuacao.api.*;
import br.com.dama.intelligence.shared.error.ConflictException;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.security.EscopoEmpresa;
import br.com.dama.intelligence.shared.error.ValidationException;
import br.com.dama.intelligence.shared.event.DesempenhoAtualizadoEvent;
import br.com.dama.intelligence.shared.security.UsuarioAtual;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
class PontuacaoService implements PontuacaoFacade {

    private final PontuacaoRepository repo;
    private final ColaboradorFacade colaboradores;
    private final OrganizacaoFacade organizacao;
    private final AtividadeFacade atividades;
    private final AuditoriaFacade auditoria;
    private final ApplicationEventPublisher eventos;
    private final EscopoEmpresa escopo;

    PontuacaoService(PontuacaoRepository repo, ColaboradorFacade colaboradores, OrganizacaoFacade organizacao,
                     AtividadeFacade atividades, AuditoriaFacade auditoria, ApplicationEventPublisher eventos,
                     EscopoEmpresa escopo) {
        this.repo = repo;
        this.colaboradores = colaboradores;
        this.organizacao = organizacao;
        this.atividades = atividades;
        this.auditoria = auditoria;
        this.eventos = eventos;
        this.escopo = escopo;
    }

    @Override
    public List<RegraPontuacaoView> listarRegras(Long empresaId) {
        exigirEmpresa(empresaId);
        return repo.listarRegras(empresaId);
    }

    @Override
    @Transactional
    public RegraPontuacaoView criarRegra(Long empresaId, CreateRegraPontuacaoCommand comando) {
        exigirEmpresa(empresaId);
        if (comando.atividadeId() != null && !atividades.atividadePertenceAEmpresa(comando.atividadeId(), empresaId)) {
            throw new ValidationException("A atividade informada não existe ou não pertence a esta empresa.");
        }
        RegraPontuacaoView criada = repo.inserirRegra(empresaId, comando.atividadeId(), comando.nome(), comando.pontos());
        auditoria.registrar(empresaId, UsuarioAtual.id(), "regra_pontuacao", criada.regraId().toString(), "CRIACAO", null);
        return criada;
    }

    @Override
    @Transactional
    public RegraPontuacaoView alterarRegraAtiva(Long regraId, boolean ativa) {
        RegraPontuacaoView regra = repo.buscarRegra(regraId)
                .orElseThrow(() -> new NotFoundException("Regra de pontuação inexistente: " + regraId));
        escopo.exigir(regra.empresaId());
        repo.atualizarRegraAtiva(regraId, ativa);
        auditoria.registrar(regra.empresaId(), UsuarioAtual.id(), "regra_pontuacao", regraId.toString(),
                ativa ? "ATIVACAO" : "DESATIVACAO", null);
        return repo.buscarRegra(regraId).orElseThrow();
    }

    @Override
    @Transactional
    public PontuacaoView lancar(Long colaboradorId, LancarPontosCommand comando) {
        ColaboradorView colaborador = colaboradores.buscarPorId(colaboradorId);
        escopo.exigir(colaborador.empresaId());
        exigirAtivo(colaborador);

        RegraPontuacaoView regra = repo.buscarRegra(comando.regraId())
                .orElseThrow(() -> new NotFoundException("Regra de pontuação inexistente: " + comando.regraId()));
        if (!regra.empresaId().equals(colaborador.empresaId())) {
            throw new ValidationException("A regra informada não pertence à empresa do colaborador.");
        }
        if (!regra.ativa()) {
            throw new ValidationException("A regra informada está inativa.");
        }

        String descricao = comando.descricao() != null && !comando.descricao().isBlank() ? comando.descricao() : regra.nome();
        PontuacaoView lancada = repo.inserirPontuacao(colaboradorId, regra.regraId(), regra.pontos(), descricao);
        auditoria.registrar(colaborador.empresaId(), UsuarioAtual.id(), "pontuacao", lancada.pontuacaoId().toString(), "LANCAMENTO", null);
        eventos.publishEvent(new DesempenhoAtualizadoEvent(colaboradorId));
        return lancada;
    }

    @Override
    public PontuacaoResumoView resumo(Long colaboradorId) {
        escopo.exigir(colaboradores.buscarPorId(colaboradorId).empresaId()); // 404 se não existir, 403 se de outra empresa
        return new PontuacaoResumoView(colaboradorId, repo.total(colaboradorId), repo.historico(colaboradorId));
    }

    @Override
    @Transactional
    public TarefaEntregueView registrarTarefa(Long colaboradorId, RegistrarTarefaCommand comando) {
        ColaboradorView colaborador = colaboradores.buscarPorId(colaboradorId);
        escopo.exigir(colaborador.empresaId());
        exigirAtivo(colaborador);
        String urgencia = comando.urgencia() == null ? "MEDIA" : comando.urgencia().toUpperCase();
        TarefaEntregueView tarefa;
        try {
            tarefa = repo.inserirTarefa(colaboradorId, comando.chave().trim(), comando.descricao(), comando.storyPoints(),
                    urgencia, comando.tokensIa() == null ? 0 : comando.tokensIa(),
                    Boolean.TRUE.equals(comando.retrabalho()), comando.entregueEm());
        } catch (DuplicateKeyException e) {
            throw new ConflictException("A tarefa " + comando.chave().trim() + " já foi registrada para este colaborador.");
        }
        auditoria.registrar(colaborador.empresaId(), UsuarioAtual.id(), "tarefa_entregue", tarefa.tarefaId().toString(), "CRIACAO", null);
        eventos.publishEvent(new DesempenhoAtualizadoEvent(colaboradorId));
        return tarefa;
    }

    @Override
    public List<TarefaEntregueView> listarTarefas(Long colaboradorId) {
        escopo.exigir(colaboradores.buscarPorId(colaboradorId).empresaId()); // 404 se não existir, 403 se de outra empresa
        return repo.listarTarefas(colaboradorId);
    }

    @Override
    @Transactional
    public BonusGestorView concederBonus(Long colaboradorId, ConcederBonusCommand comando) {
        ColaboradorView colaborador = colaboradores.buscarPorId(colaboradorId);
        escopo.exigir(colaborador.empresaId());
        exigirAtivo(colaborador);
        BonusGestorView bonus = repo.inserirBonus(colaboradorId, UsuarioAtual.id(), comando.pontos(), comando.motivo().trim());
        auditoria.registrar(colaborador.empresaId(), UsuarioAtual.id(), "bonus_gestor", bonus.bonusId().toString(), "CRIACAO", null);
        eventos.publishEvent(new DesempenhoAtualizadoEvent(colaboradorId));
        return bonus;
    }

    @Override
    public List<BonusGestorView> listarBonus(Long colaboradorId) {
        escopo.exigir(colaboradores.buscarPorId(colaboradorId).empresaId()); // 404 se não existir, 403 se de outra empresa
        return repo.listarBonus(colaboradorId);
    }

    @Override
    @Transactional
    public PontuacaoView creditarPontos(Long colaboradorId, BigDecimal pontos, String descricao) {
        ColaboradorView colaborador = colaboradores.buscarPorId(colaboradorId);
        PontuacaoView lancada = repo.inserirPontuacao(colaboradorId, null, pontos, descricao);
        auditoria.registrar(colaborador.empresaId(), UsuarioAtual.id(), "pontuacao", lancada.pontuacaoId().toString(),
                "LANCAMENTO_AUTOMATICO", null);
        eventos.publishEvent(new DesempenhoAtualizadoEvent(colaboradorId));
        return lancada;
    }

    private void exigirEmpresa(Long empresaId) {
        if (!organizacao.existeEmpresa(empresaId)) {
            throw new NotFoundException("Empresa inexistente: " + empresaId);
        }
        escopo.exigir(empresaId);
    }

    private void exigirAtivo(ColaboradorView colaborador) {
        if (!colaborador.ativo()) {
            throw new ValidationException("O colaborador está inativo.");
        }
    }
}
