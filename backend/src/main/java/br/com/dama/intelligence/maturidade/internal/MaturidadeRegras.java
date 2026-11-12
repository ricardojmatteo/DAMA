package br.com.dama.intelligence.maturidade.internal;

import br.com.dama.intelligence.maturidade.api.AmsDepartamentoView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Cálculo do AI Maturity Score (docs/produto/ams.md). Funções puras sobre contagens já lidas do banco.
 *
 * <pre>
 * adoção        = % de colaboradores ativos com uso de IA registrado na janela (atividade produtiva ou tarefa com tokens)
 * capacitação   = % de colaboradores ativos com treinamento em IA concluído nos últimos 12 meses
 * governança    = % das tarefas entregues dentro do limite de tokens e sem retrabalho
 * produtividade = story points entregues por colaborador ativo, em relação ao melhor departamento da empresa (= 100)
 * eficiência    = 100 × (1 − fração média penalizada por uso excessivo de IA nas tarefas)
 * AMS           = Σ peso × dimensão ÷ 100
 * </pre>
 *
 * Todas as dimensões são absolutas (0 a 100) exceto produtividade, que é relativa ao melhor departamento, porque story
 * points não têm escala comparável entre empresas. Departamento sem colaborador ativo tem AMS 0.
 */
final class MaturidadeRegras {

    private MaturidadeRegras() {
    }

    /** Pesos em pontos percentuais; precisam somar 100. */
    record Pesos(double adocao, double capacitacao, double governanca, double produtividade, double eficiencia) {
        Pesos {
            double soma = adocao + capacitacao + governanca + produtividade + eficiencia;
            if (Math.abs(soma - 100) > 0.01 || adocao < 0 || capacitacao < 0 || governanca < 0 || produtividade < 0 || eficiencia < 0) {
                throw new IllegalArgumentException("Os pesos do AMS devem ser não negativos e somar 100 (soma atual: " + soma + ").");
            }
        }
    }

    /** Contagens de um departamento na janela. */
    record Dados(Long departamentoId, String nome, long ativos, long comUsoDeIa, long capacitados, long tarefas,
                 long tarefasConformes, double storyPoints, double somaFracaoPenalizada) {
    }

    static List<AmsDepartamentoView> calcular(List<Dados> departamentos, Pesos pesos) {
        double melhorProdutividade = departamentos.stream().mapToDouble(MaturidadeRegras::storyPointsPorPessoa).max().orElse(0);
        List<AmsDepartamentoView> resultado = new ArrayList<>();
        for (Dados d : departamentos) {
            double adocao = percentual(d.comUsoDeIa(), d.ativos());
            double capacitacao = percentual(d.capacitados(), d.ativos());
            double governanca = percentual(d.tarefasConformes(), d.tarefas());
            double porPessoa = storyPointsPorPessoa(d);
            double produtividade = melhorProdutividade <= 0 ? 0 : 100 * porPessoa / melhorProdutividade;
            double eficiencia = d.tarefas() == 0 ? 0 : 100 * (1 - d.somaFracaoPenalizada() / d.tarefas());
            double ams = d.ativos() == 0 ? 0
                    : (pesos.adocao() * adocao + pesos.capacitacao() * capacitacao + pesos.governanca() * governanca
                    + pesos.produtividade() * produtividade + pesos.eficiencia() * eficiencia) / 100;
            String faixa = faixa(ams);
            resultado.add(new AmsDepartamentoView(d.departamentoId(), d.nome(), d.ativos(), arredondar(adocao),
                    arredondar(capacitacao), arredondar(governanca), arredondar(produtividade), arredondar(eficiencia),
                    arredondar(ams), faixa, leitura(faixa),
                    new AmsDepartamentoView.Evidencias(d.comUsoDeIa(), d.capacitados(), d.tarefas(), d.tarefasConformes(),
                            arredondar(porPessoa), d.tarefas() == 0 ? null : arredondar(d.somaFracaoPenalizada() / d.tarefas()))));
        }
        return resultado;
    }

    /** AMS da empresa: média dos departamentos ponderada pelos colaboradores ativos. */
    static double consolidar(List<AmsDepartamentoView> departamentos) {
        long ativos = departamentos.stream().mapToLong(AmsDepartamentoView::colaboradoresAtivos).sum();
        if (ativos == 0) {
            return 0;
        }
        return departamentos.stream().mapToDouble(d -> d.ams().doubleValue() * d.colaboradoresAtivos()).sum() / ativos;
    }

    /** Faixas da visão do produto (seção 4.3): 0–39, 40–69, 70–100. */
    static String faixa(double ams) {
        if (ams < 40) {
            return "ADOCAO_BAIXA";
        }
        return ams < 70 ? "EM_DESENVOLVIMENTO" : "AVANCADO";
    }

    static String leitura(String faixa) {
        return switch (faixa) {
            case "ADOCAO_BAIXA" -> "Adoção baixa: rever licenças ociosas e começar pela capacitação.";
            case "EM_DESENVOLVIMENTO" -> "Em desenvolvimento: capacitar quem usa muito e ampliar o acesso aos capacitados.";
            default -> "Avançado: ampliar níveis de autonomia e usar a equipe como referência.";
        };
    }

    private static double storyPointsPorPessoa(Dados d) {
        return d.ativos() == 0 ? 0 : d.storyPoints() / d.ativos();
    }

    private static double percentual(long parte, long total) {
        return total == 0 ? 0 : 100.0 * parte / total;
    }

    static BigDecimal arredondar(double valor) {
        return BigDecimal.valueOf(valor).setScale(1, RoundingMode.HALF_UP);
    }
}
