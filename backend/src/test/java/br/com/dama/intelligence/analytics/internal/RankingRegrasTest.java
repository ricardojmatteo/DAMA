package br.com.dama.intelligence.analytics.internal;

import br.com.dama.intelligence.analytics.api.RankingColaboradorView;
import br.com.dama.intelligence.analytics.api.RankingDepartamentoView;
import br.com.dama.intelligence.analytics.internal.RankingRegras.Bonus;
import br.com.dama.intelligence.analytics.internal.RankingRegras.Colaborador;
import br.com.dama.intelligence.analytics.internal.RankingRegras.Departamento;
import br.com.dama.intelligence.analytics.internal.RankingRegras.Parametros;
import br.com.dama.intelligence.analytics.internal.RankingRegras.Tarefa;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.Assertions.within;

class RankingRegrasTest {

    /** 20.000 tokens por story point, primeiros 25% grátis, bônus do gestor limitado a 15%. */
    private static final Parametros P = new Parametros(20000, 0.25, 0.15);

    @Test
    void urgenciaMultiplicaOsPontosDaTarefa() {
        assertThat(RankingRegras.multiplicadorUrgencia("BAIXA")).isEqualTo(1.0);
        assertThat(RankingRegras.multiplicadorUrgencia("MEDIA")).isEqualTo(1.25);
        assertThat(RankingRegras.multiplicadorUrgencia("alta")).isEqualTo(1.5);
        assertThat(RankingRegras.multiplicadorUrgencia("CRITICA")).isEqualTo(2.0);
        assertThat(RankingRegras.pontosDaTarefa(tarefa(1, 4, "ALTA", 0))).isEqualTo(6.0);
    }

    @Test
    void osPrimeiros25PorCentoDoLimiteDeTokensSaoGratisEAPenalidadeCrescePeloRestante() {
        // tarefa de 5 story points: limite de 100.000 tokens
        assertThat(RankingRegras.fracaoPenalizada(tarefa(1, 5, "MEDIA", 0), P)).isEqualTo(0.0);
        assertThat(RankingRegras.fracaoPenalizada(tarefa(1, 5, "MEDIA", 25000), P)).isEqualTo(0.0);
        assertThat(RankingRegras.fracaoPenalizada(tarefa(1, 5, "MEDIA", 62500), P)).isCloseTo(0.5, within(1e-9));
        assertThat(RankingRegras.fracaoPenalizada(tarefa(1, 5, "MEDIA", 100000), P)).isEqualTo(1.0);
        assertThat(RankingRegras.fracaoPenalizada(tarefa(1, 5, "MEDIA", 300000), P)).isEqualTo(1.0);
    }

    @Test
    void tarefaMaiorTemLimiteMaiorDeTokens() {
        // os mesmos 62.500 tokens pesam menos numa tarefa de 10 story points (limite de 200.000)
        double pequena = RankingRegras.fracaoPenalizada(tarefa(1, 5, "MEDIA", 62500), P);
        double grande = RankingRegras.fracaoPenalizada(tarefa(1, 10, "MEDIA", 62500), P);
        assertThat(grande).isLessThan(pequena);
        assertThat(grande).isCloseTo((0.3125 - 0.25) / 0.75, within(1e-9));
    }

    @Test
    void pontuacaoEhPontosMenosPenalidadeDeIaMaisBonusLimitadoA15PorCento() {
        List<Colaborador> pessoas = List.of(new Colaborador(1L, "Ana", 10L), new Colaborador(2L, "Bia", 10L),
                new Colaborador(3L, "Caio", 10L), new Colaborador(4L, "Dani", 10L));
        List<Tarefa> tarefas = List.of(
                tarefa(1, 5, "ALTA", 0),          // Ana: 7,5 pontos, sem IA
                tarefa(2, 5, "ALTA", 100000),     // Bia: 7,5 pontos, mas gastou o limite inteiro: perde tudo
                tarefa(3, 2, "BAIXA", 0));        // Caio: 2 pontos
        List<Bonus> bonus = List.of(
                new Bonus(3L, BigDecimal.valueOf(5)),    // Caio: pediram 5, o teto é 15% de 2 = 0,3
                new Bonus(4L, BigDecimal.valueOf(10)));  // Dani não entregou nada: o teto é 0

        List<RankingColaboradorView> ranking = RankingRegras.rankingColaboradores(pessoas, tarefas, bonus, P);

        assertThat(ranking).extracting(RankingColaboradorView::nome, RankingColaboradorView::pontuacao, RankingColaboradorView::posicao)
                .containsExactly(
                        tuple("Ana", new BigDecimal("7.50"), 1L),
                        tuple("Caio", new BigDecimal("2.30"), 2L),
                        tuple("Bia", new BigDecimal("0.00"), 3L),
                        tuple("Dani", new BigDecimal("0.00"), 3L));   // empate divide a posição
        RankingColaboradorView bia = ranking.get(2);
        assertThat(bia.pontosTarefas()).isEqualByComparingTo("7.5");
        assertThat(bia.penalidadeIa()).isEqualByComparingTo("7.5");
        assertThat(ranking.get(1).bonusGestor()).isEqualByComparingTo("0.3");
    }

