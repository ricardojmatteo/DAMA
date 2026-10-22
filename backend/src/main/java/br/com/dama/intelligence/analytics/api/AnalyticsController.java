package br.com.dama.intelligence.analytics.api;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/** API somente leitura do módulo analytics (RF13, RF14, RF15, RF36; RF08/RF28). */
@RestController
@RequestMapping("/api/empresas/{empresaId}/analytics")
public class AnalyticsController {

    private final AnalyticsFacade facade;

    public AnalyticsController(AnalyticsFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/visao-consolidada")
    @PreAuthorize("hasAuthority('ANALYTICS_LER')")
    public VisaoConsolidadaView visaoConsolidada(@PathVariable Long empresaId) {
        return facade.visaoConsolidada(empresaId);
    }

    @GetMapping("/ranking-departamentos")
    @PreAuthorize("hasAuthority('ANALYTICS_LER')")
    public List<RankingDepartamentoView> rankingDepartamentos(@PathVariable Long empresaId,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return facade.rankingDepartamentos(empresaId, inicio, fim);
    }

    /** US11: o colaborador também vê o ranking, por isso basta GAMIFICACAO_LER. */
    @GetMapping("/ranking-colaboradores")
    @PreAuthorize("hasAnyAuthority('ANALYTICS_LER', 'GAMIFICACAO_LER')")
    public List<RankingColaboradorView> rankingColaboradores(@PathVariable Long empresaId,
                                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return facade.rankingColaboradores(empresaId, inicio, fim);
    }

    @GetMapping("/indicadores-departamentos")
    @PreAuthorize("hasAuthority('ANALYTICS_LER')")
    public List<IndicadorDepartamentoView> indicadoresPorDepartamento(@PathVariable Long empresaId,
                                                                        @RequestParam(required = false) Long indicadorId) {
        return facade.indicadoresPorDepartamento(empresaId, indicadorId);
    }

    @GetMapping("/alertas")
    @PreAuthorize("hasAuthority('ANALYTICS_LER')")
    public AnaliseGestaoView alertas(@PathVariable Long empresaId) {
        return facade.analisarGestao(empresaId);
    }
}
