# Registro de auditorias

Três auditorias sobre os artefatos do projeto. Auditor: Ricardo Matteo.

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
| 2 | 10 ADRs com template e decisões registradas | C | `docs/adr` |
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
| 1 | 70 testes no backend, entre unitários por módulo e integração com PostgreSQL real | C | 16 arquivos em `backend/src/test` |
| 2 | Meta de cobertura de 70% (linhas e ramos) configurada e exigida no build | C | Regra `check` do JaCoCo no `pom.xml` |
| 3 | Cobertura efetiva ainda não registrada | NC | Relatório do JaCoCo não gerado nesta coleta |
| 4 | 19 testes no `alert-service` cobrindo regras e API HTTP | C | `alert-service/test` |
| 5 | Nenhum teste no front-end | NC | Não há testes em `frontend/` |
| 6 | Testes de integração são pulados quando não há Docker | OM | Comportamento documentado no README |
| 7 | AI Credits e dashboard do colaborador sem teste automatizado de ponta a ponta | OM | Matriz de rastreabilidade ("verificado no front e na demo") |

Limitação: esta auditoria não executou a suíte, porque o ambiente não tinha Maven nem Docker. Os números vêm da leitura do código e das configurações.

## Resumo

| Auditoria | Conformes | Não conformidades | Oportunidades |
|---|---|---|---|
| AUD-01 | 3 | 4 | 2 |
| AUD-02 | 5 | 2 | 2 |
| AUD-03 | 3 | 2 | 2 |
| Total | 11 | 8 | 6 |
