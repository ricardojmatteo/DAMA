# Correções do Deliverable 1 — Machine Learning e Análise Preditiva

**DAMA Intelligence** · Ana Luiza Proença, Gustavo Gomes, Kaique Lira e Ricardo Matteo · Ciência de Dados e Negócios, ESPM, 4º semestre, 2026

Este arquivo entra na pasta `00_Correcoes_Deliverable_1`. Os notebooks originais do D1 foram mantidos para comparação. As versões corrigidas ficam ao lado deles, com o sufixo `_corrigido`, seguindo a convenção do `Deliverable1/README.md`.

## Análise Preditiva — `Deliverable1/AnalisePreditiva/DAMAAnalisePreditiva1_corrigido.ipynb`

Correções feitas a partir do feedback da pré-avaliação, registrado no épico **SMAN-78** ("Correção — Análise Preditiva"), e da revisão do notebook.

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

**O que a correção revelou**
- Com o corte temporal, sobram 31 incidentes em 2.039 colaboradores (1,5%).
- O melhor modelo tem PR-AUC de 0,082 ± 0,059 (sorteio: 0,015) e ROC-AUC de 0,701 ± 0,064 na validação cruzada. No teste, cai para 0,644.
- A ablação mostra que o sinal é da unidade: só com o departamento, o ROC-AUC é 0,704. Só com cursos e tokens, 0,542; só com incidentes anteriores, 0,480. Unidades menores têm mais incidentes por colaborador.
- Para encontrar 6 dos 9 incidentes do teste, o modelo sinaliza 33% dos colaboradores, com precisão de 2,9%.

O modelo não sustenta decisão sobre pessoas. Isso é coerente com a Estatística Multivariada, que não confirmou a hipótese H2. Por isso, no D2 a disciplina passou a prever demanda e custo de IA (`Deliverable2/Analise Preditiva`).

## Machine Learning — `Deliverable1/machine-learning-ia/main_corrigido.ipynb`

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

No D2, o modelo passou a prever o uso de cada ferramenta no mês seguinte, com histórico e validação temporal (`Deliverable2/Machine Learning`).
