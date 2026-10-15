# Indicadores e métricas de qualidade

## Processo (Jira, projeto SMAN)

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
| Testes automatizados do backend | 70 | ocorrências de `@Test` |
| Testes do `alert-service` | 19 | `alert-service/test` |
| Testes do front-end | 0 | não há |
| Razão linhas de teste / linhas de produção (backend) | 0,32 | 1.671 / 5.250 |
| Meta de cobertura configurada | 70% de linhas e de ramos nos serviços e regras | `backend/pom.xml` (JaCoCo, regra `check`) |
| Cobertura medida | não medida nesta coleta | Maven não estava instalado no ambiente |
| Front-end | 20 arquivos JS, 1.915 linhas, CSS de 346 linhas | contagem em `frontend/public` |
| Banco de dados | 818 linhas de SQL | `database/` |
| Decisões de arquitetura registradas | 10 ADRs | `docs/adr` |
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

Pull requests mesclados: #1, #2, #4 e #5.

## Como atualizar

As consultas JQL da coluna "Fonte" podem ser coladas na busca avançada do Jira. A cobertura sai de `cd backend && mvn test`, que gera o relatório em `backend/target/site/jacoco/index.html`.
