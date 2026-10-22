package br.com.dama.intelligence.analytics.internal;

import br.com.dama.intelligence.analytics.api.RankingColaboradorView;
import br.com.dama.intelligence.analytics.api.RankingDepartamentoView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cálculo do ranking (docs/requisitos/ranking.md). Funções puras: recebem as tarefas e bônus da janela de tempo
 * já lidos do banco e devolvem as posições.
 *
 * <pre>
 * pontos da tarefa = story points × multiplicador da urgência
 * penalidade de IA = pontos da tarefa × fração penalizada   (0 até a franquia grátis, 1 no limite de tokens)
 * pontuação        = max(0, Σ (pontos − penalidade) + bônus do gestor)
 * </pre>
 */
final class RankingRegras {

    private RankingRegras() {
    }

    /**
     * @param tokensPorPonto limite de tokens por story point: com esse uso a tarefa perde todos os pontos
     * @param franquiaGratis fração do limite que é gratuita (0,25 = os primeiros 25% não pesam)
     * @param bonusMaximo    teto do bônus do gestor como fração dos pontos líquidos das tarefas (0,15 = 15%)
     */
    record Parametros(double tokensPorPonto, double franquiaGratis, double bonusMaximo) {
    }

    record Colaborador(Long colaboradorId, String nome, Long departamentoId) {
    }

    /** Tarefa entregue na janela. */
    record Tarefa(Long colaboradorId, BigDecimal storyPoints, String urgencia, long tokensIa) {
    }

    /** Soma dos bônus do gestor concedidos ao colaborador na janela. */
    record Bonus(Long colaboradorId, BigDecimal pontos) {
    }

    record Departamento(Long departamentoId, String nome) {
    }

    /** Resultado de um colaborador, antes de virar posição. */
    private record Calculo(Colaborador colaborador, long tarefas, double pontosTarefas, double penalidadeIa,
                           double bonusAplicado, double pontuacao) {
    }

    private record Equipe(Departamento departamento, int membros, long tarefas, double total, double porPessoa) {
    }

    /** BAIXA 1,0 · MEDIA 1,25 · ALTA 1,5 · CRITICA 2,0. Valor desconhecido conta como BAIXA. */
    static double multiplicadorUrgencia(String urgencia) {
        if (urgencia == null) {
            return 1.0;
        }
        switch (urgencia.toUpperCase()) {
            case "MEDIA":
                return 1.25;
            case "ALTA":
                return 1.5;
            case "CRITICA":
                return 2.0;
            default:
                return 1.0;
        }
    }

    static double pontosDaTarefa(Tarefa t) {
        return t.storyPoints().doubleValue() * multiplicadorUrgencia(t.urgencia());
    }

    /** 0 até a franquia grátis; cresce linearmente até 1 quando os tokens chegam ao limite (story points × tokens por ponto). */
    static double fracaoPenalizada(Tarefa t, Parametros p) {
        double limite = t.storyPoints().doubleValue() * p.tokensPorPonto();
        double uso = limite <= 0 ? 0 : t.tokensIa() / limite;
        double fracao = (uso - p.franquiaGratis()) / (1 - p.franquiaGratis());
        return Math.max(0, Math.min(1, fracao));
    }

    static List<RankingColaboradorView> rankingColaboradores(List<Colaborador> colaboradores, List<Tarefa> tarefas,
                                                             List<Bonus> bonus, Parametros p) {
        List<Calculo> calculos = calcular(colaboradores, tarefas, bonus, p);
        double maior = calculos.stream().mapToDouble(Calculo::pontuacao).max().orElse(0);
        List<RankingColaboradorView> semPosicao = new ArrayList<>();
        for (Calculo c : calculos) {
            semPosicao.add(new RankingColaboradorView(c.colaborador().colaboradorId(), c.colaborador().nome(),
                    c.colaborador().departamentoId(), c.tarefas(), arredondar(c.pontosTarefas()),
                    arredondar(c.penalidadeIa()), arredondar(c.bonusAplicado()), arredondar(c.pontuacao()),
                    arredondar(nota(c.pontuacao(), maior)), 0));
        }
        List<RankingColaboradorView> ordenados = semPosicao.stream()
                .sorted(Comparator.comparing(RankingColaboradorView::pontuacao).reversed()
                        .thenComparing(RankingColaboradorView::nome))
                .toList();
        List<RankingColaboradorView> resultado = new ArrayList<>();
        for (RankingColaboradorView r : ordenados) {
            long posicao = 1 + ordenados.stream().filter(o -> o.pontuacao().compareTo(r.pontuacao()) > 0).count();
            resultado.add(new RankingColaboradorView(r.colaboradorId(), r.nome(), r.departamentoId(), r.tarefasEntregues(),
                    r.pontosTarefas(), r.penalidadeIa(), r.bonusGestor(), r.pontuacao(), r.nota(), posicao));
        }
        return resultado;
    }

