# Análise Preditiva — Deliverable 2

**Quanto as empresas vão usar e gastar com IA nos próximos seis meses?** Previsão mensal da demanda de IA (requisições) e do custo de consumo (tokens) das 18 empresas, de agosto de 2026 a janeiro de 2027, com validação por backtest e um diagnóstico do orçamento de cada centro de custo.

**DAMA Intelligence** · Ana Luiza Proença, Gustavo Gomes, Kaique Lira e Ricardo Matteo · Ciência de Dados e Negócios, ESPM, 4º semestre, 2026

## Resumo dos resultados

- **O modelo foi escolhido numa parte do backtest e conferido em outra.**
  - O modelo é a **tendência linear por empresa × fatores de calendário comuns**.
  - **Seleção** (previsões com alvo até abr/2026): erra 7,8% (WAPE), contra 8,3% da mesma tendência em log e 26,7% do ingênuo.
  - **Confirmação** (origens de abr a jun/2026, fora de qualquer escolha): **WAPE de 4,0%**, **sMAPE de 7,3%** e viés de +1,8%. O ingênuo erra 7,9%.
  - A confirmação não tem dezembro nem janeiro. Em janeiro de 2026, o erro foi de 13,9%.
- **O total esconde a diferença entre empresas.**
  - A NexoCode é 48% do volume e erra 2,2%. Sem ela, o WAPE vai a 5,8%.
  - O WAPE mediano por empresa é de 5,8%. As 3 maiores erram de 2,2% a 6,7%.
  - Cinco empresas pequenas passam de 10%, até 60,5% na TextilNorte, que começou a usar IA em abril de 2026.
  - Como o alerta de orçamento é por centro de custo, a margem que vale é a da empresa (tabela abaixo).
- **O crescimento é aditivo, não proporcional.**
  - Os resíduos da tendência linear passam nos testes de autocorrelação, heterocedasticidade e curvatura.
  - A versão em log deixa curvatura nos resíduos de NexoCode, Vortice e Piloto e superestima 7,4% na confirmação.
  - Ressalva: a Vortice desacelera, e a reta tende a superestimá-la.
- **O calendário explica as quedas.** As 18 empresas juntas mostram o mesmo efeito de férias:
  - dezembro em 58% da tendência;
  - janeiro em 81%;
  - julho em 86%.
- **Faixa de 80% honesta no conjunto, desigual por horizonte.** Calibrada na seleção, cobre 79% da confirmação: 92% a 1 mês, 70% a 2 meses e 56% a 3 meses.
- **A demanda total deve crescer e cair no fim do ano.**
  - Passa de 792 mil requisições em julho de 2026 para **979 mil em agosto** e **1,11 milhão em novembro**.
  - Cai para **670 mil em dezembro** e volta a **972 mil em janeiro**.
  - Quase metade do salto da NexoCode de julho a novembro (373 mil → 531 mil) é a volta das férias de julho. O resto são 22 mil requisições a mais por mês.
- **O consumo pesa pouco no custo.** Os tokens de APIs e plataformas custam cerca de R$ 8 a 9 mil por mês somando as 8 empresas que os usam, menos de 2% do custo total. São previstos com WAPE de 10,8% na confirmação, contra 18,5% do ingênuo, e têm faixa própria.
- **O orçamento é um retrato, com uma parte prevista pequena.**
  - Com os assentos e o consumo de julho de 2026, **35 dos 161 centros de custo** já estão acima do orçamento, com excesso de cerca de **R$ 83 mil por mês**.
  - Os assentos, mantidos constantes, são 98% do custo projetado. O consumo previsto muda a situação de **1 centro**.
  - O maior é Engenharia de Software da NexoCode, com **347%**, o mesmo achado da Pesquisa Operacional.
  - Somando por empresa, só ServPro (107%) e Banco Alterosa (101%) passam do total. A soma esconde onde está o problema.
- **D1 corrigido.** A pergunta do D1 (quem vai ter incidente de segurança) foi refeita conforme a pré-avaliação, incluindo o corte temporal, e revisada após o checkpoint: a validação cruzada e o limiar agora usam só o treino, e o teste é usado uma vez. No teste, o modelo tem PR-AUC de 0,025 (sorteio: 0,015) e acha 5 dos 9 incidentes sinalizando 31% dos colaboradores. Com 22 casos no treino, nem dá para dizer de onde vem o pouco sinal. Por isso o D2 mudou de pergunta.

