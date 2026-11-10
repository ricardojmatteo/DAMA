# Correções do Deliverable 1 — Machine Learning, Análise Preditiva, Estatística Multivariada e Pesquisa Operacional

**DAMA Intelligence** · Ana Luiza Proença, Gustavo Gomes, Kaique Lira e Ricardo Matteo · Ciência de Dados e Negócios, ESPM, 4º semestre, 2026

Para Machine Learning e Análise Preditiva, o `Deliverable1/` mantém só as versões corrigidas, com o sufixo `_corrigido`. Os notebooks originais estão no histórico do Git.

## Análise Preditiva — `Deliverable1/08_Analise_Preditiva/DAMAAnalisePreditiva1_corrigido.ipynb`

Correções feitas a partir do feedback da pré-avaliação, registrado no épico **SMAN-78** ("Correção — Análise Preditiva"), da revisão do notebook e do feedback do checkpoint do D2.

| Apontamento | Tarefas | O que foi feito |
|---|---|---|
| Faltava o diagnóstico da base | SMAN-65, 154 a 159 | Dimensões, nulos, cardinalidade, duplicatas e ausências disfarçadas das 4 tabelas, com tabela-resumo e tratamento de cada achado |
| Acurácia enganosa com o alvo desbalanceado | SMAN-61, 147 a 150 | Distribuição do alvo; baseline da classe majoritária (acurácia de 98,5%, recall 0); PR-AUC como métrica principal, ROC-AUC e recall de apoio; justificativa pelo custo do falso negativo, que é muito maior que o do falso positivo |
| Desbalanceamento não tratado | SMAN-62, 151 a 153 | `class_weight='balanced'`, sobreamostragem e subamostragem aplicadas **só no treino** de cada dobra, comparadas com validação cruzada estratificada repetida (5 × 5). Nenhuma melhora o ranking, então o desbalanceamento passou a ser tratado no limiar, que prioriza recall |
| Rigor temporal: preditores e alvo somados sobre o mesmo período | — (está no feedback, não no Jira) | Corte em 01/01/2026: cursos, tokens e incidentes anteriores medidos até 31/12/2025; alvo = incidente de jan a jul/2026; tabela variável a variável do que existia antes da previsão |
| Decisões do pré-processamento sem justificativa | SMAN-66, 160 a 164 | Seção dedicada: imputação, escalonamento, one-hot com `handle_unknown`, ordinal com ordem explícita e ajuste só no treino |
| *Encontrado na revisão:* o notebook não rodava | — | A célula da curva ROC usava `modelo`, que nunca era treinado. O pipeline agora é treinado e avaliado no próprio notebook |
| *Encontrado na revisão:* ordem da senioridade errada | SMAN-163 | A ordem tinha "Diretor", que não existe na base, e "Gerente" virava categoria desconhecida. Agora: Junior < Pleno < Senior < Especialista < Gerente |
| *Encontrado na revisão:* imputação pela mediana | SMAN-160 | Colaborador sem curso ou sem uso de IA tem zero, não dado faltante. A imputação passou a ser por zero |
| *Encontrado na revisão:* tokens incompletos | — | O total somava só os tokens de entrada. Agora soma entrada e saída |
| *Checkpoint do D2:* seleção e teste misturados | — (feedback do checkpoint) | A validação cruzada que escolhia a configuração rodava na base inteira, inclusive nas linhas do teste, e o limiar era escolhido na curva do teste. Agora a divisão treino/teste vem antes de tudo: validação cruzada, ablação e limiar (previsões fora da dobra) usam só o treino, e o teste é usado uma única vez |
| *Checkpoint do D2:* reprodutibilidade | — (feedback do checkpoint) | O notebook procura a pasta de dados sozinho: variável `DAMA_DADOS`, a própria pasta (Colab), `dados/` ou `Deliverable1/05_Machine_Learning/Data/csv` no repositório. Se não achar, diz o que fazer. Não há cópia do notebook em `00_Correcoes_Deliverable_1`: a única versão é a de `Deliverable1/08_Analise_Preditiva`, que lê `../05_Machine_Learning/Data/csv` |

