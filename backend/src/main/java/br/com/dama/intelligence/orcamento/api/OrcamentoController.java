package br.com.dama.intelligence.orcamento.api;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/empresas/{empresaId}/orcamentos-ia")
public class OrcamentoController {

    private final OrcamentoFacade facade;

    public OrcamentoController(OrcamentoFacade facade) {
        this.facade = facade;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ANALYTICS_LER')")
    public List<OrcamentoIaView> listar(@PathVariable Long empresaId,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate mes) {
        return facade.listar(empresaId, mes);
    }

    /** Idempotente: um registro por departamento e mês; repetir a chamada atualiza. */
    @PutMapping
    @PreAuthorize("hasAuthority('DESEMPENHO_ESCREVER')")
    public OrcamentoIaView salvar(@PathVariable Long empresaId, @Valid @RequestBody UpsertOrcamentoCommand comando) {
        return facade.salvar(empresaId, comando);
    }
}
