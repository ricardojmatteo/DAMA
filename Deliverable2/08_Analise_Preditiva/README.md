# Análise Preditiva — Deliverable 2

**Quanto as empresas vão usar e gastar com IA nos próximos seis meses?** Previsão mensal da demanda de IA (requisições) e do custo de consumo (tokens) das 18 empresas, de agosto de 2026 a janeiro de 2027, com validação por backtest e comparação com o orçamento de cada centro de custo.

**DAMA Intelligence** · Ana Luiza Proença, Gustavo Gomes, Kaique Lira e Ricardo Matteo · Ciência de Dados e Negócios, ESPM, 4º semestre, 2026

## Resumo dos resultados

- **Erro de 5,8%, menos de metade do ingênuo.**
  - O modelo escolhido é a **tendência linear por empresa × fatores de calendário comuns**.
  - No backtest com 7 origens, ele erra **5,8%** da demanda (WAPE): de 5,6% a 6,1% entre 1 e 3 meses à frente, com viés de −1,1%.
  - Repetir o último mês erra 17,5%, e o gradient boosting global, 16,5%.
- **O calendário explica as quedas.** As 18 empresas juntas mostram o mesmo efeito de férias:
  - dezembro em 58% da tendência;
  - janeiro em 81%;
  - julho em 86%.
- **Faixas de previsão honestas.** A faixa de 80%, calibrada nas primeiras origens, cobre **89%** dos casos nas origens seguintes.
- **A demanda total deve crescer e cair no fim do ano.**
  - Passa de 792 mil requisições em julho de 2026 para **979 mil em agosto** e **1,11 milhão em novembro**.
  - Cai para **670 mil em dezembro** e volta a **972 mil em janeiro**.
- **O consumo pesa pouco no custo.** Os tokens de APIs e plataformas custam cerca de R$ 8 a 9 mil por mês somando as 8 empresas que os usam, menos de 2% do custo total. São previstos com WAPE de 14,7%, contra 25,2% do ingênuo.
- **Os estouros de orçamento estão nos centros de custo.**
  - **35 dos 161 centros de custo** devem ficar acima do orçamento, com excesso de cerca de **R$ 84 mil por mês**.
  - O maior é Engenharia de Software da NexoCode, com **348%**, o mesmo achado da Pesquisa Operacional.
  - Somando por empresa, só ServPro (107%) e Banco Alterosa (101%) passam do total. A soma esconde onde está o problema.
- **D1 corrigido.** A pergunta do D1 (quem vai ter incidente de segurança) foi refeita conforme o feedback da pré-avaliação, incluindo o corte temporal (preditores até dez/2025, alvo em jan a jul/2026). Sobram 31 casos em 2.039 colaboradores. O pouco sinal vem da unidade (departamento e empresa), não da pessoa: cursos e tokens têm ROC-AUC de 0,542, e no teste o modelo fica no nível do sorteio. Por isso o D2 mudou de pergunta.

## Arquivos

| Arquivo | Conteúdo |
|---|---|
| `previsao_demanda_custo.ipynb` | Notebook executado: diagnóstico das séries, modelos, backtest, intervalos, previsões, custo e orçamento |
| `resultados/diagnostico_series.csv` | Início do uso, meses de histórico e variação de cada empresa |
| `resultados/backtest_demanda.csv` | Cada previsão do backtest: origem, empresa, modelo, horizonte, real e previsto |
| `resultados/metricas_backtest_demanda.csv` | WAPE por horizonte, sMAPE, viés e MAE dos 6 modelos |
| `resultados/erro_por_empresa.csv`, `erro_por_mes.csv` | Erro do modelo escolhido por empresa e por mês previsto |
| `resultados/previsao_demanda_ago2026_jan2027.csv` | Previsão por empresa e mês, com faixa de 80% |
| `resultados/backtest_tokens_consumo.csv` | Backtest dos tokens de consumo |
| `resultados/previsao_custo_consumo.csv` | Tokens e custo de consumo previstos (R$), com faixa |
| `resultados/orcamento_previsto_por_centro_de_custo.csv` | Orçamento, assentos, consumo previsto, % do orçamento e situação dos 161 centros de custo |
| `resultados/orcamento_previsto_por_empresa.csv` | O mesmo, somado por empresa |
| `figuras/fig1` a `fig5` | Demanda total, erro por horizonte, previsão total, previsão de seis empresas, centros de custo acima do orçamento |

Para rodar: Python 3 com `pandas`, `numpy`, `scikit-learn`, `statsmodels` e `matplotlib`, a partir desta pasta. Os dados vêm de `Deliverable1/machine-learning-ia/Data/csv`. O notebook leva poucos segundos.

## 1. Do D1 ao D2

| | Deliverable 1 | Deliverable 2 |
|---|---|---|
| Pergunta | Quem vai ter um incidente de segurança | Qual será a demanda e o custo de IA de cada empresa nos próximos meses |
| Dados | 69 eventos de segurança; com o corte temporal, 31 incidentes em 2.039 colaboradores | 14 meses de uso de 18 empresas (269.515 eventos) e tokens de 8 empresas |
| Modelo | Random Forest | 6 modelos de série temporal, do ingênuo ao gradient boosting global |
| Validação | Nenhuma (o notebook não rodava) → na versão corrigida, validação cruzada estratificada repetida | Backtest com origem móvel (7 origens × 18 empresas × 3 horizontes) e intervalos calibrados |
| Resultado | ROC-AUC de 0,701 na validação cruzada e 0,644 no teste; o sinal é a unidade, não a pessoa | WAPE de 5,8%; previsão até jan/2027; centros de custo em risco |

