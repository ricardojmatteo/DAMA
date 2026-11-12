package br.com.dama.intelligence.aicredit.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** RF19/RF20 — atividades produtivas e atividades realizadas. */
@RestController
@RequestMapping("/api")
public class AtividadeController {

    private final AtividadeFacade facade;

    public AtividadeController(AtividadeFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/empresas/{empresaId}/atividades-produtivas")
    @PreAuthorize("hasAuthority('AI_CREDITS_LER')")
    public List<AtividadeProdutivaView> listarAtividades(@PathVariable Long empresaId) {
        return facade.listarAtividades(empresaId);
    }

    @PostMapping("/empresas/{empresaId}/atividades-produtivas")
    @PreAuthorize("hasAuthority('AI_CREDITS_GERENCIAR')")
    public ResponseEntity<AtividadeProdutivaView> criarAtividade(@PathVariable Long empresaId,
                                                                   @Valid @RequestBody CreateAtividadeCommand comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.criarAtividade(empresaId, comando));
    }

    @PostMapping("/colaboradores/{colaboradorId}/atividades")
    @PreAuthorize("hasAuthority('AI_CREDITS_GERENCIAR')")
    public ResponseEntity<RegistroAtividadeResultadoView> registrarAtividade(@PathVariable Long colaboradorId,
                                                                               @Valid @RequestBody RegistrarAtividadeCommand comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.registrarAtividade(colaboradorId, comando));
    }

    @GetMapping("/colaboradores/{colaboradorId}/atividades")
    @PreAuthorize("hasAuthority('AI_CREDITS_LER')")
    public List<AtividadeRegistroView> listarRegistros(@PathVariable Long colaboradorId) {
        return facade.listarRegistros(colaboradorId);
    }
}
