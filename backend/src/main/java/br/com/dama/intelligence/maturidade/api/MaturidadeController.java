package br.com.dama.intelligence.maturidade.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MaturidadeController {

    private final MaturidadeFacade facade;

    public MaturidadeController(MaturidadeFacade facade) {
        this.facade = facade;
    }

    /** RF40 — AI Maturity Score da empresa e dos departamentos. */
    @GetMapping("/empresas/{empresaId}/ams")
    @PreAuthorize("hasAuthority('ANALYTICS_LER')")
    public AmsEmpresaView ams(@PathVariable Long empresaId) {
        return facade.ams(empresaId);
    }

    @GetMapping("/colaboradores/{colaboradorId}/capacitacoes")
    @PreAuthorize("hasAuthority('DESEMPENHO_LER')")
    public List<CapacitacaoView> capacitacoes(@PathVariable Long colaboradorId) {
        return facade.capacitacoesDoColaborador(colaboradorId);
    }

    @PostMapping("/colaboradores/{colaboradorId}/capacitacoes")
    @PreAuthorize("hasAuthority('DESEMPENHO_ESCREVER')")
    public ResponseEntity<CapacitacaoView> registrar(@PathVariable Long colaboradorId,
                                                     @Valid @RequestBody RegistrarCapacitacaoCommand comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.registrarCapacitacao(colaboradorId, comando));
    }
}
