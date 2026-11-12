package br.com.dama.intelligence.perfil.internal;

import br.com.dama.intelligence.colaborador.api.ColaboradorFacade;
import br.com.dama.intelligence.perfil.api.*;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.error.ValidationException;
import br.com.dama.intelligence.shared.security.EscopoEmpresa;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
class PerfilService implements PerfilFacade {

    private final PerfilRepository repo;
    private final ColaboradorFacade colaboradores;
    private final EscopoEmpresa escopo;

    PerfilService(PerfilRepository repo, ColaboradorFacade colaboradores, EscopoEmpresa escopo) {
        this.repo = repo;
        this.colaboradores = colaboradores;
        this.escopo = escopo;
    }

    @Override
    public List<PerfilView> listarPerfis() {
        return repo.listarPerfis();
    }

    @Override
    public PerfilView criarPerfil(CreatePerfilCommand comando) {
        List<Long> permissaoIds = repo.resolverPermissoes(comando.permissoes());
        if (permissaoIds.size() != comando.permissoes().size()) {
            throw new ValidationException("Um ou mais códigos de permissão informados não existem.");
        }
        Long perfilId = repo.inserirPerfil(comando.nome(), comando.descricao());
        repo.vincularPermissoes(perfilId, permissaoIds);
        return new PerfilView(perfilId, comando.nome(), comando.descricao(), repo.permissoesDoPerfil(perfilId));
    }

    @Override
    public List<PermissaoView> listarPermissoes() {
        return repo.listarPermissoes();
    }

    @Override
    public void atribuirPerfil(Long colaboradorId, Long perfilId) {
        exigirColaboradorDaEmpresa(colaboradorId);
        if (!repo.existePerfil(perfilId)) {
            throw new NotFoundException("Perfil inexistente: " + perfilId);
        }
        repo.atribuirPerfil(colaboradorId, perfilId);
    }

    @Override
    public void removerPerfil(Long colaboradorId, Long perfilId) {
        exigirColaboradorDaEmpresa(colaboradorId);
        repo.removerPerfil(colaboradorId, perfilId);
    }

    /** Leitura interna (sessão, filtro de autenticação): sem escopo. A rota da API passa por {@link #perfisDoColaboradorDaEmpresa}. */
    @Override
    public List<PerfilView> perfisDoColaborador(Long colaboradorId) {
        return repo.perfisDoColaborador(colaboradorId);
    }

    @Override
    public List<PerfilView> perfisDoColaboradorDaEmpresa(Long colaboradorId) {
        exigirColaboradorDaEmpresa(colaboradorId);
        return repo.perfisDoColaborador(colaboradorId);
    }

    private void exigirColaboradorDaEmpresa(Long colaboradorId) {
        escopo.exigir(colaboradores.buscarPorId(colaboradorId).empresaId()); // 404 se não existir, 403 se de outra empresa
    }

    @Override
    public Set<String> permissoesDoColaborador(Long colaboradorId) {
        return repo.permissoesDoColaborador(colaboradorId);
    }
}
