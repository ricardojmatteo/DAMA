package br.com.dama.intelligence.auditoria.internal;

import br.com.dama.intelligence.auditoria.api.AuditoriaEventoView;
import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.shared.security.EscopoEmpresa;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class AuditoriaService implements AuditoriaFacade {

    private final AuditoriaRepository repo;
    private final EscopoEmpresa escopo;

    AuditoriaService(AuditoriaRepository repo, EscopoEmpresa escopo) {
        this.repo = repo;
        this.escopo = escopo;
    }

    @Override
    public void registrar(Long empresaId, Long colaboradorId, String entidade, String entidadeId, String acao, String detalhesJson) {
        repo.inserir(empresaId, colaboradorId, entidade, entidadeId, acao, detalhesJson == null ? "{}" : detalhesJson);
    }

    @Override
    public List<AuditoriaEventoView> listar(Long empresaId, String entidade, String entidadeId) {
        escopo.exigir(empresaId);
        return repo.listar(empresaId, entidade, entidadeId);
    }
}
