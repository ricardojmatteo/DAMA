# Indicadores e métricas de qualidade

Duas coletas: a primeira em 15/10 (base das auditorias AUD-01 a AUD-03) e a segunda em 20/10, que serve de acompanhamento (AUD-04). A exportação da segunda coleta está em [evidencias/jira-sprint3-2026-10-20.csv](evidencias/jira-sprint3-2026-10-20.csv).

## Processo (Jira, projeto SMAN) — coleta 1, 15/10

| Indicador | Valor | Fonte |
|---|---|---|
| Issues na Sprint 3 | 113 | `sprint = 111` |
| Concluídas (Feito) | 32 (28%) | `sprint = 111 AND statusCategory = Done` |
| Em andamento (Dev, Code Review, Pull Request, Teste-Dev, QA, Correction) | 31 (27%) | `sprint = 111 AND status in (...)` |
| Não iniciadas (A fazer) | 50 (44%) | `sprint = 111 AND status = "A fazer"` |
| Issues sem responsável | 0 | `sprint = 111 AND assignee is EMPTY` |
| Retrabalho (issues que passaram por Correction) | 1 | `project = SMAN AND status was CORRECTION` |
| Lead time das 29 issues concluídas (sem as 3 criadas e fechadas durante esta coleta) | média 10,9 dias, mediana 4,2 dias, de 0,6 a 45,1 dias | datas de criação e de resolução |
| Issues concluídas por responsável | Ricardo 26, Ana 5, Gustavo 1, Kaique 0 | campo responsável |
| Issues duplicadas identificadas | 7 (SMAN-97, 99 a 103 e POAN-24) | título "[DUPLICADO - excluir]" |
| Sprints com meta (goal) preenchida | 0 de 2 | campo goal das Sprints 2 e 3 |
| Atraso de encerramento da Sprint 2 | 13 dias após o fim previsto | datas da Sprint 2 |

Leitura dos números: 22 issues foram resolvidas num intervalo de 70 minutos, no mesmo dia, ou seja, em atualização em lote. Isso distorce o lead time (a mediana de 4,2 dias reflete mais a data da atualização do que o tempo real de trabalho) e indica que o fluxo do quadro nem sempre acompanha o andamento.

## Produto (repositório)

| Indicador | Valor | Fonte |
|---|---|---|
| Código de produção do backend | 137 arquivos Java, 5.250 linhas | contagem em `backend/src/main` |
| Código de teste do backend | 16 arquivos, 1.671 linhas | `backend/src/test` |
| Testes automatizados do backend | 69 | ocorrências de `@Test` (a contagem anterior, 70, incluía por engano a anotação `@Testcontainers`) |
| Testes do `alert-service` | 19 | `alert-service/test` |
| Testes do front-end | 0 | não há |
| Razão linhas de teste / linhas de produção (backend) | 0,32 | 1.671 / 5.250 |
| Testes do `alert-service` executados em 20/10 | 19 de 19 passando, 0,5 s | `evidencias/alert-service-testes-2026-10-20.txt` |
| Última execução registrada da suíte do backend | 29/09: 31 testes em 4 classes, 0 falhas, 9 de integração pulados (sem Docker) | `evidencias/backend-surefire-2026-09-29/` |
| Meta de cobertura configurada | 70% de linhas e de ramos nos serviços e regras | `backend/pom.xml` (JaCoCo, regra `check`) |
| Cobertura medida | não medida nesta coleta | Maven não estava instalado no ambiente |
| Front-end | 20 arquivos JS, 1.915 linhas, CSS de 346 linhas | contagem em `frontend/public` |
| Banco de dados | 818 linhas de SQL | `database/` |
| Decisões de arquitetura registradas | 9 ADRs (mais o template) | `docs/adr` |
| Etapas do pipeline de CI | 3 (checkout, testes, imagem Docker) | `backend/Jenkinsfile` |
| Serviços cobertos por CI | 1 de 3 (só o backend) | Jenkinsfile existe só em `backend/` |
| Análise estática de código | nenhuma configurada | ausência de Checkstyle, SonarQube, ESLint |

## Contribuição no repositório

| Autor | Commits (sem merges) |
|---|---|
| Ricardo Matteo | 15 |
| Ana Luiza Proença | 5 |
| Kaique Lira | 3 |
| Gustavo Gomes | 1 |

Pull requests mesclados até a coleta 1: #1, #2, #4 e #5. O #6 (esta documentação de qualidade) foi mesclado depois.

## Processo — coleta 2, 20/10 (acompanhamento)

Mesmas consultas, cinco dias depois e três dias antes do fim da Sprint 3 (23/10).

| Indicador | 15/10 | 20/10 | Leitura |
|---|---:|---:|---|
| Issues na Sprint 3 | 113 | 118 | Entraram tarefas do Deliverable 2 (SMAN-186 a 192, 202 a 208) |
| Concluídas | 32 (28%) | 39 (33%) | +7 em cinco dias |
| Em andamento | 31 (27%) | 30 (25%) | 28 delas com uma única pessoa |
| Não iniciadas | 50 (44%) | 49 (42%) | |
| Issues sem responsável | 0 | 0 | Uma issue está numa segunda conta do mesmo integrante ("Ricardo.matteo") |
| Issues duplicadas na sprint | 7 | 0 | Retiradas da sprint (ação 5); exclusão ainda depende de administrador |
| Issues herdadas da Sprint 2 | — | 10 | Trabalho não concluído na sprint anterior |
| Lead time das concluídas | média 10,9 d, mediana 4,2 d | média 10,4 d, mediana 4,1 d, máximo 59,8 d | |
| Concluídas criadas e fechadas em menos de 1 hora | — | 8 de 39 | Registro retroativo de trabalho já feito. Sem elas: média 13,1 d, mediana 4,2 d |
| Maior lote de resoluções num mesmo dia | 22 (28/09) | 22 (28/09) | Nenhum lote novo desde então |
| Concluídas por responsável | Ricardo 26, Ana 5, Gustavo 1, Kaique 0 | Ricardo 31, Ana 5, Gustavo 3, Kaique 0 | Concentração continua: 79% das entregas com uma pessoa |
| Sprints com meta | 0 de 2 | 0 de 2 | Ação 3 não cumprida |
| Tarefas do D2 por disciplina (SMAN-193 a 201) dentro da sprint | — | 0 de 9 | Estão no backlog; 8 de 9 em "A fazer" |

## Produto — coleta 2, 20/10

| Indicador | Valor | Fonte |
|---|---|---|
| Testes automatizados | 88 (69 no backend, 19 no `alert-service`) | [evidencias/inventario-testes.csv](evidencias/inventario-testes.csv) |
| Testes por nível | 67 unitários, 9 de integração, 5 de contrato, 7 de API HTTP | idem |
| Execução do `alert-service` | 19 de 19 passando (100%) | [evidencias/alert-service-testes-2026-10-20.txt](evidencias/alert-service-testes-2026-10-20.txt) |
| Execução do backend | não executada nesta coleta: o ambiente usado não tinha acesso ao Maven Central | — |
| Cobertura medida | ainda não medida | pendente (ação 9) |
| Casos manuais definidos / executados | 12 / 0 | [plano-de-testes.md](plano-de-testes.md), seção 5.2 |
| Tags de versão | 0 | `git tag` |

## Como atualizar

As consultas JQL da coluna "Fonte" podem ser coladas na busca avançada do Jira. A cobertura sai de `cd backend && mvn test`, que gera o relatório em `backend/target/site/jacoco/index.html`.