**Erro por empresa na confirmação** (tendência × fatores comuns; peso = participação na demanda real da confirmação)

| Empresa | Peso no volume | WAPE | Viés | WAPE do ingênuo |
|---|---:|---:|---:|---:|
| NexoCode Solucoes | 47,5% | 2,2% | +0,6% | 8,5% |
| Vortice Software | 18,7% | 5,0% | +5,0% | 4,8% |
| Cronos Tech Labs | 8,1% | 6,7% | +6,7% | 5,6% |
| Banco Alterosa S.A. | 3,6% | 1,8% | −1,0% | 7,1% |
| Estrela Marketing Digital | 3,4% | 5,3% | −1,1% | 10,5% |
| MercadoPrime E-commerce | 3,3% | 5,6% | −1,9% | 2,7% |
| Prisma Consultoria Empresarial | 3,1% | 7,2% | +3,6% | 11,2% |
| Piloto Digital Sistemas | 3,0% | 9,2% | +1,2% | 17,4% |
| Ideia Fresca Marketing | 2,4% | 2,8% | −1,7% | 6,3% |
| Compra Certa Varejo | 1,7% | 1,7% | +0,7% | 3,7% |
| ByteFlow Engenharia de Software | 1,6% | 11,7% | −2,4% | 19,7% |
| Quimica Bandeirantes | 0,9% | 5,9% | −0,3% | 5,7% |
| Metalurgica Vale Forte | 0,8% | 3,9% | −1,7% | 6,3% |
| Capital Sul Investimentos | 0,6% | 16,4% | +15,2% | 14,0% |
| Lojas Formosa | 0,6% | 5,6% | +2,3% | 12,5% |
| Fincredi Solucoes Financeiras | 0,5% | 16,6% | −1,9% | 13,7% |
| ServPro Facilities | 0,1% | 18,3% | +12,4% | 28,0% |
| TextilNorte Manufatura | 0,0% | 60,5% | +60,5% | 60,5% |
| **Total (WAPE)** | 100% | **4,0%** | +1,8% | 7,9% |
| **sMAPE** (mesmo peso por previsão) | | **7,3%** | | 10,0% |

## Revisão após o feedback do checkpoint

| Ponto | O que mudou | Onde |
|---|---|---|
| 1. Diagnóstico da regressão de tendência | Resíduos plotados por empresa (`fig6`); Ljung-Box, Durbin-Watson, Breusch-Pagan e teste de curvatura; versão em log incluída como modelo e comparada no backtest; extrapolação da NexoCode decomposta em calendário e crescimento | Notebook, seções 4, 5.4 e 6.1; `resultados/diagnostico_residuos.csv` |
| 2. Headline esconde a heterogeneidade | sMAPE, WAPE sem a NexoCode, WAPE mediano e tabela por empresa no resumo acima | Seção 5.3 e resumo |
| 3. Seleção e teste misturados | D2: backtest dividido no tempo, com escolha do modelo e calibração da faixa só com alvos até abr/2026 e confirmação nas origens de abr a jun/2026; os tokens seguem a mesma regra e ganharam faixa própria. D1: validação cruzada, ablação e limiar só no treino | Seções 5.2, 5.3, 5.5 e 7; `Deliverable1/08_Analise_Preditiva` |
| 4. Explicar os 262 | Grade de 7 × 18 × 3 = 378 com o motivo de cada descarte | Seção 5.1 |
| 5. Orçamento é retrato | Assentos (retrato de jul/2026) e consumo (previsto, com faixa de 80%) separados; situação atual e projetada lado a lado | Seção 8; `fig5` |
| 6. Reprodutibilidade | Localização automática dos dados, variável `DAMA_DADOS` e pasta `dados/`; instruções abaixo | Seção 2; "Como rodar" |

## Arquivos

