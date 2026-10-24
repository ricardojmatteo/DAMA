# Machine Learning — Deliverable 2

**Quem vai usar cada ferramenta de IA no próximo mês?** Um modelo estima, para cada colaborador × ferramenta por assento, a probabilidade de uso no mês seguinte. Com ela, o DAMA recomenda quem deve receber, manter ou devolver cada licença. O trabalho continua o modelo do Deliverable 1 (`Deliverable1/machine-learning-ia`), agora com histórico de uso e validação temporal.

**DAMA Intelligence** · Ana Luiza Proença, Gustavo Gomes, Kaique Lira e Ricardo Matteo · Ciência de Dados e Negócios, ESPM, 4º semestre, 2026

## Resumo dos resultados

- **O modelo prevê bem e é calibrado.** No teste (jun e jul/2026, nunca vistos no ajuste), o gradient boosting tem:
  - PR-AUC de **0,991** e ROC-AUC de **0,993**;
  - Brier de **0,031**: entre os casos com probabilidade prevista em torno de 70%, 68% usaram.
- **A regra simples já é forte, e o ganho do modelo está onde ela falha.**
  - "Quem usou no mês usa no próximo" tem PR-AUC de 0,958.
  - Entre quem **não usou no mês**, o modelo chega a PR-AUC de 0,727, contra 0,520 da regra.
  - Entre quem **usou**, separa melhor quem vai parar: ROC-AUC de 0,917 contra 0,728.
- **Ter licença não indica uso.** Como previsor, a licença vigente tem ROC-AUC de 0,493, igual a um sorteio. Na base, 34,6% do uso de ferramentas por assento vem de quem não tem licença da ferramenta.
- **Com o mesmo número de assentos, a realocação usa melhor as licenças.**
  - O aproveitamento (assentos usados no mês) sobe de **46% para 59%**.
  - A cobertura (usuários com assento) sobe de **68% para 87%**.
  - O máximo possível de aproveitamento seria 68%, porque há mais assentos do que usuários.
- **Recomendação para agosto de 2026:**
  - **1.707** dos 2.880 assentos ativos têm probabilidade de uso abaixo de 20%, custando cerca de **R$ 219 mil por mês**.
  - **2.032** pares colaborador × ferramenta têm uso provável (acima de 80%) e nenhum assento.
  - Os assentos ociosos estão sobretudo na indústria, no varejo e no financeiro. O uso sem assento está na tecnologia e no marketing.
- **O resultado não depende do corte.** Reajustado mês a mês de fevereiro a julho de 2026, o modelo supera a regra simples em todos os meses (PR-AUC de 0,987 a 0,993, contra 0,950 a 0,965).

## Arquivos

| Arquivo | Conteúdo |
|---|---|
| `modelo_uso_ferramentas.ipynb` | Notebook executado: diagnóstico da base, variáveis, validação temporal, experimentos, avaliação, interpretação e recomendação |
| `resultados/diagnostico_base.csv` | Dimensões, nulos, duplicatas, ausências disfarçadas e cardinalidade de cada tabela |
| `resultados/experimentos_validacao.csv` | As 16 configurações testadas (3 famílias) com as métricas na validação |
| `resultados/metricas_teste.csv` | Modelos e regras simples no teste |
| `resultados/metricas_subgrupos.csv` | Desempenho por subgrupo (não usou no mês, férias, recém-contratados, com e sem licença) |
| `resultados/backtest_mensal.csv` | Reajuste e avaliação mês a mês, de fev a jul/2026 |
| `resultados/importancia_permutacao.csv` | Importância de cada variável no teste |
| `resultados/simulacao_realocacao_teste.csv` | Aproveitamento e cobertura com a alocação atual, a regra simples e o modelo |
| `resultados/recomendacao_ago2026.csv` | Uma linha por colaborador × ferramenta, com probabilidade de uso em agosto e recomendação |
| `resultados/recomendacao_ago2026_por_empresa.csv` | Resumo por empresa: assentos, usuários esperados, assentos a revisar e custo |
| `figuras/fig1` a `fig5` | Curvas ROC e precisão × recall, calibração, backtest mensal, importância e realocação |

Para rodar: Python 3 com `pandas`, `numpy`, `scikit-learn` e `matplotlib`, a partir desta pasta. Os dados vêm de `Deliverable1/machine-learning-ia/Data/csv`, a mesma base da Estatística e da Pesquisa Operacional. O notebook leva cerca de 7 minutos.

## 1. Do D1 ao D2

| | Deliverable 1 | Deliverable 2 |
|---|---|---|
| Pergunta | Quem tem alto uso de IA no período inteiro | Quem vai usar cada ferramenta no mês seguinte |
| Unidade | Colaborador (2.356 linhas) | Colaborador × ferramenta × mês (83.340 linhas) |
| Variáveis | Cargo, senioridade, departamento, setor, porte, tempo de casa | As mesmas, mais o **histórico de uso** (defasagens, recência, uso geral de IA), contexto do departamento, licença, governança, capacitação e calendário |
| Validação | Uma divisão aleatória 80/20 | **Temporal**: treino até mar/2026, validação em abr e mai, teste em jun e jul, mais o backtest mês a mês |
| Modelos | Regressão logística, normal e balanceada | Quatro regras simples e três famílias (logística, Random Forest, gradient boosting) com grade de hiperparâmetros |
| Métricas | Acurácia, recall, F1 | PR-AUC (principal), ROC-AUC, Brier, calibração, precisão, recall, F1 e subgrupos |
| Uso | Classificação | Recomendação de licenças e entrada para a Pesquisa Operacional |

As correções do D1 estão em `Deliverable1/machine-learning-ia/main_corrigido.ipynb`. A principal é o alvo, que media o tempo de exposição junto com a intensidade de uso.

