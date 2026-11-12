package br.com.dama.intelligence.orcamento.internal;

import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.orcamento.api.OrcamentoFacade;
import br.com.dama.intelligence.orcamento.api.OrcamentoIaView;
import br.com.dama.intelligence.orcamento.api.UpsertOrcamentoCommand;
import br.com.dama.intelligence.organizacao.api.OrganizacaoFacade;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.error.ValidationException;
import br.com.dama.intelligence.shared.security.EscopoEmpresa;
import br.com.dama.intelligence.shared.security.UsuarioAtual;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
class OrcamentoService implements OrcamentoFacade {

    private final OrcamentoRepository repo;
    private final OrganizacaoFacade organizacao;
    private final AuditoriaFacade auditoria;
    private final EscopoEmpresa escopo;
    private final Clock clock;

    OrcamentoService(OrcamentoRepository repo, OrganizacaoFacade organizacao, AuditoriaFacade auditoria,
                     EscopoEmpresa escopo, Clock clock) {
        this.repo = repo;
        this.organizacao = organizacao;
        this.auditoria = auditoria;
        this.escopo = escopo;
        this.clock = clock;
    }

    @Override
    public List<OrcamentoIaView> listar(Long empresaId, LocalDate mes) {
        exigirEmpresa(empresaId);
        LocalDate referencia = (mes == null ? LocalDate.now(clock) : mes).withDayOfMonth(1);
        return repo.listar(empresaId, referencia);
    }

    @Override
    @Transactional
    public OrcamentoIaView salvar(Long empresaId, UpsertOrcamentoCommand comando) {
        exigirEmpresa(empresaId);
        if (!organizacao.departamentoPertenceAEmpresa(comando.departamentoId(), empresaId)) {
            throw new ValidationException("O departamento informado não pertence a esta empresa.");
        }
        boolean faixaIncompleta = (comando.previstoInferior() == null) != (comando.previstoSuperior() == null);
        if (faixaIncompleta) {
            throw new ValidationException("Informe previstoInferior e previstoSuperior juntos, ou nenhum dos dois.");
        }
        if (comando.previstoInferior() != null && comando.previstoInferior().compareTo(comando.previstoSuperior()) > 0) {
            throw new ValidationException("previstoInferior não pode ser maior que previstoSuperior.");
        }
        if (comando.previstoInferior() != null && comando.consumoPrevisto() == null) {
            throw new ValidationException("A faixa de previsão exige consumoPrevisto.");
        }
        BigDecimal gasto = comando.gastoAtual() == null ? BigDecimal.ZERO : comando.gastoAtual();
        OrcamentoIaView salvo = repo.salvar(empresaId, comando.departamentoId(), comando.mesReferencia().withDayOfMonth(1),
                comando.valorOrcado(), gasto, comando.consumoPrevisto(), comando.previstoInferior(), comando.previstoSuperior(),
                comando.origemPrevisao());
        auditoria.registrar(empresaId, UsuarioAtual.id(), "orcamento_ia", salvo.orcamentoId().toString(), "REGISTRO", null);
        return salvo;
    }

    private void exigirEmpresa(Long empresaId) {
        if (!organizacao.existeEmpresa(empresaId)) {
            throw new NotFoundException("Empresa inexistente: " + empresaId);
        }
        escopo.exigir(empresaId);
    }
}
