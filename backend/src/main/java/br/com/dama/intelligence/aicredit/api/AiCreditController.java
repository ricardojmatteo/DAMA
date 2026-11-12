package br.com.dama.intelligence.aicredit.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** RF22–RF26 — transações, saldo, histórico e níveis de autonomia. */
@RestController
@RequestMapping("/api")
public class AiCreditController {

    private final AiCreditFacade facade;

    public AiCreditController(AiCreditFacade facade) {
        this.facade = facade;
    }

    @PostMapping("/colaboradores/{colaboradorId}/ai-credits/transacoes")
    @PreAuthorize("hasAuthority('AI_CREDITS_GERENCIAR')")
    public ResponseEntity<TransacaoCreditoView> lancarTransacao(@PathVariable Long colaboradorId,
                                                                  @Valid @RequestBody LancarTransacaoCommand comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.lancarTransacao(colaboradorId, comando));
    }

    @GetMapping("/colaboradores/{colaboradorId}/ai-credits/historico")
    @PreAuthorize("hasAuthority('AI_CREDITS_LER')")
    public List<TransacaoCreditoView> historico(@PathVariable Long colaboradorId) {
        return facade.historico(colaboradorId);
    }

    @GetMapping("/colaboradores/{colaboradorId}/ai-credits/saldo")
    @PreAuthorize("hasAuthority('AI_CREDITS_LER')")
    public SaldoCreditosView saldo(@PathVariable Long colaboradorId) {
        return facade.saldo(colaboradorId);
    }

    @GetMapping("/ai-credits/niveis")
    @PreAuthorize("hasAuthority('AI_CREDITS_LER')")
    public List<NivelAutonomiaView> listarNiveis() {
        return facade.listarNiveis();
    }

    @PostMapping("/ai-credits/niveis")
    @PreAuthorize("hasAuthority('AI_CREDITS_GERENCIAR')")
    public ResponseEntity<NivelAutonomiaView> criarNivel(@Valid @RequestBody CreateNivelCommand comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.criarNivel(comando));
    }
}