**O que a correção revelou** (números da versão com escolha só no treino)
- Com o corte temporal, sobram 31 incidentes em 2.039 colaboradores (1,5%): 22 no treino (1.427 colaboradores) e 9 no teste (612).
- Na validação cruzada do treino, o melhor modelo tem PR-AUC de 0,070 ± 0,048 (sorteio: 0,015) e ROC-AUC de 0,694 ± 0,108. No teste, o ROC-AUC é 0,644 e a PR-AUC, 0,025.
- Com o limiar escolhido no treino, o modelo encontra 5 dos 9 incidentes do teste sinalizando 31% dos colaboradores, com precisão de 2,6%. Com o limiar escolhido na curva do teste, como antes, eram 6 de 9: o número anterior era otimista.
- A ablação no treino não separa as fontes de sinal: só com o departamento, ROC-AUC de 0,657; só com cursos e tokens, 0,658; só com incidentes anteriores, 0,508. Na versão anterior, com a validação cruzada na base inteira, eram 0,704, 0,542 e 0,480. A leitura "o sinal é da unidade, não da pessoa" dependia das linhas do teste e foi retirada.

O modelo não sustenta decisão sobre pessoas. Isso é coerente com a Estatística Multivariada, que não confirmou a hipótese H2. Por isso, no D2 a disciplina passou a prever demanda e custo de IA (`Deliverable2/08_Analise_Preditiva`).

## Machine Learning — `Deliverable1/05_Machine_Learning/main_corrigido.ipynb`

Não há feedback registrado no Jira para este notebook. As correções vêm da revisão feita no D2.

| Problema | O que foi feito |
|---|---|
| O alvo (alto uso) somava 14 meses de requisições, então misturava intensidade com tempo de exposição | Alvo por mês de exposição: requisições ÷ meses desde a contratação, ou desde jun/2025 |
| `tenure_days` sem padronização na regressão logística, com coeficientes incomparáveis | `StandardScaler` dentro de um `Pipeline`; os coeficientes passam a ser comparáveis |
| Divisão treino/teste sem estratificação, repetida em duas células | Uma divisão estratificada |
| Avaliação numa única divisão, só com regressão logística | Validação cruzada estratificada repetida; baseline; regressão logística, Random Forest e gradient boosting |
| Só acurácia e relatório de classificação | ROC-AUC, PR-AUC, precisão, recall e F1 |
| Cargo com 75 categorias em one-hot | `min_frequency` agrupa os cargos raros |
| O caminho dos dados só funcionava na máquina de quem criou o notebook | Caminho relativo à pasta do repositório |

**Resultado:** a conclusão do D1 se mantém com a validação correta. O melhor modelo (Random Forest balanceada) tem ROC-AUC de 0,90 na validação cruzada. No teste, tem recall de 0,79 e precisão de 0,38, perto do recall de 0,80 e da precisão de 0,43 da regressão logística balanceada original.

No D2, o modelo passou a prever o uso de cada ferramenta no mês seguinte, com histórico e validação temporal (`Deliverable2/05_Machine_Learning`).

## Estatística Multivariada — `Deliverable1/02_Estatistica_Multivariada/relatorioinicialcorrigido.rmd` (e `.html`)

O relatório do D1 testou duas hipóteses com correlações simples. Ele mesmo registra, na conclusão, o que faltava: uma análise multivariada que controle por área, cargo e exposição às ferramentas. As correções seguem esse apontamento. Não há feedback desta disciplina registrado no Jira além do que está abaixo.

