# Estatística Multivariada — Deliverable 2

**Indicadores organizacionais de maturidade no uso de IA.** O trabalho continua o relatório do D1 (`Deliverable1/02_Estatistica_Multivariada`), que testou H1 e H2 com correlações simples. A conclusão do D1 pedia uma análise mais completa, controlando por área, cargo e exposição às ferramentas.

## Arquivos

| Arquivo | Conteúdo |
|---|---|
| [analise_multivariada.Rmd](analise_multivariada.Rmd) | Código e texto da análise |
| [analise_multivariada.pdf](analise_multivariada.pdf) | Relatório com tabelas e gráficos |

## Dados

A unidade de análise é o **departamento**. O período vai de janeiro a julho de 2026 e só entram colaboradores ativos. Dos 154 departamentos, 110 têm pelo menos 5 colaboradores ativos. Quatro deles não usaram IA no período e entram no fim do ranking. Restam **106 departamentos**.

## Resultados

**Dimensões.** Dos 14 indicadores, saíram `sucesso`, `nota` e `incidentes`, por terem correlação abaixo de 0,30 com todos os outros. Os 11 restantes formam três dimensões: uso, capacitação e investimento.

**Índices.** O IMIA combina uso e capacitação, e o IINV mede investimento, ambos de 0 a 100.

| Setor | Departamentos | IMIA | IINV |
|---|---:|---:|---:|
| Tecnologia | 32 | 82,4 | 64,4 |
| Marketing | 7 | 69,7 | 37,0 |
| Serviços | 11 | 58,7 | 34,6 |
| Varejo | 23 | 42,5 | 37,0 |
| Financeiro | 16 | 38,9 | 49,6 |
| Indústria | 21 | 25,6 | 52,2 |

**23 departamentos estão no quadrante de desperdício**, com investimento acima da mediana e maturidade abaixo dela.

**Agrupamento.** Escolhemos 3 grupos, com hierárquico (Ward), k-means e k-means++:

| Perfil | Departamentos | Colaboradores | Adoção | Licenças ociosas |
|---|---:|---:|---:|---:|
| Avançados | 18 | 349 | 83% | 22% |
| Capacitados, investimento baixo | 34 | 506 | 69% | 39% |
| Adoção baixa | 54 | 1.163 | 31% | 71% |

Além disso, 16 departamentos usam IA acima da média com capacitação abaixo da média, que é a situação de maior risco de governança.

**Hipóteses do D1, com regressão logística:**

| | Sem controles | Com setor, senioridade, tempo de casa e licenças |
|---|---|---|
| **H1:** razão de chances de usar IA por curso | 1,37 (p < 0,001) | 1,04 (p = 0,642) |
| **H2:** razão de chances de ter incidente por curso | 0,89 (p = 0,648) | 0,81 (p = 0,401) |

H1 era confundida pelo setor, e H2 continua não confirmada. No teste, o modelo da H1 tem AUC de 0,714. O da H2 tem AUC de 0,503, praticamente a de um sorteio.

## Limitações

- A base é sintética, e orçamento e gasto foram gerados de forma independente.
- A H1 mede se o colaborador usa IA ou não, e não quanto usa.
- Os incidentes são raros (64 colaboradores), o que limita os testes da H2.
- Os indicadores são médias do período, sem análise ao longo do tempo.

## Como rodar

Abra o `analise_multivariada.Rmd` no RStudio e clique em Knit. Os dados são lidos de `Deliverable1/05_Machine_Learning/Data/csv/`.

Jira: SMAN-195.