| Arquivo | Conteúdo |
|---|---|
| `previsao_demanda_custo.ipynb` | Notebook executado: diagnóstico das séries, modelos, backtest, resíduos, intervalos, previsões, custo e orçamento |
| `resultados/diagnostico_series.csv` | Início do uso, meses de histórico e variação de cada empresa |
| `resultados/backtest_demanda.csv` | Cada previsão do backtest: origem, empresa, modelo, horizonte, real, previsto e parte (seleção, confirmação ou fora) |
| `resultados/metricas_backtest_demanda.csv` | WAPE por horizonte, sMAPE, viés e MAE dos 7 modelos, na seleção e na confirmação |
| `resultados/erro_por_empresa.csv`, `erro_por_mes.csv` | Erro do modelo escolhido por empresa (peso no volume, seleção, confirmação, viés) e por mês previsto |
| `resultados/diagnostico_residuos.csv` | Testes dos resíduos das tendências linear e em log, por empresa |
| `resultados/previsao_demanda_ago2026_jan2027.csv` | Previsão por empresa e mês, com faixa de 80% |
| `resultados/backtest_tokens_consumo.csv` | Backtest dos tokens de consumo |
| `resultados/previsao_custo_consumo.csv` | Tokens e custo de consumo previstos (R$), com faixa de 80% própria |
| `resultados/orcamento_previsto_por_centro_de_custo.csv` | Diagnóstico dos 161 centros de custo: orçamento, assentos de jul/2026 (retrato), consumo de jul/2026, consumo previsto com faixa, % do orçamento atual e projetado, situação atual e projetada |
| `resultados/orcamento_previsto_por_empresa.csv` | O mesmo, somado por empresa |
| `figuras/fig1` a `fig6` | Demanda total, erro por horizonte na seleção e na confirmação, previsão total, previsão de seis empresas, centros de custo acima do orçamento (retrato + previsão), resíduos por empresa |

## Como rodar

Python 3 com `pandas`, `numpy`, `scikit-learn`, `statsmodels` e `matplotlib`. O notebook leva poucos segundos.

Os dados **não ficam nesta pasta**: são os CSVs da base sintética em `Deliverable1/05_Machine_Learning/Data/csv`. O notebook usa 7 deles: `ai_usage_logs`, `companies`, `ai_tools`, `ai_models`, `ai_billing`, `ai_budgets` e `departments`. Ele procura a pasta sozinho, nesta ordem:

1. **Variável de ambiente `DAMA_DADOS`** com o caminho da pasta dos CSVs. Por exemplo, no PowerShell: `$env:DAMA_DADOS = "C:\caminho\DAMA\Deliverable1\05_Machine_Learning\Data\csv"`, e depois abrir o Jupyter no mesmo terminal.
2. **Pasta `dados/` ao lado do notebook**, com os 7 CSVs copiados. Serve para quem baixou só esta pasta.
3. **Repositório completo:** procura `Deliverable1/05_Machine_Learning/Data/csv` a partir da pasta do notebook e de cada pasta acima dela.

Se não encontrar, a primeira célula para com uma mensagem que lista os arquivos e as três opções. A pasta usada aparece na saída da célula.

## 1. Do D1 ao D2

| | Deliverable 1 | Deliverable 2 |
|---|---|---|
| Pergunta | Quem vai ter um incidente de segurança | Qual será a demanda e o custo de IA de cada empresa nos próximos meses |
| Dados | 69 eventos de segurança; com o corte temporal, 31 incidentes em 2.039 colaboradores | 14 meses de uso de 18 empresas (269.515 eventos) e tokens de 8 empresas |
| Modelo | Random Forest | 7 modelos de série temporal, do ingênuo ao gradient boosting global |
| Validação | Nenhuma (o notebook não rodava) → na versão corrigida, validação cruzada estratificada repetida só no treino, limiar escolhido no treino e teste usado uma vez | Backtest com origem móvel (262 previsões por modelo), dividido em seleção e confirmação, e intervalos calibrados na seleção |
| Resultado | ROC-AUC de 0,694 na validação cruzada do treino e 0,644 no teste; PR-AUC de 0,025 no teste (sorteio: 0,015) | WAPE de 4,0% e sMAPE de 7,3% na confirmação; previsão até jan/2027; diagnóstico dos centros de custo |

