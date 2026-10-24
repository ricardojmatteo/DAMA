# Registro de auditorias

Quatro auditorias sobre os artefatos do projeto. AUD-01 a AUD-03 em 15/10, com auditor Ricardo Matteo; AUD-04 em 20/10, de acompanhamento do plano de melhoria, com coleta automatizada a ser revisada pela equipe.

Classificação dos achados: C (conforme), NC (não conformidade), OM (oportunidade de melhoria).

## AUD-01 — Processo de gestão no Jira

Escopo: projeto SMAN, Sprints 2 e 3. Critério: uso do fluxo do quadro (A fazer, Dev, Code Review, Pull Request, Teste-Dev, QA, Feito) e das regras do Scrum.

| # | Achado | Tipo | Evidência |
|---|---|---|---|
| 1 | Toda issue da Sprint 3 tem responsável | C | `sprint = 111 AND assignee is EMPTY` retorna 0 |
| 2 | Quadro com etapas de revisão e teste definidas | C | Fluxo com Code Review, Teste-Dev e QA |
| 3 | 22 issues resolvidas num intervalo de 70 minutos, sinal de atualização em lote | NC | Datas de resolução das issues SMAN-4, 6, 50, 51, 52, 59, 104, 105, 106, 108, 109, 143, 144, 145, 165, 166, 168, 169, 171, 172, 173 e 174 |
| 4 | Sprints 2 e 3 sem meta preenchida | NC | Campo goal vazio |
| 5 | Sprint 2 fechada 13 dias depois do fim previsto | NC | Datas da Sprint 2 |
| 6 | 31 issues em andamento ao mesmo tempo, mais que o total já concluído | OM | 31 em andamento contra 32 concluídas |
| 7 | Issues duplicadas mantidas no quadro | NC | 7 issues com título "[DUPLICADO - excluir]". Foram tiradas da Sprint 3; a exclusão depende de administrador |
| 8 | Concentração das entregas: 26 das 32 concluídas são de um responsável; nenhuma do Kaique | OM | Concluídas por responsável |
| 9 | Retrabalho baixo: 1 issue passou por Correction | C | `status was CORRECTION` retorna 1 |

Ação resultante: itens 3 a 8 entram no plano de melhoria.

## AUD-02 — Repositório e integração contínua

Escopo: repositório `ricardojmatteo/DAMA`, branch `main`. Critério: boas práticas de versionamento, documentação e pipeline.

| # | Achado | Tipo | Evidência |
|---|---|---|---|
| 1 | Estrutura de pastas documentada no README e coerente com o conteúdo | C | README da raiz |
| 2 | 9 ADRs com template e decisões registradas | C | `docs/adr` |
| 3 | Matriz de rastreabilidade história → teste | C | `docs/requisitos/rastreabilidade.md` |
| 4 | `.env.example` e variáveis padronizadas | C | `.env.example` |
| 5 | Pipeline de CI só para o backend | NC | Único `Jenkinsfile` fora de `projetos-aula` está em `backend/` |
| 6 | Sem análise estática de código | NC | Nenhuma configuração de Checkstyle, SonarQube ou ESLint |
| 7 | Sem tags ou releases | OM | `git tag` vazio |
| 8 | Pull requests exigem aprovação, e o PR #5 foi mesclado; conferir no GitHub se houve revisão registrada | OM | Tela do PR #5 mostrava "Review required" antes da mesclagem |
| 9 | Credenciais e segredos fora do código | C | Variáveis lidas de ambiente, `.env.example` sem valores reais |

## AUD-03 — Testes e cobertura

Escopo: backend, `alert-service` e front-end. Critério: existência, cobertura e execução dos testes.

