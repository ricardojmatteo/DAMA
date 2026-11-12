package br.com.dama.intelligence.auth.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** US01 — entrar na plataforma e gerenciar a própria senha. */
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthFacade facade;

    public AuthController(AuthFacade facade) {
        this.facade = facade;
    }

    /** Público: é a única rota da API que não exige token. */
    @PostMapping("/auth/login")
    public TokenView login(@Valid @RequestBody LoginCommand comando) {
        return facade.login(comando);
    }

    @PostMapping("/auth/senha")
    public ResponseEntity<Void> alterarPropriaSenha(@Valid @RequestBody AlterarSenhaCommand comando) {
        facade.alterarPropriaSenha(comando);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/colaboradores/{colaboradorId}/senha")
    @PreAuthorize("hasAuthority('COLABORADOR_ESCREVER')")
    public ResponseEntity<Void> definirSenha(@PathVariable Long colaboradorId, @Valid @RequestBody DefinirSenhaCommand comando) {
        facade.definirSenha(colaboradorId, comando);
        return ResponseEntity.noContent().build();
    }
}