| Ponto | O que foi feito | Onde |
|---|---|---|
| Base única por colaborador, com nulos tratados | Junção de quatro bases (cadastro, treinamentos, uso de IA, segurança) em uma matriz analítica por colaborador, com ausências estruturais tratadas e a tabela exportada (`tabela_analitica_final.csv`) | `relatorioinicialcorrigido.rmd`, seções 3 e 4 |
| H1 — treinamento aumenta o uso de IA | Confirmada, com efeito fraco: correlação de 0,11; média de tokens de 7.003.698 entre treinados e 5.390.459 entre não treinados | `resumo_hipoteses.csv` |
| H2 — treinamento reduz incidentes | Não confirmada: correlação de −0,02; 0,0255 incidentes por colaborador entre treinados e 0,031 entre não treinados | `resumo_hipoteses.csv` |
| Correlações simples não controlam por setor, cargo e exposição | Hipóteses retestadas com regressão logística com controles (setor, senioridade, tempo de casa e licenças) | `Deliverable2/02_Estatistica_Multivariada` (SMAN-251) |

**O que o reteste revelou**
- **H1 era confundida pelo setor.** Cada curso concluído multiplica a chance de usar IA por 1,37 (p < 0,001). Com os controles, a razão de chances cai para 1,04 (p = 0,642). As empresas de tecnologia treinam mais e também usam mais IA.
- **H2 continua não confirmada.** A razão de chances por curso é 0,89 (IC 95%: 0,55–1,45) sem controles e 0,81 (IC 95%: 0,49–1,33) com eles. Os incidentes são raros (64 colaboradores, 3,0%), o que limita o poder dos testes.
- A conclusão do D1 ("a hipótese de senso comum não se sustenta") se mantém, agora com controles.

Além do reteste, o D2 construiu indicadores de maturidade por departamento (índices IMIA e IINV e agrupamento), descritos em `Deliverable2/02_Estatistica_Multivariada/README.md`.

## Pesquisa Operacional — `Deliverable1/07_Pesquisa_Operacional_e_Calculo_Aplicado/DELIVERABLE 1 LUCY.docx`

O D1 modelou um problema de programação linear com duas variáveis: licenças de Copiloto de Desenvolvimento (x₁) e de Assistente Generativo (x₂).
- **Objetivo:** maximizar Z = 50x₁ + 30x₂.
- **Restrições:** orçamento 20x₁ + 10x₂ ≤ 1.000 e capacitados x₁ + x₂ ≤ 40, com x₁, x₂ ≥ 0.
- **Coeficientes:** supostos, como o próprio documento diz ("por exemplo").

**Não existe um `.docx` corrigido desta disciplina no repositório.** A correção foi feita no D2, que parte do mesmo modelo e o substitui por um com dados reais.

| Problema do D1 | O que foi feito no D2 | Onde |
|---|---|---|
| Resultado do modelo não apresentado no documento | O notebook resolve o modelo do D1 e reproduz o ótimo x₁ = 40, x₂ = 0, Z = 2.000, com R$ 200 de folga no orçamento e preço-sombra de 50 para os capacitados: o limite era a capacitação, não o dinheiro | `Deliverable2/07_Pesquisa_Operacional_e_Calculo_Aplicado/modelo_otimizacao_licencas.ipynb` |
| Custos, valores e número de capacitados supostos | Coeficientes extraídos de faturamento, orçamentos, logs de uso, treinamentos e ferramentas aprovadas | `Deliverable2/07_Pesquisa_Operacional_e_Calculo_Aplicado/README.md`, seção 2 |
| Só duas variáveis e duas restrições | Decisão de quem recebe cada licença (1.554 variáveis binárias), com restrições de orçamento, governança, categoria, cobertura, autonomia, capacidade de treino e transição | idem, seção 1 |
| Sem análise de sensibilidade | 12 cenários, curva de orçamento, preço-sombra, elasticidade e aplicação às 18 empresas | `Deliverable2/07_Pesquisa_Operacional_e_Calculo_Aplicado/resultados/` |
