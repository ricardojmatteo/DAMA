# Análise Preditiva — Deliverable 1

**DAMA Intelligence** · Ana Luiza Proença, Gustavo Gomes, Kaique Lira e Ricardo Matteo · Ciência de Dados e Negócios, ESPM, 4º semestre, 2026

| Entrega | Onde está | Situação |
|---|---|---|
| Definição dos problemas de previsão | [DAMAAnalisePreditiva1_corrigido.ipynb](DAMAAnalisePreditiva1_corrigido.ipynb) | Pronto |
| Seleção das variáveis | Mesmo notebook | Pronto |
| Base utilizada | [05_Machine_Learning/Data](../05_Machine_Learning/Data/) | Pronto |

**Como rodar.** Python 3 com `pandas`, `numpy`, `scikit-learn` e `matplotlib`; leva cerca de 3 minutos. O notebook lê `employees`, `training_records`, `ai_usage_logs` e `security_events` e procura a pasta dos CSVs nesta ordem: a variável de ambiente `DAMA_DADOS`; a própria pasta do notebook (no Colab, com os CSVs enviados); uma pasta `dados/` ao lado dele; e `Deliverable1/05_Machine_Learning/Data/csv` no repositório completo. Se não encontrar, para com uma mensagem que explica as opções.

**Revisão após o checkpoint do D2.** A validação cruzada, a ablação e o limiar passaram a usar só o treino, e o teste é usado uma única vez. Detalhes em [00_Correcoes_Deliverable_1](../../Deliverable2/00_Correcoes_Deliverable_1/ml-e-analise-preditiva.md).