    /** Equipes sem colaborador ativo entram com pontuação zero. */
    static List<RankingDepartamentoView> rankingDepartamentos(List<Departamento> departamentos, List<Colaborador> colaboradores,
                                                              List<Tarefa> tarefas, List<Bonus> bonus, Parametros p) {
        List<Calculo> calculos = calcular(colaboradores, tarefas, bonus, p);
        List<Equipe> equipes = new ArrayList<>();
        for (Departamento dep : departamentos) {
            List<Calculo> membros = calculos.stream()
                    .filter(c -> dep.departamentoId().equals(c.colaborador().departamentoId())).toList();
            double total = membros.stream().mapToDouble(Calculo::pontuacao).sum();
            long n = membros.stream().mapToLong(Calculo::tarefas).sum();
            equipes.add(new Equipe(dep, membros.size(), n, total, membros.isEmpty() ? 0 : total / membros.size()));
        }
        double maior = equipes.stream().mapToDouble(Equipe::porPessoa).max().orElse(0);
        List<RankingDepartamentoView> semPosicao = new ArrayList<>();
        for (Equipe e : equipes) {
            semPosicao.add(new RankingDepartamentoView(e.departamento().departamentoId(), e.departamento().nome(), e.membros(),
                    e.tarefas(), arredondar(e.total()), arredondar(e.porPessoa()), arredondar(nota(e.porPessoa(), maior)), 0));
        }
        List<RankingDepartamentoView> ordenados = semPosicao.stream()
                .sorted(Comparator.comparing(RankingDepartamentoView::pontuacaoPorPessoa).reversed()
                        .thenComparing(RankingDepartamentoView::departamentoNome))
                .toList();
        List<RankingDepartamentoView> resultado = new ArrayList<>();
        for (RankingDepartamentoView r : ordenados) {
            long posicao = 1 + ordenados.stream().filter(o -> o.pontuacaoPorPessoa().compareTo(r.pontuacaoPorPessoa()) > 0).count();
            resultado.add(new RankingDepartamentoView(r.departamentoId(), r.departamentoNome(), r.colaboradoresAtivos(),
                    r.tarefasEntregues(), r.pontuacaoTotal(), r.pontuacaoPorPessoa(), r.nota(), posicao));
        }
        return resultado;
    }

    private static List<Calculo> calcular(List<Colaborador> colaboradores, List<Tarefa> tarefas, List<Bonus> bonus, Parametros p) {
        Map<Long, List<Tarefa>> tarefasPor = new HashMap<>();
        for (Tarefa t : tarefas) {
            tarefasPor.computeIfAbsent(t.colaboradorId(), k -> new ArrayList<>()).add(t);
        }
        Map<Long, Double> bonusPor = new HashMap<>();
        for (Bonus b : bonus) {
            bonusPor.merge(b.colaboradorId(), b.pontos().doubleValue(), Double::sum);
        }
        List<Calculo> calculos = new ArrayList<>();
        for (Colaborador c : colaboradores) {
            List<Tarefa> doColaborador = tarefasPor.getOrDefault(c.colaboradorId(), List.of());
            double pontos = 0;
            double penalidade = 0;
            for (Tarefa t : doColaborador) {
                double pontosDaTarefa = pontosDaTarefa(t);
                pontos += pontosDaTarefa;
                penalidade += pontosDaTarefa * fracaoPenalizada(t, p);
            }
            double liquidos = pontos - penalidade;
            double bonusAplicado = Math.min(bonusPor.getOrDefault(c.colaboradorId(), 0.0), p.bonusMaximo() * liquidos);
            calculos.add(new Calculo(c, doColaborador.size(), pontos, penalidade, bonusAplicado,
                    Math.max(0, liquidos + bonusAplicado)));
        }
        return calculos;
    }

    private static double nota(double valor, double maior) {
        return maior <= 0 ? 0 : 10 * valor / maior;
    }

    private static BigDecimal arredondar(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP);
    }
}