    @Test
    void notaVaiDeZeroADezRelativaAMaiorPontuacaoDaEmpresa() {
        List<Colaborador> pessoas = List.of(new Colaborador(1L, "Ana", 10L), new Colaborador(2L, "Caio", 10L), new Colaborador(3L, "Eva", 10L));
        List<Tarefa> tarefas = List.of(tarefa(1, 5, "ALTA", 0), tarefa(2, 2, "BAIXA", 0));

        List<RankingColaboradorView> ranking = RankingRegras.rankingColaboradores(pessoas, tarefas, List.of(), P);

        assertThat(ranking).extracting(RankingColaboradorView::nome, RankingColaboradorView::nota)
                .containsExactly(tuple("Ana", new BigDecimal("10.00")), tuple("Caio", new BigDecimal("2.67")),
                        tuple("Eva", new BigDecimal("0.00")));
    }

    @Test
    void semNenhumaEntregaTodasAsNotasSaoZero() {
        List<RankingColaboradorView> ranking = RankingRegras.rankingColaboradores(
                List.of(new Colaborador(1L, "Ana", 10L)), List.of(), List.of(), P);
        assertThat(ranking.get(0).nota()).isEqualByComparingTo("0");
        assertThat(ranking.get(0).posicao()).isEqualTo(1);
    }

    @Test
    void equipePequenaComMelhorMediaSuperaEquipeGrande() {
        List<Departamento> equipes = List.of(new Departamento(10L, "Grande"), new Departamento(20L, "Pequena"), new Departamento(30L, "Vazia"));
        List<Colaborador> pessoas = List.of(
                new Colaborador(1L, "A", 10L), new Colaborador(2L, "B", 10L), new Colaborador(3L, "C", 10L),
                new Colaborador(4L, "D", 10L), new Colaborador(5L, "E", 20L));
        List<Tarefa> tarefas = List.of(
                tarefa(1, 2, "BAIXA", 0), tarefa(2, 2, "BAIXA", 0), tarefa(3, 2, "BAIXA", 0), tarefa(4, 2, "BAIXA", 0),   // 8 no total, 2 por pessoa
                tarefa(5, 5, "BAIXA", 0));                                                                                // 5 no total, 5 por pessoa

        List<RankingDepartamentoView> ranking = RankingRegras.rankingDepartamentos(equipes, pessoas, tarefas, List.of(), P);

        assertThat(ranking).extracting(RankingDepartamentoView::departamentoNome, RankingDepartamentoView::posicao,
                        RankingDepartamentoView::pontuacaoPorPessoa, RankingDepartamentoView::nota)
                .containsExactly(
                        tuple("Pequena", 1L, new BigDecimal("5.00"), new BigDecimal("10.00")),
                        tuple("Grande", 2L, new BigDecimal("2.00"), new BigDecimal("4.00")),
                        tuple("Vazia", 3L, new BigDecimal("0.00"), new BigDecimal("0.00")));
        assertThat(ranking.get(1).pontuacaoTotal()).isEqualByComparingTo("8");
        assertThat(ranking.get(1).tarefasEntregues()).isEqualTo(4);
        assertThat(ranking.get(2).colaboradoresAtivos()).isZero();
    }

    private static Tarefa tarefa(long colaboradorId, int storyPoints, String urgencia, long tokens) {
        return new Tarefa(colaboradorId, BigDecimal.valueOf(storyPoints), urgencia, tokens);
    }
}
