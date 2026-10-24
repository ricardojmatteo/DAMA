# Plano de melhoria do processo e do produto

Plano em formato 5W2H, derivado das auditorias em [auditorias.md](auditorias.md) e das métricas em [metricas.md](metricas.md). Responsáveis e prazos são sugestões para a equipe confirmar.

| # | O quê | Por quê | Origem | Quem (sugerido) | Quando | Onde | Como | Quanto |
|---|---|---|---|---|---|---|---|---|
| 1 | Definir a Definition of Done e o checklist de revisão de código | Não há critério escrito para dar uma issue como pronta | AUD-01, maturidade | Time inteiro | Fim da Sprint 3 | `Deliverable2/04_Qualidade_de_Software/` | Escrever em 8 a 10 itens (testes passando, PR revisado, doc atualizada) e colar no quadro | 2 h |
| 2 | Parar de atualizar o quadro em lote | 22 issues resolvidas em 70 minutos distorcem lead time e burndown | AUD-01 #3 | Todos os responsáveis | Imediato | Jira | Mover o cartão no dia em que o estado muda; revisar o quadro na daily | Sem custo |
| 3 | Preencher a meta de cada sprint | Sprints 2 e 3 sem objetivo | AUD-01 #4 | Scrum Master | Próxima planning | Jira | Uma frase por sprint, ligada à entrega do deliverable | 15 min |
| 4 | Limitar trabalho em andamento | 31 issues abertas ao mesmo tempo | AUD-01 #6 | Time inteiro | Fim da Sprint 3 | Quadro | Limite de 2 cartões por pessoa em Dev e Code Review | Sem custo |
| 5 | Excluir as issues duplicadas | Poluem o quadro e as métricas | AUD-01 #7 | Administrador do Jira | Fim da Sprint 3 | Jira | Excluir SMAN-97, 99 a 103 e POAN-24 | 10 min |
| 6 | Redistribuir as tarefas de ML e equilibrar a carga | Nenhuma issue concluída do Kaique; 26 de 32 concentradas em uma pessoa | AUD-01 #8 | Time inteiro | Próxima planning | Jira | Rever a capacidade na planning e quebrar as tarefas de ML em partes menores | 1 h |
| 7 | Criar pipeline de CI para `alert-service` e front-end | Só o backend tem CI | AUD-02 #5 | Gustavo | Fim da POAN Sprint 3 | `alert-service/Jenkinsfile`, `frontend/Jenkinsfile` | Etapas de instalação, `npm test` e build da imagem, como no backend | 3 h |
| 8 | Adicionar análise estática | Sem verificação automática de estilo e defeitos | AUD-02 #6 | Ricardo | Fim da POAN Sprint 3 | `backend/pom.xml`, `frontend/` | Checkstyle ou SpotBugs no backend e ESLint no front, rodando no CI | 4 h |
| 9 | Gerar e registrar a cobertura de testes | A meta de 70% existe, mas a cobertura real nunca foi registrada | AUD-03 #3 | Ricardo | Fim da Sprint 3 | `metricas.md` | Rodar `mvn test`, abrir `target/site/jacoco/index.html` e registrar o resultado | 30 min |
| 10 | Criar testes para o front-end | Nenhum teste automatizado de interface | AUD-03 #5 | Ana | Fim da POAN Sprint 3 | `frontend/test/` | Testes de componentes com `node --test` e um roteiro de teste manual de responsividade | 6 h |
| 11 | Cobrir AI Credits e dashboard do colaborador com teste de integração | Estão só "verificados na demo" | AUD-03 #7 | Gustavo | Fim da POAN Sprint 3 | `DamaApiIntegrationTest` | Um cenário por regra de AI Credits e um para o dashboard | 4 h |
| 12 | Definir regra de revisão e criar tags de versão | PRs sem revisão registrada e sem releases | AUD-02 #7, #8 | Time inteiro | Fim da Sprint 3 | GitHub | Exigir uma aprovação antes do merge e criar a tag `v0.2.0` ao fechar o Deliverable 2 | 30 min |
| 13 | Registrar Reviews e Retrospectivas de cada sprint | Sem registro formal do aprendizado | Maturidade | Scrum Master | Fim de cada sprint | `docs/sprints/` | Uma ata curta com entregas, feedback, o que melhorar e ações | 1 h por sprint |
| 14 | Repetir as métricas a cada sprint | Para ver a evolução | Maturidade | Ricardo | Fim de cada sprint | `metricas.md` | Reexecutar as consultas e atualizar a tabela | 30 min |

## Acompanhamento em 20/10

Registrado na auditoria [AUD-04](auditorias.md).

| # | Ação | Situação | Observação |
|---|---|---|---|
| 1 | Definition of Done e checklist de revisão | Não iniciada | |
| 2 | Parar de atualizar o quadro em lote | Parcial | Nenhum lote novo desde 28/09, mas 8 issues foram criadas e fechadas em menos de 1 hora |
| 3 | Meta de cada sprint | Não iniciada | Sprint 3 sem meta |
| 4 | Limitar trabalho em andamento | Não iniciada | 30 em andamento, 28 com uma pessoa |
| 5 | Excluir as duplicadas | Parcial | Fora da sprint; exclusão depende de administrador |
| 6 | Redistribuir as tarefas de ML | Não iniciada | |
| 7 | CI para `alert-service` e front-end | Não iniciada | |
| 8 | Análise estática | Não iniciada | |
| 9 | Registrar a cobertura | Bloqueada | Precisa rodar `mvn verify` numa máquina com Docker e acesso ao Maven Central |
| 10 | Testes do front-end | Planejada | Casos TF-01 a TF-03 no [plano de testes](plano-de-testes.md) |
| 11 | Integração de AI Credits e dashboard | Planejada | Casos TI-01 e TI-02 no plano de testes |
| 12 | Regra de revisão e tags | Não iniciada | |
| 13 | Registrar Reviews e Retrospectivas | Não iniciada | SMAN-188 e 189 |
| 14 | Repetir as métricas | Concluída | Coleta 2 em `metricas.md` |

Duas ações novas saíram da avaliação [CMMI](cmmi.md):

| # | O quê | Por quê | Quem (sugerido) | Quando | Como |
|---|---|---|---|---|---|
| 15 | Registrar story points e comparar estimado × realizado | Estimativa (EST) no nível 1 | Scrum Master | Sprint 4 | Campo de story points no planning poker e velocidade nas métricas |
| 16 | Criar uma lista de riscos do projeto | Gestão de riscos (RSK) no nível 0 | Time inteiro | Sprint 4 | Riscos com probabilidade, impacto e resposta, revisados na planning |

## Indicadores de sucesso

| Indicador | Hoje | Meta na próxima coleta |
|---|---|---|
| Issues atualizadas em lote | 22 em 70 minutos | 0 |
| Sprints com meta | 0 de 2 | 100% |
| Cobertura medida | não medida | registrada, acima de 70% nos serviços |
| Serviços com CI | 1 de 3 | 3 de 3 |
| Testes no front-end | 0 | pelo menos os componentes principais |
| Nível médio de maturidade | 1,6 | 2,0 |
