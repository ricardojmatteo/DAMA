package br.com.dama.intelligence.pontuacao.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PontuacaoController {

    private final PontuacaoFacade facade;

    public PontuacaoController(PontuacaoFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/empresas/{empresaId}/regras-pontuacao")
    @PreAuthorize("hasAuthority('GAMIFICACAO_LER')")
    public List<RegraPontuacaoView> listarRegras(@PathVariable Long empresaId) {
        return facade.listarRegras(empresaId);
    }

    // Lançar pontos e mexer nas regras é exclusivo do Administrador (permissão PONTOS_LANCAR).
    @PostMapping("/empresas/{empresaId}/regras-pontuacao")
    @PreAuthorize("hasAuthority('PONTOS_LANCAR')")
    public ResponseEntity<RegraPontuacaoView> criarRegra(@PathVariable Long empresaId,
                                                           @Valid @RequestBody CreateRegraPontuacaoCommand comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.criarRegra(empresaId, comando));
    }

    @PatchMapping("/regras-pontuacao/{regraId}/ativa")
    @PreAuthorize("hasAuthority('PONTOS_LANCAR')")
    public RegraPontuacaoView alterarRegraAtiva(@PathVariable Long regraId,
                                                  @Valid @RequestBody AlterarRegraAtivaCommand comando) {
        return facade.alterarRegraAtiva(regraId, comando.ativa());
    }

    @PostMapping("/colaboradores/{colaboradorId}/pontuacao")
    @PreAuthorize("hasAuthority('PONTOS_LANCAR')")
    public ResponseEntity<PontuacaoView> lancar(@PathVariable Long colaboradorId,
                                                  @Valid @RequestBody LancarPontosCommand comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.lancar(colaboradorId, comando));
    }

    // Tarefas entregues e bônus são registrados pelo gestor (SM/coordenador) ou pela integração com o Jira.
    @PostMapping("/colaboradores/{colaboradorId}/tarefas")
    @PreAuthorize("hasAuthority('DESEMPENHO_ESCREVER')")
    public ResponseEntity<TarefaEntregueView> registrarTarefa(@PathVariable Long colaboradorId,
                                                                @Valid @RequestBody RegistrarTarefaCommand comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.registrarTarefa(colaboradorId, comando));
    }

    @GetMapping("/colaboradores/{colaboradorId}/tarefas")
    @PreAuthorize("hasAuthority('GAMIFICACAO_LER')")
    public List<TarefaEntregueView> listarTarefas(@PathVariable Long colaboradorId) {
        return facade.listarTarefas(colaboradorId);
    }

    @PostMapping("/colaboradores/{colaboradorId}/bonus")
    @PreAuthorize("hasAuthority('DESEMPENHO_ESCREVER')")
    public ResponseEntity<BonusGestorView> concederBonus(@PathVariable Long colaboradorId,
                                                           @Valid @RequestBody ConcederBonusCommand comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.concederBonus(colaboradorId, comando));
    }

    @GetMapping("/colaboradores/{colaboradorId}/bonus")
    @PreAuthorize("hasAuthority('GAMIFICACAO_LER')")
    public List<BonusGestorView> listarBonus(@PathVariable Long colaboradorId) {
        return facade.listarBonus(colaboradorId);
    }

    @GetMapping("/colaboradores/{colaboradorId}/pontuacao")
    @PreAuthorize("hasAuthority('GAMIFICACAO_LER')")
    public PontuacaoResumoView resumo(@PathVariable Long colaboradorId) {
        return facade.resumo(colaboradorId);
    }
}