A versão corrigida do D1 está em `Deliverable1/08_Analise_Preditiva/DAMAAnalisePreditiva1_corrigido.ipynb`. Ela atende às 19 tarefas do épico SMAN-78, ao ponto de rigor temporal da pré-avaliação e ao ponto 3 do checkpoint:
- diagnóstico da base;
- baseline da classe majoritária;
- ROC-AUC, PR-AUC e justificativa pelo custo do erro;
- `class_weight`, sobreamostragem e subamostragem só no treino de cada dobra, comparadas com validação cruzada;
- decisões do pré-processamento documentadas, incluindo a ordem da senioridade;
- corte temporal em 01/01/2026, com checagem variável a variável do que existia antes da previsão;
- **divisão treino/teste antes de qualquer escolha:** validação cruzada, ablação e limiar só no treino (1.427 colaboradores, 22 incidentes); o teste (612, 9 incidentes) é usado uma vez.

A separação mudou duas leituras do D1:
- **O limiar.** Escolhido na curva do teste, dava recall de 0,67 (6 de 9). Escolhido no treino, dá 0,56 (5 de 9), com 31% dos colaboradores sinalizados e precisão de 2,6%.
- **A origem do sinal.** Com a validação cruzada na base inteira, o departamento dava ROC-AUC de 0,704 e cursos e tokens, 0,542. Só no treino, os dois empatam (0,657 e 0,658, com desvio de 0,09). A leitura "o sinal é da unidade, não da pessoa" não se sustenta com 22 casos. A conclusão de que o modelo não serve para decidir sobre pessoas continua.

## 2. Modelos e validação

O backtest usa origens de dez/2025 a jun/2026. Em cada uma, os modelos veem só os dados até ali e preveem de 1 a 3 meses à frente.

**De 378 para 262 previsões por modelo.** A grade é de 7 origens × 18 empresas × 3 horizontes = 378. Saem:
- **54** com alvo depois de jul/2026, ainda não observado: 3 por empresa (origem de mai/2026 a 3 meses; origem de jun/2026 a 2 e 3 meses);
- **62** de empresas com menos de 3 meses de uso na origem: TextilNorte (17), ServPro (15), Capital Sul (12), Lojas Formosa (9), ByteFlow (6) e Metalúrgica Vale Forte (3).

Sobram 262 (105 a 1 mês, 87 a 2 meses e 70 a 3 meses), as mesmas para todos os modelos.

**Seleção e confirmação.** A escolha é feita como seria no fim de abril de 2026:

| Parte | Previsões por modelo | Uso |
|---|---:|---|
| Seleção: origens de dez/2025 a mar/2026, alvo até abr/2026 | 118 | Escolher o modelo e calibrar a faixa de 80% |
| Confirmação: origens de abr a jun/2026, alvos de mai a jul/2026 | 100 | Medir erro e cobertura do modelo escolhido |
| Fora: origens de fev e mar/2026 com alvo em mai ou jun/2026 | 44 | Não entram, porque o alvo cai no período da confirmação |

| Modelo | WAPE na seleção | WAPE na confirmação | 1 mês | 2 meses | 3 meses | sMAPE | Viés |
|---|---:|---:|---:|---:|---:|---:|---:|
| **Tendência × fatores comuns** | **7,8%** | **4,0%** | 3,9% | 4,4% | 3,8% | 7,3% | +1,8% |
| Tendência log × fatores comuns | 8,3% | 8,3% | 7,1% | 8,8% | 10,8% | 9,4% | +7,4% |
| Tendência + férias (por empresa) | 10,9% | 4,4% | 3,9% | 5,0% | 4,6% | 9,3% | −2,5% |
| Holt amortecido | 24,8% | 13,0% | 9,6% | 13,4% | 23,1% | 14,8% | +11,0% |
| Ingênuo (último mês) | 26,7% | 7,9% | 8,6% | 9,2% | 2,7% | 10,0% | −0,6% |
| Gradient boosting global | 27,9% | 6,0% | 6,0% | 6,8% | 4,8% | 10,3% | +4,4% |
| Média móvel de 3 meses | 29,2% | 8,8% | 9,1% | 9,5% | 6,5% | 11,5% | −4,8% |