A versão corrigida do D1 está em `Deliverable1/AnalisePreditiva/DAMAAnalisePreditiva1_corrigido.ipynb`. Ela atende às 19 tarefas do épico SMAN-78 e ao ponto de rigor temporal do feedback:
- diagnóstico da base;
- baseline da classe majoritária;
- ROC-AUC, PR-AUC e justificativa pelo custo do erro;
- `class_weight`, sobreamostragem e subamostragem só no treino, comparadas com validação cruzada;
- decisões do pré-processamento documentadas, incluindo a ordem da senioridade;
- corte temporal em 01/01/2026, com checagem variável a variável do que existia antes da previsão.

## 2. Modelos e validação

| Modelo | WAPE 1 mês | WAPE 2 meses | WAPE 3 meses | WAPE geral | Viés |
|---|---:|---:|---:|---:|---:|
| **Tendência × fatores comuns** | **5,8%** | **5,6%** | **6,1%** | **5,8%** | −1,1% |
| Tendência + férias (por empresa) | 6,8% | 7,3% | 8,4% | 7,4% | −5,7% |
| Gradient boosting global | 15,2% | 16,4% | 18,4% | 16,5% | −6,8% |
| Ingênuo (último mês) | 12,8% | 18,6% | 22,4% | 17,5% | −14,4% |
| Holt amortecido | 13,6% | 18,6% | 22,6% | 17,8% | −8,4% |
| Média móvel de 3 meses | 16,0% | 22,0% | 27,3% | 21,2% | −19,8% |

- **Backtest com origem móvel.** Em cada origem, de dez/2025 a jun/2026, os modelos veem só os dados até ali e preveem de 1 a 3 meses à frente. São 262 previsões por modelo.
- **Fatores de calendário comuns.** Cada empresa viu um único dezembro, então o efeito das férias é estimado com as 18 juntas: mediana da razão entre o observado e a tendência da própria empresa. Só entram meses de férias já observados até a origem.
- **Séries curtas.** Com menos de 6 meses de histórico, uma tendência extrapola a oscilação da adoção. Nesses casos, o modelo usa o nível recente vezes o calendário.
- **Por que o gradient boosting perde.** Com 14 meses por empresa, não há histórico para aprender a dinâmica a partir de defasagens. Modelos com estrutura explícita (tendência e calendário) levam vantagem com séries curtas.
- **Onde o modelo erra mais.**
  - Em **janeiro de 2026** (13,9%): era o primeiro janeiro da base, e nenhum modelo podia conhecer a queda.
  - Na **TextilNorte**, que começou a usar IA em abril de 2026.
  - Nas empresas grandes, o erro fica entre 3,8% e 4,7%.

## 3. Previsão da demanda (soma das 18 empresas)

| Mês | Previsto | Faixa de 80% |
|---|---:|---:|
| ago/2026 | 979 mil | 853 mil a 1,15 milhão |
| set/2026 | 1,02 milhão | 933 mil a 1,24 milhão |
| out/2026 | 1,07 milhão | 975 mil a 1,40 milhão |
| nov/2026 | 1,11 milhão | 1,00 milhão a 1,51 milhão |
| dez/2026 | 670 mil | 597 mil a 947 mil |
| jan/2027 | 972 mil | 857 mil a 1,42 milhão |

A faixa do total soma as faixas das empresas, o que supõe que todas erram juntas. É uma leitura conservadora. De 4 a 6 meses à frente, a faixa é alargada pela raiz do horizonte, porque o backtest só valida até 3 meses.

## 4. Custo e orçamento

- **Consumo.** O custo vem dos logs de uso multiplicados pelo preço por token, não das faturas.
  - 4,5% dos lançamentos de consumo não têm fatura. Onde a fatura existe, ela fica entre −4% e +5% do calculado.
  - Os tokens são previstos por empresa e multiplicados pelo preço médio da empresa em mai a jul/2026. O câmbio é de R$ 5,50 por US$, a mesma premissa da Pesquisa Operacional.
- **Assentos.** São contratuais e não se preveem com série histórica. A premissa é manter os assentos e o preço de jul/2026. Realocar licenças, como propõem o [Machine Learning](../05_Machine_Learning/) e a [Pesquisa Operacional](../07_Pesquisa_Operacional_e_Calculo_Aplicado/), reduz esse valor.
- **Orçamento.** Média mensal orçada em 2026, por centro de custo.

| Centro de custo | Orçamento mensal | Custo previsto | % do orçamento |
|---|---:|---:|---:|
| NexoCode · Engenharia de Software | R$ 6.747 | R$ 23.475 | 348% |
| Química Bandeirantes · Produção | R$ 2.538 | R$ 7.030 | 277% |
| Fincredi · Atendimento ao Cliente | R$ 888 | R$ 2.299 | 259% |
| ByteFlow · Qualidade (QA) | R$ 527 | R$ 1.338 | 254% |
| Estrela Marketing · Atendimento a Clientes (Account) | R$ 2.031 | R$ 4.900 | 241% |

Os assentos são 98% do custo previsto. **O estouro vem de como as licenças estão distribuídas, não do consumo.**

## 5. Limitações

- **Histórico curto.** São 14 meses e um ciclo anual. Os fatores de dezembro e janeiro vêm de uma observação por empresa.
- **Base sintética.** Tendência e calendário seguem regras simples na geração, o que favorece modelos com a mesma estrutura. Dados reais terão choques que nenhum destes modelos antecipa.
- **Assentos constantes.** A base registra só o status atual de cada licença. Renovação, expiração e realocação de contratos mudam o custo de assentos.

**Próximo passo no produto:** a previsão por centro de custo pode virar o alerta "orçamento de IA em risco" nas análises de gestão do DAMA. Ela também pode entrar na restrição de orçamento da Pesquisa Operacional no lugar da média histórica.

## Apoio

Desenvolvimento do notebook e redação com apoio do Claude.
