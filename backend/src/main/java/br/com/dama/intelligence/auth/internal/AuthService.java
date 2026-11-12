package br.com.dama.intelligence.auth.internal;

import br.com.dama.intelligence.auth.api.*;
import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorView;
import br.com.dama.intelligence.colaborador.api.CredencialView;
import br.com.dama.intelligence.shared.error.ValidationException;
import br.com.dama.intelligence.shared.security.EscopoEmpresa;
import br.com.dama.intelligence.shared.security.JwtService;
import br.com.dama.intelligence.shared.security.UsuarioAtual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
class AuthService implements AuthFacade {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final ColaboradorFacade colaboradores;
    private final PasswordEncoder senhas;
    private final JwtService jwt;
    private final AuditoriaFacade auditoria;
    private final EscopoEmpresa escopo;
    /** Hash de uma senha aleatória: comparado quando o e-mail não existe, para o tempo de resposta não revelar e-mails válidos. */
    private final String hashNeutro;

    AuthService(ColaboradorFacade colaboradores, PasswordEncoder senhas, JwtService jwt, AuditoriaFacade auditoria,
                EscopoEmpresa escopo) {
        this.colaboradores = colaboradores;
        this.senhas = senhas;
        this.jwt = jwt;
        this.auditoria = auditoria;
        this.escopo = escopo;
        this.hashNeutro = senhas.encode(java.util.UUID.randomUUID().toString());
    }

    @Override
    @Transactional
    public TokenView login(LoginCommand comando) {
        Optional<CredencialView> credencial = colaboradores.credencialPorEmail(comando.email().trim());
        String hash = credencial.map(CredencialView::senhaHash).orElse(null);
        boolean senhaConfere = senhas.matches(comando.senha(), hash == null ? hashNeutro : hash);

        if (credencial.isEmpty() || hash == null || !senhaConfere) {
            log.info("Login recusado para {}", comando.email());
            throw new BadCredentialsException("E-mail ou senha inválidos.");
        }
        CredencialView c = credencial.get();
        if (!c.ativo()) {
            log.info("Login recusado: colaborador {} desligado", c.colaboradorId());
            throw new BadCredentialsException("Colaborador desligado.");
        }
        JwtService.TokenEmitido emitido = jwt.emitir(c.colaboradorId());
        auditoria.registrar(c.empresaId(), c.colaboradorId(), "sessao", c.colaboradorId().toString(), "LOGIN", null);
        return new TokenView(emitido.token(), "Bearer", emitido.expiraEm(), c.colaboradorId());
    }

    @Override
    @Transactional
    public void alterarPropriaSenha(AlterarSenhaCommand comando) {
        Long id = UsuarioAtual.id();
        String hashAtual = colaboradores.senhaHash(id);
        if (hashAtual == null || !senhas.matches(comando.senhaAtual(), hashAtual)) {
            throw new ValidationException("A senha atual não confere.");
        }
        colaboradores.definirSenhaHash(id, senhas.encode(comando.novaSenha()));
    }

    @Override
    @Transactional
    public void definirSenha(Long colaboradorId, DefinirSenhaCommand comando) {
        ColaboradorView alvo = colaboradores.buscarPorId(colaboradorId); // 404 se não existir
        escopo.exigir(alvo.empresaId());
        colaboradores.definirSenhaHash(colaboradorId, senhas.encode(comando.senha()));
    }
}