Colunas de 1 a 3 meses, sMAPE e viés: confirmação.

- **A confirmação é um período fácil.** De maio a julho só há o mês de férias de julho. Todos os modelos erram menos que na seleção, que inclui dezembro e janeiro. O ingênuo a 3 meses (2,7%) é uma única origem (abr → jul), em que três meses de crescimento e a queda de julho se compensam.
- **Fatores de calendário comuns.** Cada empresa viu um único dezembro, então o efeito das férias é estimado com as 18 juntas: mediana da razão entre o observado e a tendência da própria empresa. Só entram meses de férias já observados até a origem.
- **Séries curtas.** Com menos de 6 meses de histórico, uma tendência extrapola a oscilação da adoção. Nesses casos, o modelo usa o nível recente vezes o calendário.
- **Por que o gradient boosting perde na seleção.** Com poucos meses por empresa, não há histórico para aprender a dinâmica a partir de defasagens. Modelos com estrutura explícita (tendência e calendário) levam vantagem com séries curtas.
- **Onde o modelo erra mais.**
  - Em **janeiro de 2026** (13,9%): era o primeiro janeiro da base, e nenhum modelo podia conhecer a queda.
  - Nas empresas que começaram a usar IA em 2026 (Capital Sul, ServPro e TextilNorte) e nas de menor volume (ByteFlow, Fincredi).
  - Nas 3 maiores, o erro fica entre 2,2% e 6,7%. Vortice e Cronos são superestimadas (+5,0% e +6,7%).

**Diagnóstico dos resíduos (ponto 1).** As tendências linear e em log foram ajustadas com o histórico inteiro, nas 16 empresas com pelo menos 6 meses de uso.

| Teste (p < 0,05, salvo indicação) | Linear (aditiva) | Log (proporcional) |
|---|---:|---:|
| Autocorrelação (Ljung-Box, até 2 defasagens) | 0 de 16 | 1 de 16 |
| Durbin-Watson abaixo de 1,2 | 1 (Vortice) | 2 (Vortice, Piloto) |
| Heterocedasticidade no tempo (Breusch-Pagan) | 2 (Piloto, Fincredi) | 1 (Estrela) |
| Curvatura côncava (t² < 0) | 1 (Vortice) | 3 (NexoCode, Vortice, Piloto) |
| Curvatura convexa (t² > 0) | 0 | 0 |

Por acaso, espera-se cerca de 0,8 rejeição por linha. A tendência linear não deixa padrão nos resíduos. A versão em log deixa curvatura côncava justamente nas empresas que crescem há mais tempo, o formato de um crescimento aditivo, e superestima 7,4% na confirmação. A Vortice é a exceção: desacelera mesmo na escala linear, e a reta tende a superestimá-la.

**Intervalos (ponto 3).** A faixa de 80% são os quantis 10% e 90% de log(real/previsto), por horizonte.
- Calibrada só na seleção, cobre 79% da confirmação: 92% a 1 mês, 70% a 2 meses e 56% a 3 meses.
- A seleção tem poucas origens a 2 e 3 meses (3 e 2), e elas incluem o primeiro janeiro, quando o modelo subestimou. A faixa saiu deslocada para cima.
- Para o futuro, os quantis são recalculados com as 7 origens, como o modelo é reajustado com os 14 meses: de −13% a +17% a 1 mês e de −9% a +31% a 3 meses. Essa faixa só pode ser testada com os meses que vierem.

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

**Aditivo ou proporcional, na prática.** A NexoCode passa de 373 mil requisições em julho para 531 mil em novembro:
- 70 mil são a volta das férias de julho (sem o fator de calendário, a tendência de julho já é de 442 mil);
- 88 mil são 4 meses de crescimento de 22 mil requisições por mês, ou 5,0% ao mês em agosto e 4,0% em janeiro.

Com crescimento proporcional (8,2% ao mês), novembro iria a 648 mil, e o total das 18 empresas, a 1,34 milhão. Os resíduos e a confirmação descartam essa hipótese.

## 4. Custo e orçamento

