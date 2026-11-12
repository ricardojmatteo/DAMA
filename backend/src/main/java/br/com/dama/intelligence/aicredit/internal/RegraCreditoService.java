package br.com.dama.intelligence.aicredit.internal;

import br.com.dama.intelligence.aicredit.api.*;
import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.organizacao.api.OrganizacaoFacade;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.error.ValidationException;
import br.com.dama.intelligence.shared.security.EscopoEmpresa;
import br.com.dama.intelligence.shared.security.UsuarioAtual;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** RF21 — regras de elegibilidade (RN04). */
@Service
class RegraCreditoService implements RegraCreditoFacade {

    private final RegraCreditoRepository repo;
    private final AtividadeRepository atividades;
    private final OrganizacaoFacade organizacao;
    private final AuditoriaFacade auditoria;
    private final EscopoEmpresa escopo;

    RegraCreditoService(RegraCreditoRepository repo, AtividadeRepository atividades, OrganizacaoFacade organizacao,
                        AuditoriaFacade auditoria, EscopoEmpresa escopo) {
        this.repo = repo;
        this.atividades = atividades;
        this.organizacao = organizacao;
        this.auditoria = auditoria;
        this.escopo = escopo;
    }

    @Override
    public List<RegraCreditoView> listarRegras(Long empresaId) {
        exigirEmpresa(empresaId);
        return repo.listarRegras(empresaId);
    }

    @Override
    @Transactional
    public RegraCreditoView criarRegra(Long empresaId, CreateRegraCreditoCommand comando) {
        exigirEmpresa(empresaId);
        boolean atividadeDaEmpresa = atividades.buscarAtividade(comando.atividadeId())
                .map(a -> a.empresaId().equals(empresaId)).orElse(false);
        if (!atividadeDaEmpresa) {
            throw new ValidationException("A atividade informada não existe ou não pertence a esta empresa.");
        }
        RegraCreditoView criada = repo.inserirRegra(empresaId, comando.atividadeId(), comando.nome(),
                comando.criterioElegibilidade(), comando.creditosConcedidos());
        auditoria.registrar(empresaId, UsuarioAtual.id(), "ai_credit_regra", criada.regraId().toString(), "CRIACAO", null);
        return criada;
    }

    @Override
    public ElegibilidadeView avaliarElegibilidade(Long atividadeId) {
        AtividadeProdutivaView atividade = atividades.buscarAtividade(atividadeId)
                .orElseThrow(() -> new NotFoundException("Atividade inexistente: " + atividadeId));
        escopo.exigir(atividade.empresaId());
        List<RegraCreditoView> regras = repo.regrasAtivasDaAtividade(atividadeId);
        return new ElegibilidadeView(atividadeId, !regras.isEmpty(), regras);
    }

    @Override
    public List<RegraCreditoView> regrasAtivasDaAtividade(Long atividadeId) {
        return repo.regrasAtivasDaAtividade(atividadeId);
    }

    private void exigirEmpresa(Long empresaId) {
        if (!organizacao.existeEmpresa(empresaId)) {
            throw new NotFoundException("Empresa inexistente: " + empresaId);
        }
        escopo.exigir(empresaId);
    }
}
