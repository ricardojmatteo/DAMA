package br.com.dama.intelligence.aicredit.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** RF21 — regras de elegibilidade para AI Credits. */
@RestController
@RequestMapping("/api")
public class RegraCreditoController {

    private final RegraCreditoFacade facade;

    public RegraCreditoController(RegraCreditoFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/empresas/{empresaId}/ai-credits/regras")
    @PreAuthorize("hasAuthority('AI_CREDITS_LER')")
    public List<RegraCreditoView> listarRegras(@PathVariable Long empresaId) {
        return facade.listarRegras(empresaId);
    }

    @PostMapping("/empresas/{empresaId}/ai-credits/regras")
    @PreAuthorize("hasAuthority('AI_CREDITS_GERENCIAR')")
    public ResponseEntity<RegraCreditoView> criarRegra(@PathVariable Long empresaId,
                                                         @Valid @RequestBody CreateRegraCreditoCommand comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.criarRegra(empresaId, comando));
    }

    @GetMapping("/atividades-produtivas/{atividadeId}/elegibilidade")
    @PreAuthorize("hasAuthority('AI_CREDITS_LER')")
    public ElegibilidadeView avaliarElegibilidade(@PathVariable Long atividadeId) {
        return facade.avaliarElegibilidade(atividadeId);
    }
}
