# Sprint 2 — Review e Retrospectiva

**Período:** 24/09 a 30/09/2026

## Review

**Números do Jira:** 10 issues na sprint, todas concluídas (ver [acompanhamento-sprints.md](acompanhamento-sprints.md)). Ricardo Matteo e Ana Luiza Proença conduziram a maior parte das entregas.

**O que foi entregue:**

| Entrega | Issues |
|---|---|
| ADRs 0001 a 0004 e template padrão (monólito em camadas → modular → microservices; alert-service Node via `AlertClient`) | SMAN-50, 90 a 96 |
| Documentação de arquitetura publicada em `docs/` e linkada no README | SMAN-51 |
| Backend evoluído além do projeto-modelo de aula (PRs #1 e #2 de 28/09) | SMAN-52 |
| AI Credits e níveis de autonomia | SMAN-88 |
| Integração back-front e versão executável com `docker compose`, com a demo documentada no README | SMAN-4, 6, 59 |
| Cerimônias: planning poker, recebimento dos epics do PO e abertura da sprint no Jira-SM | SMAN-12, 30, 35 |

**Feedback recebido na review:** o MVP precisava mostrar, além do código, como o projeto é conduzido e validado. Os pontos levantados viraram as issues da Sprint 3:
- registrar as cerimônias (Review e Retro) e reunir evidências do Jira (SMAN-188 a 190);
- validar o MVP com usuários de cada persona (SMAN-186 e 187);
- deixar claro onde a hipótese de inovação está no MVP (SMAN-191);
- comprovar o MVP integrado rodando, com testes e pipeline (SMAN-192).

## Retrospectiva

| O que foi bem | O que pode melhorar | O que vamos mudar |
|---|---|---|
| A arquitetura ficou documentada (ADRs e `docs/`) e o backend saiu do projeto-modelo de aula | **Pontualidade:** a sprint terminou em 30/09, mas só foi encerrada no Jira em 13/10 | Encerrar a sprint no Jira no dia da review |
| A stack inteira sobe com um `docker compose up` e AI Credits, a hipótese de inovação, já está no backend | **Organização:** o quadro teve tasks duplicadas (SMAN-97 a 103) | Revisar o quadro antes de abrir a sprint e usar subtarefas para detalhar uma entrega |
| O Jira ajudou bastante: com os epics do PO e o quadro do SM, ficou claro o que entrava na sprint e quem cuidava de cada entrega | | |

**Ações com responsável e prazo:**

| Ação | Responsável | Prazo | Situação |
|---|---|---|---|
| Limpar as duplicadas e organizar o Jira com tasks por disciplina | Ricardo Matteo | 15/10/2026 | Feito (SMAN-203) |
| Adotar o fluxo do quadro em seis passos (A fazer → Dev → Code review → Pull request → Teste em dev → QA → Feito) | Gustavo Gomes | 13/10/2026 | Feito |
| Criar o roteiro de validação por persona | Ricardo Matteo | 15/10/2026 | Feito (SMAN-186) |
