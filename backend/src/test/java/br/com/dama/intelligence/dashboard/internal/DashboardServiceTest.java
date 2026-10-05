package br.com.dama.intelligence.dashboard.internal;

import br.com.dama.intelligence.colaborador.api.ColaboradorFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorView;
import br.com.dama.intelligence.conquista.api.ConquistaFacade;
import br.com.dama.intelligence.conquista.api.EvolucaoView;
import br.com.dama.intelligence.conquista.api.NivelEvolucaoView;
import br.com.dama.intelligence.dashboard.api.MembroEquipeView;
import br.com.dama.intelligence.organizacao.api.DepartamentoView;
import br.com.dama.intelligence.organizacao.api.OrganizacaoFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DashboardServiceTest {
    private ColaboradorFacade colaboradores;
    private OrganizacaoFacade organizacao;
    private ConquistaFacade conquistas;
    private DashboardRepository repo;
    private DashboardService service;

    @BeforeEach
    void setUp() {
        colaboradores = mock(ColaboradorFacade.class);
        organizacao = mock(OrganizacaoFacade.class);
        conquistas = mock(ConquistaFacade.class);
        repo = mock(DashboardRepository.class);
        service = new DashboardService(colaboradores, organizacao, conquistas, repo,
                Clock.fixed(Instant.parse("2026-10-05T12:00:00Z"), ZoneOffset.UTC));
    }

    @Test
    void dashboardDoColaboradorAgregaIndicadoresMetasPontosCreditosENivel() {
        when(colaboradores.buscarPorId(10L)).thenReturn(colaborador());
        NivelEvolucaoView nivel = new NivelEvolucaoView(1L, "Aprendiz", BigDecimal.TEN, 1, null);
        when(conquistas.evolucao(10L)).thenReturn(new EvolucaoView(10L, "Ana", BigDecimal.TEN, nivel, null, BigDecimal.ZERO, 100, List.of()));
        when(repo.contarMetas(10L, "Em andamento")).thenReturn(2L);
        when(repo.contarMetas(10L, "Concluida")).thenReturn(3L);
        when(repo.pontosTotais(10L)).thenReturn(new BigDecimal("40"));
        when(repo.aiCreditsSaldo(10L)).thenReturn(new BigDecimal("12"));

        var dashboard = service.doColaborador(10L);

        assertThat(dashboard.nome()).isEqualTo("Ana");
        assertThat(dashboard.nivelEvolucao()).isEqualTo("Aprendiz");
        assertThat(dashboard.metasEmAndamento()).isEqualTo(2);

        when(conquistas.evolucao(11L)).thenReturn(new EvolucaoView(11L, "Ana", BigDecimal.ZERO, null, null, null, 0, List.of()));
        when(colaboradores.buscarPorId(11L)).thenReturn(colaborador());
        assertThat(service.doColaborador(11L).nivelEvolucao()).isNull();
    }

    @Test
    void dashboardDaEquipeUsaDataDoClockEConsolidaDadosDaEquipe() {
        when(organizacao.buscarDepartamento(2L)).thenReturn(new DepartamentoView(2L, 1L, "TI"));
        when(repo.colaboradoresAtivosDaEquipe(2L)).thenReturn(4L);
        when(repo.contarMetasDaEquipe(2L, "Em andamento")).thenReturn(2L);
        when(repo.contarMetasDaEquipe(2L, "Concluida")).thenReturn(3L);
        when(repo.contarMetasVencidasDaEquipe(2L, LocalDate.of(2026, 10, 5))).thenReturn(1L);
        when(repo.pontosDaEquipe(2L)).thenReturn(new BigDecimal("90"));
        when(repo.aiCreditsSaldoDaEquipe(2L)).thenReturn(new BigDecimal("40"));
        when(repo.conquistasDaEquipe(2L)).thenReturn(5L);
        when(repo.posicaoNoRanking(2L)).thenReturn(1L);
        when(repo.membros(2L)).thenReturn(List.of(new MembroEquipeView(10L, "Ana", "Dev", BigDecimal.TEN, BigDecimal.ONE, 2)));

        var dashboard = service.daEquipe(2L);

        assertThat(dashboard).extracting(d -> d.nome(), d -> d.metasVencidas(), d -> d.posicaoNoRanking())
                .containsExactly("TI", 1L, 1L);
        verify(repo).contarMetasVencidasDaEquipe(2L, LocalDate.of(2026, 10, 5));
    }

    private static ColaboradorView colaborador() {
        return new ColaboradorView(10L, 1L, 2L, "TI", "Ana", "ana@x.com", "Dev", "Pleno", LocalDate.now(), true);
    }
}