## 2. Dados, alvo e variáveis

- **Linha:** colaborador ativo × ferramenta por assento usada ou licenciada pela empresa × mês *m*, a partir da contratação.
- **Alvo:** pelo menos um uso da ferramenta no mês *m*+1. A taxa é de 44%, então não há desbalanceamento a tratar.
- **Regra contra vazamento:** toda variável da linha *m* usa só informação até o fim de *m*.
- **Variáveis:**
  - **Histórico da ferramenta:** requisições em *m*, *m*−1 e *m*−2; meses com uso; recência.
  - **Uso geral de IA:** todas as ferramentas, inclusive as de consumo.
  - **Contexto:** fração dos colegas do departamento que usaram; licença vigente; liberação pela governança; cursos concluídos; tempo de casa.
  - **Calendário:** mês de férias.
  - **Categóricas:** senioridade, ferramenta, categoria, setor, porte e departamento.

O diagnóstico da base (pedido na pré-avaliação do D1) não encontrou duplicatas, ausências disfarçadas nem números negativos. Os nulos que existem são estruturais: contratos sem data de fim, aprovações sem lista de departamentos e cursos não concluídos. O tratamento de cada um está no notebook.

## 3. Validação e experimentos

| Bloco | Meses-alvo | Uso |
|---|---|---|
| Treino | jul/2025 a mar/2026 | Ajuste |
| Validação | abr e mai/2026 | Escolha da configuração e do limiar de decisão |
| Teste | jun e jul/2026 | Avaliação única do modelo final |

**Métrica principal: PR-AUC.** A decisão é entregar assentos a quem tem maior probabilidade de uso, então o que importa é a precisão no topo do ranking. O **Brier** entra porque a probabilidade vira valor esperado na Pesquisa Operacional. O limiar de decisão maximiza o F1 na validação, porque os dois erros têm custo da mesma ordem:
- **Falso negativo:** produtividade perdida.
- **Falso positivo:** um assento pago sem uso.

As 16 configurações ficam muito próximas (PR-AUC de 0,9885 a 0,9908 na validação). O **gradient boosting** (`learning_rate=0,05`, `max_leaf_nodes=15`, `l2=1,0`) foi escolhido na validação e não foi trocado depois do teste.

## 4. Resultados no teste (jun e jul/2026)

| Modelo ou regra | PR-AUC | ROC-AUC | Brier | Precisão | Recall | F1 |
|---|---:|---:|---:|---:|---:|---:|
| **Gradient boosting (escolhido)** | **0,991** | **0,993** | **0,031** | 0,942 | 0,969 | 0,955 |
| Random Forest | 0,991 | 0,993 | 0,031 | 0,924 | 0,988 | 0,955 |
| Regressão logística | 0,990 | 0,992 | 0,035 | 0,933 | 0,973 | 0,952 |
| Persistência (usou no mês) | 0,958 | 0,977 | 0,051 | 0,939 | 0,944 | 0,942 |
| Taxa do departamento | 0,727 | 0,800 | 0,183 | 0,714 | 0,729 | 0,721 |
| Licença vigente | 0,462 | 0,493 | 0,520 | 0,460 | 0,677 | 0,548 |
| Classe majoritária | 0,466 | 0,500 | 0,466 | — | 0,000 | — |

| Subgrupo | Linhas | Taxa de uso | PR-AUC modelo | PR-AUC persistência |
|---|---:|---:|---:|---:|
| Não usou no mês *m* | 7.246 | 4,9% | **0,727** | 0,520 |
| Usou no mês *m* | 6.379 | 93,9% | 0,994 | 0,966 |
| Julho (férias) | 6.821 | 46,1% | 0,989 | 0,951 |
| Até 6 meses de casa | 972 | 37,6% | 0,980 | 0,918 |

**O que o modelo usa.**
- O **uso geral de IA nos últimos três meses** é, de longe, a variável mais importante.
- Depois vêm a ferramenta, o uso dela no mês e a recência.
- Licença, governança e cursos não pesam. Na base, licença e uso são independentes, e a Estatística Multivariada não encontrou efeito da capacitação no uso depois dos controles (H1).

## 5. Aplicação: recomendação de licenças

| Regra de alocação (mesmo número de assentos) | Aproveitamento | Cobertura dos usuários |
|---|---:|---:|
| Atual (licença vigente) | 46,0% | 67,7% |
| Persistência | 58,2% | 85,7% |
| **Modelo** | **58,8%** | **86,5%** |

Para agosto de 2026, `resultados/recomendacao_ago2026.csv` classifica cada colaborador × ferramenta em três grupos:

- **Manter.**
- **Revisar** (com assento e probabilidade abaixo de 20%): 1.707 assentos, cerca de R$ 219 mil por mês.
- **Considerar** (sem assento e probabilidade acima de 80%): 2.032 pares.

A decisão de quem recebe cada assento, dentro do orçamento e das regras de governança, é do [modelo da Pesquisa Operacional](../07_Pesquisa_Operacional_e_Calculo_Aplicado/). A probabilidade deste modelo pode substituir o valor histórico que a PO usa hoje.

## 6. Limitações

- **A base é sintética.** O nível de atividade de cada pessoa é fixo durante o período, por isso o uso é tão persistente e as métricas são tão altas. Em dados reais, espera-se mais variação.
- **Licença e uso são independentes na base.** Numa empresa real, sem licença não há uso, e o ganho da realocação seria menor do que o simulado.
- **O modelo prevê se a pessoa usa, não quanto.** A próxima versão pode prever o volume, que é o valor em usuários-equivalentes usado na Pesquisa Operacional.

## Apoio

Desenvolvimento do notebook e redação com apoio do Claude.
