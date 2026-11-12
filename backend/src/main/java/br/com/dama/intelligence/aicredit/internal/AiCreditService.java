package br.com.dama.intelligence.aicredit.internal;

import br.com.dama.intelligence.aicredit.api.*;
import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorView;
import br.com.dama.intelligence.shared.error.ValidationException;
import br.com.dama.intelligence.shared.event.DesempenhoAtualizadoEvent;
import br.com.dama.intelligence.shared.security.EscopoEmpresa;
import br.com.dama.intelligence.shared.security.UsuarioAtual;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/** RF22–RF26 — carteira de AI Credits: transações, saldo derivado e níveis de autonomia. */
@Service
class AiCreditService implements AiCreditFacade {

    private final AiCreditRepository repo;
    private final ColaboradorFacade colaboradores;
    private final AuditoriaFacade auditoria;
    private final ApplicationEventPublisher eventos;
    private final EscopoEmpresa escopo;

    AiCreditService(AiCreditRepository repo, ColaboradorFacade colaboradores, AuditoriaFacade auditoria,
                    ApplicationEventPublisher eventos, EscopoEmpresa escopo) {
        this.repo = repo;
        this.colaboradores = colaboradores;
        this.auditoria = auditoria;
        this.eventos = eventos;
        this.escopo = escopo;
    }

    @Override
    @Transactional
    public TransacaoCreditoView lancarTransacao(Long colaboradorId, LancarTransacaoCommand comando) {
        ColaboradorView colaborador = colaboradores.buscarPorId(colaboradorId);
        escopo.exigir(colaborador.empresaId());

        TransacaoCreditoView transacao;
        if ("DEBITO".equals(comando.tipo())) {
            repo.bloquearSaldo(colaboradorId);
            if (repo.saldo(colaboradorId).compareTo(comando.quantidade()) < 0) {
                throw new ValidationException("Saldo de AI Credits insuficiente para este débito.");
            }
            transacao = repo.inserirTransacao(colaboradorId, null, null, "DEBITO", comando.quantidade(), comando.motivo());
        } else {
            if (!colaborador.ativo()) {
                throw new ValidationException("O colaborador está inativo.");
            }
            transacao = repo.inserirTransacao(colaboradorId, null, null, "CREDITO", comando.quantidade(), comando.motivo());
        }

        auditoria.registrar(colaborador.empresaId(), UsuarioAtual.id(), "ai_credit_transacao",
                transacao.transacaoId().toString(), comando.tipo(), null);
        if ("CREDITO".equals(comando.tipo())) {
            eventos.publishEvent(new DesempenhoAtualizadoEvent(colaboradorId));
        }
        return transacao;
    }

    /** Chamado pelo módulo de atividades dentro da transação do registro; quem chama já validou colaborador e escopo. */
    @Override
    @Transactional
    public TransacaoCreditoView creditarPorRegra(Long colaboradorId, Long regraId, Long registroId, BigDecimal quantidade, String motivo) {
        ColaboradorView colaborador = colaboradores.buscarPorId(colaboradorId);
        TransacaoCreditoView transacao = repo.inserirTransacao(colaboradorId, regraId, registroId, "CREDITO", quantidade, motivo);
        auditoria.registrar(colaborador.empresaId(), UsuarioAtual.id(), "ai_credit_transacao",
                transacao.transacaoId().toString(), "CREDITO_AUTOMATICO", null);
        return transacao;
    }

    @Override
    public List<TransacaoCreditoView> historico(Long colaboradorId) {
        exigirDaEmpresa(colaboradorId);
        return repo.historico(colaboradorId);
    }

    @Override
    public SaldoCreditosView saldo(Long colaboradorId) {
        exigirDaEmpresa(colaboradorId);
        BigDecimal saldo = repo.saldo(colaboradorId);
        return new SaldoCreditosView(colaboradorId, saldo, repo.nivelParaSaldo(saldo).orElse(null));
    }

    @Override
    public List<NivelAutonomiaView> listarNiveis() {
        return repo.listarNiveis();
    }

    @Override
    @Transactional
    public NivelAutonomiaView criarNivel(CreateNivelCommand comando) {
        return repo.inserirNivel(comando.nome(), comando.creditosMinimos(), comando.ordem(), comando.descricao());
    }

    private void exigirDaEmpresa(Long colaboradorId) {
        escopo.exigir(colaboradores.buscarPorId(colaboradorId).empresaId()); // 404 se não existir, 403 se de outra empresa
    }
}
