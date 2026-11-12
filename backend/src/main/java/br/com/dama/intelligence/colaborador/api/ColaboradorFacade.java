package br.com.dama.intelligence.colaborador.api;

import java.util.List;
import java.util.Optional;

/** Contrato público do módulo colaborador (RF01, RF02, RF16, RF17). */
public interface ColaboradorFacade {
    List<ColaboradorView> listarPorEmpresa(Long empresaId);

    /** Leitura interna entre módulos: não aplica o escopo de empresa (quem chama já validou o contexto). */
    ColaboradorView buscarPorId(Long colaboradorId);

    /** Leitura pela API: 403 se o colaborador for de outra empresa que não a do usuário (ADR-0011). */
    ColaboradorView buscarNaEmpresaDoUsuario(Long colaboradorId);

    ColaboradorView criar(Long empresaId, CreateColaboradorCommand comando);

    ColaboradorView atualizar(Long colaboradorId, UpdateColaboradorCommand comando);

    ColaboradorView alterarStatus(Long colaboradorId, boolean ativo);

    /** Usado por outros módulos (perfil, pontuacao, meta...) para validar existência sem depender de detalhes internos. */
    boolean existe(Long colaboradorId);

    /** Credencial pelo e-mail (único na plataforma), para o módulo auth. Vazio se o e-mail não existir. */
    Optional<CredencialView> credencialPorEmail(String email);

    /** Hash da senha atual do colaborador (null se ainda não tem senha). */
    String senhaHash(Long colaboradorId);

    /** Grava um novo hash de senha (já codificado por quem chama). */
    void definirSenhaHash(Long colaboradorId, String senhaHash);
}
