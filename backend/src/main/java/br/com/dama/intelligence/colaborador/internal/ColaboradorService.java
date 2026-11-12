package br.com.dama.intelligence.colaborador.internal;

import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.colaborador.api.*;
import br.com.dama.intelligence.shared.error.ConflictException;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.error.ValidationException;
import br.com.dama.intelligence.shared.security.EscopoEmpresa;
import br.com.dama.intelligence.shared.security.UsuarioAtual;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
class ColaboradorService implements ColaboradorFacade {

    private final ColaboradorRepository repo;
    private final AuditoriaFacade auditoria;
    private final EscopoEmpresa escopo;
    private final PasswordEncoder senhas;

    ColaboradorService(ColaboradorRepository repo, AuditoriaFacade auditoria, EscopoEmpresa escopo, PasswordEncoder senhas) {
        this.repo = repo;
        this.auditoria = auditoria;
        this.escopo = escopo;
        this.senhas = senhas;
    }

    @Override
    public List<ColaboradorView> listarPorEmpresa(Long empresaId) {
        if (!repo.existeEmpresa(empresaId)) {
            throw new NotFoundException("Empresa inexistente: " + empresaId);
        }
        escopo.exigir(empresaId);
        return repo.listarPorEmpresa(empresaId);
    }

    /** Leitura interna (outros módulos e filtro de autenticação): não aplica o escopo de empresa. */
    @Override
    public ColaboradorView buscarPorId(Long colaboradorId) {
        return repo.buscarPorId(colaboradorId)
                .orElseThrow(() -> new NotFoundException("Colaborador inexistente: " + colaboradorId));
    }

    /** Leitura pela API: o colaborador precisa ser da empresa do usuário. */
    @Override
    public ColaboradorView buscarNaEmpresaDoUsuario(Long colaboradorId) {
        ColaboradorView colaborador = buscarPorId(colaboradorId);
        escopo.exigir(colaborador.empresaId());
        return colaborador;
    }

    @Override
    @Transactional
    public ColaboradorView criar(Long empresaId, CreateColaboradorCommand comando) {
        if (!repo.existeEmpresa(empresaId)) {
            throw new NotFoundException("Empresa inexistente: " + empresaId);
        }
        escopo.exigir(empresaId);
        if (!repo.departamentoPertenceAEmpresa(comando.departamentoId(), empresaId)) {
            throw new ValidationException("O departamento informado não pertence a esta empresa.");
        }
        if (repo.emailJaCadastradoNaPlataforma(comando.email(), null)) {
            throw new ConflictException("Já existe um colaborador com este e-mail.");
        }

        String senhaHash = comando.senha() == null ? null : senhas.encode(comando.senha());
        Long colaboradorId = repo.inserir(empresaId, comando.departamentoId(), comando.nome(), comando.email(),
                comando.cargo(), comando.senioridade(), comando.dataAdmissao(), senhaHash);

        auditoria.registrar(empresaId, UsuarioAtual.id(), "colaborador", colaboradorId.toString(), "CRIACAO", null);
        return buscarPorId(colaboradorId);
    }

    @Override
    @Transactional
    public ColaboradorView atualizar(Long colaboradorId, UpdateColaboradorCommand comando) {
        ColaboradorView atual = buscarNaEmpresaDoUsuario(colaboradorId);

        if (!repo.departamentoPertenceAEmpresa(comando.departamentoId(), atual.empresaId())) {
            throw new ValidationException("O departamento informado não pertence a esta empresa.");
        }
        if (repo.emailJaCadastradoNaPlataforma(comando.email(), colaboradorId)) {
            throw new ConflictException("Já existe outro colaborador com este e-mail.");
        }

        repo.atualizar(colaboradorId, comando.departamentoId(), comando.nome(), comando.email(),
                comando.cargo(), comando.senioridade());

        auditoria.registrar(atual.empresaId(), UsuarioAtual.id(), "colaborador", colaboradorId.toString(), "ATUALIZACAO", null);
        return buscarPorId(colaboradorId);
    }

    @Override
    @Transactional
    public ColaboradorView alterarStatus(Long colaboradorId, boolean ativo) {
        ColaboradorView atual = buscarNaEmpresaDoUsuario(colaboradorId);
        repo.alterarStatus(colaboradorId, ativo);
        auditoria.registrar(atual.empresaId(), UsuarioAtual.id(), "colaborador", colaboradorId.toString(),
                ativo ? "REATIVACAO" : "DESLIGAMENTO", null);
        return buscarPorId(colaboradorId);
    }

    @Override
    public boolean existe(Long colaboradorId) {
        return repo.existe(colaboradorId);
    }

    @Override
    public Optional<CredencialView> credencialPorEmail(String email) {
        return repo.credencialPorEmail(email);
    }

    @Override
    public String senhaHash(Long colaboradorId) {
        return repo.senhaHash(colaboradorId);
    }

    @Override
    @Transactional
    public void definirSenhaHash(Long colaboradorId, String senhaHash) {
        ColaboradorView atual = buscarPorId(colaboradorId);
        repo.definirSenhaHash(colaboradorId, senhaHash);
        auditoria.registrar(atual.empresaId(), UsuarioAtual.id(), "colaborador", colaboradorId.toString(), "SENHA_DEFINIDA", null);
    }
}
