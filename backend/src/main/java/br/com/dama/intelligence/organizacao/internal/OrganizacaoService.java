package br.com.dama.intelligence.organizacao.internal;

import br.com.dama.intelligence.organizacao.api.*;
import br.com.dama.intelligence.shared.error.ConflictException;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.security.EscopoEmpresa;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class OrganizacaoService implements OrganizacaoFacade {

    private final OrganizacaoRepository repo;
    private final EscopoEmpresa escopo;

    OrganizacaoService(OrganizacaoRepository repo, EscopoEmpresa escopo) {
        this.repo = repo;
        this.escopo = escopo;
    }

    /** O operador da plataforma vê todas as empresas; os demais, só a própria (ADR-0011). */
    @Override
    public List<EmpresaView> listarEmpresas() {
        Long visivel = escopo.empresaVisivel();
        return visivel == null ? repo.listarEmpresas()
                : repo.listarEmpresas().stream().filter(e -> e.empresaId().equals(visivel)).toList();
    }

    @Override
    public EmpresaView criarEmpresa(CreateEmpresaCommand comando) {
        Long id = repo.inserirEmpresa(comando.nome(), comando.setor(), comando.porte());
        return new EmpresaView(id, comando.nome(), comando.setor(), comando.porte());
    }

    @Override
    public List<DepartamentoView> listarDepartamentos(Long empresaId) {
        if (!repo.existeEmpresa(empresaId)) {
            throw new NotFoundException("Empresa inexistente: " + empresaId);
        }
        escopo.exigir(empresaId);
        return repo.listarDepartamentos(empresaId);
    }

    @Override
    public DepartamentoView criarDepartamento(Long empresaId, CreateDepartamentoCommand comando) {
        if (!repo.existeEmpresa(empresaId)) {
            throw new NotFoundException("Empresa inexistente: " + empresaId);
        }
        escopo.exigir(empresaId);
        try {
            Long id = repo.inserirDepartamento(empresaId, comando.nome());
            return new DepartamentoView(id, empresaId, comando.nome());
        } catch (DuplicateKeyException e) {
            throw new ConflictException("Já existe um departamento com este nome nesta empresa.");
        }
    }

    @Override
    public boolean existeEmpresa(Long empresaId) {
        return repo.existeEmpresa(empresaId);
    }

    @Override
    public boolean departamentoPertenceAEmpresa(Long departamentoId, Long empresaId) {
        return repo.departamentoPertenceAEmpresa(departamentoId, empresaId);
    }

    @Override
    public DepartamentoView buscarDepartamento(Long departamentoId) {
        return repo.buscarDepartamento(departamentoId)
                .orElseThrow(() -> new NotFoundException("Departamento inexistente: " + departamentoId));
    }

}