- **Consumo.** O custo vem dos logs de uso multiplicados pelo preço por token, não das faturas.
  - 4,5% dos lançamentos de consumo não têm fatura. Onde a fatura existe, ela fica entre −4% e +5% do calculado.
  - Os tokens são previstos por empresa (tendência + férias, escolhido na seleção) e multiplicados pelo preço médio da empresa em mai a jul/2026. O câmbio é de R$ 5,50 por US$, a mesma premissa da Pesquisa Operacional.
  - A faixa de 80% do consumo sai dos erros do próprio modelo de tokens (de −34% a +75% a 1 mês) e cobre 81% da confirmação. Antes, usava a faixa da demanda, bem mais estreita.
- **Assentos.** São contratuais e não se preveem com série histórica. Entram como **retrato**: assentos e preço faturados em jul/2026, mantidos constantes. Realocar licenças, como propõem o [Machine Learning](../05_Machine_Learning/) e a [Pesquisa Operacional](../07_Pesquisa_Operacional_e_Calculo_Aplicado/), reduz esse valor.
- **Orçamento.** Média mensal orçada em 2026, por centro de custo.

**Diagnóstico atual com componente previsto.** O custo mensal de um centro de custo é o retrato dos assentos de jul/2026 mais o consumo previsto. Os assentos são 98,3% desse custo. Por isso o resultado é apresentado como diagnóstico:
- **Situação atual:** assentos e consumo de jul/2026. 35 centros acima do orçamento, 3 entre 90% e 100% e 123 dentro.
- **Situação projetada:** assentos de jul/2026 mais o consumo previsto (média de ago/2026 a jan/2027). Os mesmos 35 ficam acima, e só 1 centro muda de situação: Tecnologia da Informação da MercadoPrime, que passa do orçamento no limite superior da faixa do consumo.

| Centro de custo | Orçamento mensal | Assentos jul/2026 (retrato) | Consumo previsto (faixa de 80%) | % do orçamento: atual → projetado |
|---|---:|---:|---:|---:|
| NexoCode · Engenharia de Software | R$ 6.747 | R$ 23.281 | R$ 194 (123 a 312) | 347% → 348% |
| Química Bandeirantes · Produção | R$ 2.538 | R$ 7.030 | — | 277% → 277% |
| Fincredi · Atendimento ao Cliente | R$ 888 | R$ 2.299 | — | 259% → 259% |
| ByteFlow · Qualidade (QA) | R$ 527 | R$ 1.338 | — | 254% → 254% |
| Estrela Marketing · Atendimento a Clientes (Account) | R$ 2.031 | R$ 4.895 | R$ 5 (3 a 8) | 241% → 241% |

O estouro vem de como as licenças estão distribuídas, não do consumo. Um alerta "orçamento de IA em risco" construído sobre esta tabela é, hoje, sobretudo um diagnóstico. A parte prevista pesa mais onde o consumo é maior: Ideia Fresca (29% do custo projetado), MercadoPrime (9%) e Vortice (8%).

## 5. Limitações

- **Histórico curto.** São 14 meses e um ciclo anual. Os fatores de dezembro e janeiro vêm de uma observação por empresa.
- **Confirmação curta e fácil.** São 3 origens, sem dezembro e janeiro. O erro de 4,0% é o de meses comuns; nos meses de férias, a referência é o de janeiro de 2026 (13,9%).
- **Faixa por horizonte.** Calibrada com poucas origens, a faixa cobriu 56% a 3 meses na confirmação. A faixa final, com as 7 origens, ainda precisa ser acompanhada com dados novos.
- **Base sintética.** Tendência e calendário seguem regras simples na geração, o que favorece modelos com a mesma estrutura. Dados reais terão choques que nenhum destes modelos antecipa.
- **Assentos constantes.** A base registra só o status atual de cada licença. Renovação, expiração e realocação de contratos mudam o custo de assentos.

**Próximo passo no produto:** transformar a previsão por centro de custo no alerta "orçamento de IA em risco" do MVP, com a faixa de 80% exibida, e registrar a execução dessa tela como evidência de integração. A previsão também pode entrar na restrição de orçamento da Pesquisa Operacional no lugar da média histórica.

## Apoio

Desenvolvimento do notebook e redação com apoio do Claude.