| # | Achado | Tipo | Evidência |
|---|---|---|---|
| 1 | 69 testes no backend, entre unitários por módulo e integração com PostgreSQL real | C | 16 arquivos em `backend/src/test` (inventário em [plano-de-testes.md](plano-de-testes.md)) |
| 2 | Meta de cobertura de 70% (linhas e ramos) configurada e exigida no build | C | Regra `check` do JaCoCo no `pom.xml` |
| 3 | Cobertura efetiva ainda não registrada | NC | Relatório do JaCoCo não gerado nesta coleta |
| 4 | 19 testes no `alert-service` cobrindo regras e API HTTP, todos passando | C | `alert-service/test`; execução de 20/10 em `evidencias/alert-service-testes-2026-10-20.txt` |
| 5 | Nenhum teste no front-end | NC | Não há testes em `frontend/` |
| 6 | Testes de integração são pulados quando não há Docker | OM | Comportamento documentado no README |
| 7 | AI Credits e dashboard do colaborador sem teste automatizado de ponta a ponta | OM | Matriz de rastreabilidade ("verificado no front e na demo") |

Limitação: esta auditoria não executou a suíte, porque o ambiente não tinha Maven nem Docker. Os números vêm da leitura do código e das configurações.

## AUD-04 — Acompanhamento do plano de melhoria (20/10)

Escopo: as 14 ações do [plano de melhoria](plano-melhoria.md), cinco dias depois da sua criação. Critério: a ação foi cumprida e há evidência. Fontes: segunda coleta de [métricas](metricas.md), exportação do Jira em `evidencias/` e o repositório.

| # | Achado | Tipo | Evidência |
|---|---|---|---|
| 1 | Issues duplicadas retiradas da Sprint 3 (ação 5) | C | 0 issues "[DUPLICADO - excluir]" na sprint, contra 7 em 15/10 |
| 2 | Métricas coletadas pela segunda vez (ação 14) | C | Coleta 2 em `metricas.md` |
| 3 | Plano de testes escrito, com 88 casos automatizados inventariados e 12 manuais definidos | C | `plano-de-testes.md`, `evidencias/inventario-testes.csv` |
| 4 | Sprint 3 continua sem meta (ação 3) | NC | Campo goal vazio |
| 5 | Nenhuma Definition of Done nem checklist de revisão (ação 1) | NC | Nenhum arquivo no repositório |
| 6 | 28 issues em andamento com uma única pessoa (ação 4) | NC | Exportação do Jira de 20/10 |
| 7 | Tarefas de ML ainda sem andamento: 25 issues do Kaique, nenhuma concluída (ação 6) | NC | Exportação do Jira de 20/10 |
| 8 | Tarefas do Deliverable 2 por disciplina fora da sprint ativa | NC | SMAN-193 a 201 sem sprint |
| 9 | CI segue só no backend (ação 7) e sem análise estática (ação 8) | NC | Único `Jenkinsfile` em `backend/` |
| 10 | Cobertura ainda não medida (ação 9). A tentativa de 20/10 não rodou por falta de acesso ao Maven Central no ambiente da coleta | NC | Seção 8 do plano de testes |
| 11 | Sem tags de versão (ação 12) e sem atas de Review e Retrospectiva (ação 13) | NC | `git tag` vazio; pasta `docs/sprints/` inexistente |
| 12 | 8 das 39 issues concluídas foram criadas e fechadas em menos de 1 hora | OM | Registro retroativo de trabalho: criar a issue antes de começar |
| 13 | A matriz de rastreabilidade está desatualizada: POAN-113 e POAN-117 já têm testes unitários | OM | `DashboardServiceTest`, `AiCreditServiceTest` |
| 14 | A contagem de testes do backend (70) incluía a anotação `@Testcontainers`; o número correto é 69 | OM | Corrigido em `metricas.md`, `auditorias.md` e `avaliacao-maturidade.md` |

Situação das ações: 2 de 14 concluídas (5 e 14). As que dependem só de disciplina de processo (1, 2, 3, 4, 12 e 13) são as de menor custo e as que mais pesam no nível de maturidade ([cmmi.md](cmmi.md)).

## Resumo

| Auditoria | Conformes | Não conformidades | Oportunidades |
|---|---|---|---|
| AUD-01 | 3 | 4 | 2 |
| AUD-02 | 5 | 2 | 2 |
| AUD-03 | 3 | 2 | 2 |
| AUD-04 | 3 | 8 | 3 |
| Total | 14 | 16 | 9 |
