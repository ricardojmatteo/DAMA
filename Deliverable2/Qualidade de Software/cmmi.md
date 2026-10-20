# Avaliação segundo o CMMI

Este documento leva a [avaliação de maturidade](avaliacao-maturidade.md) para a estrutura do CMMI (versão 2.0 em diante, mantido pelo CMMI Institute/ISACA): cada área do processo do time é associada a uma área de prática do CMMI, recebe um nível de capacidade e, ao fim, o time recebe um nível de maturidade. É uma autoavaliação acadêmica com base em evidências do repositório e do Jira, e não uma avaliação oficial, que exige avaliador certificado.

## 1. Como o CMMI mede

| Conceito | Escala | Significado |
|---|---|---|
| **Nível de capacidade** (por área de prática) | 0 Incompleto | As práticas básicas não são feitas, ou são feitas de forma parcial |
| | 1 Inicial | As práticas básicas acontecem e atingem o objetivo da área, sem padrão |
| | 2 Gerenciado | A área é planejada, acompanhada e tem responsáveis e registros |
| | 3 Definido | A área segue um padrão do time, documentado, aplicado e melhorado |
| **Nível de maturidade** (do time) | 0 a 5: Incompleto, Inicial, Gerenciado, Definido, Quantitativamente Gerenciado, Em Otimização | Um time só está no nível N quando todas as áreas de prática daquele nível estão na capacidade N |

O nível 2 de maturidade cobre as áreas que fazem um projeto ser gerenciado. Entre elas:
- Planejamento (PLAN), Estimativa (EST) e Monitoramento e Controle (MC);
- Desenvolvimento e Gestão de Requisitos (RDM);
- Gerência de Configuração (CM);
- Garantia da Qualidade do Processo (PQA);
- Gestão de Desempenho e Medição (MPM);
- as duas áreas que sustentam o hábito: Governança (GOV) e Infraestrutura de Implementação (II).

## 2. Mapeamento e nível de capacidade por área de prática

| Área de prática CMMI | Área na avaliação interna | Capacidade | Evidências | O que impede o nível seguinte |
|---|---|:-:|---|---|
| **RDM** Desenvolvimento e Gestão de Requisitos | Gerência de requisitos | **2** | Histórias no Jira-PO (épico POAN-109); RF, RN e RNF numerados; [matriz de rastreabilidade](../../docs/requisitos/rastreabilidade.md) história → requisito → endpoint → teste | Matriz desatualizada (POAN-113 e POAN-117 já têm testes unitários e constam como "verificado na demo"); requisitos ainda não validados com usuários (SMAN-187) |
| **PLAN** Planejamento | Gerência de projeto | **1** | Sprints abertas no Jira, planning poker (SMAN-12, SMAN-45), tarefas do D2 por disciplina (SMAN-193 a 201) | Sprints 2 e 3 sem meta; as tarefas do D2 estão no backlog, fora da Sprint 3; nenhum plano de capacidade por pessoa |
| **EST** Estimativa | Gerência de projeto | **1** | Planning poker realizado | Estimativas não registradas nas issues nem comparadas com o realizado |
| **MC** Monitoramento e Controle | Gerência de projeto | **1** | Quadro com 8 estados; todas as issues com responsável | 22 issues resolvidas em lote em 28/09; 30 issues em andamento ao mesmo tempo (28 com uma pessoa); Sprint 2 fechada 13 dias após o fim; sem registro de Review e Retrospectiva |
| **CM** Gerência de Configuração | Gerência de configuração | **2** | Git com branches e 5 pull requests mesclados (#1, #2, #4, #5, #6); `.gitignore`, `.env.example`; scripts de banco versionados e numerados | Sem tags ou releases (sem baseline das entregas); sem regra de revisão registrada nos PRs |
| **PR** Revisões por Pares | Garantia da qualidade | **1** | Coluna "Code Review" no quadro; PRs antes do merge | Revisão não registrada nos PRs; nenhum checklist de revisão |
| **VV** Verificação e Validação | Verificação e testes | **1** | 69 testes no backend e 19 no `alert-service`, Testcontainers, meta de cobertura de 70% no JaCoCo, [plano de testes](plano-de-testes.md) | A avaliação interna deu 2 para "Verificação e testes". No CMMI, porém, a área une verificação e validação, e a validação com usuários ainda não aconteceu. Além disso, a cobertura nunca foi medida, o front não tem testes e a suíte do backend não é executada por inteiro desde 29/09 |
| **PI** Integração do Produto | Integração contínua | **1** | `docker compose` integra os 4 contêineres; teste de contrato backend ↔ `alert-service` | Pipeline só do backend; nenhum registro de pipeline verde; `alert-service` e front fora do CI |
| **TS** Solução Técnica | Documentação e arquitetura | **3** | Modelo C4 em 3 níveis; estrutura `api`/`internal` aplicada nos 13 módulos de negócio; referência da API; READMEs por componente | — (nível máximo considerado) |
| **DAR** Análise e Resolução de Decisões | Documentação e arquitetura | **2** | 9 ADRs com template fixo (contexto, decisão, alternativas consideradas, consequências) e histórico de substituição (ADR-0001 → 0002) | Critérios de decisão não explícitos nem pesados |
| **PQA** Garantia da Qualidade do Processo | Garantia da qualidade | **1** | Quatro auditorias registradas ([auditorias.md](auditorias.md)), a última de acompanhamento | Sem Definition of Done; auditorias começaram no fim da Sprint 3 e ainda não têm periodicidade |
| **MPM** Gestão de Desempenho e Medição | Medição e análise | **1** | Duas coletas de métricas (15/10 e 20/10) em [metricas.md](metricas.md) | Sem objetivos de medição definidos antes da coleta; cobertura nunca medida; métricas não usadas na planning |
| **GOV** Governança | — | **1** | Plano de melhoria com responsáveis sugeridos | Processo depende de uma pessoa (31 das 39 issues concluídas da Sprint 3); nenhuma regra do processo aprovada pelo time |
| **II** Infraestrutura de Implementação | — | **1** | Jira, GitHub e Jenkins disponíveis para todos | Uso das ferramentas não padronizado (sem DoD, sem política de branch, sem template de PR) |
| **RSK** Gestão de Riscos e Oportunidades | — | **0** | Riscos de teste listados no plano de testes | Não há registro de riscos do projeto |

Áreas fora do escopo, por não se aplicarem a um projeto acadêmico sem fornecedores nem serviço em produção: Gestão de Acordos com Fornecedores (SAM), Seleção de Fornecedores (SSS), Gestão de Entrega de Serviço (SDM), Gestão Estratégica de Serviços (STSM), Continuidade (CONT), Resolução e Prevenção de Incidentes (IRP) e Treinamento Organizacional (OT).

## 3. Perfil e nível de maturidade

| Capacidade | Áreas de prática |
|:-:|---|
| 3 | TS |
| 2 | RDM, CM, DAR |
| 1 | PLAN, EST, MC, PR, VV, PI, PQA, MPM, GOV, II |
| 0 | RSK |

**Nível de maturidade estimado: 1 (Inicial).** O nível 2 exige capacidade 2 em todas as áreas de gestão do projeto. Planejamento, estimativa, monitoramento, garantia da qualidade, medição e governança estão em 1.

O resultado confirma o que a avaliação interna já mostrava (média 1,6): o lado técnico já tem práticas de nível 2 e 3, como arquitetura documentada, decisões registradas, versionamento e testes automatizados, mas a gestão do trabalho prende o time no nível 1. A boa notícia é que as lacunas de nível 2 são de disciplina e registro, não de capacidade técnica.

## 4. Caminho para o nível 2

Cada lacuna corresponde a uma ação do [plano de melhoria](plano-melhoria.md) (número entre parênteses) e a uma evidência que comprova o nível 2.

| Área | O que fazer | Evidência esperada |
|---|---|---|
| PLAN | Meta escrita em toda sprint (3); trazer as tarefas do D2 para a sprint ativa; rever a capacidade na planning (6) | Campo goal preenchido; issues SMAN-193 a 201 na sprint |
| EST | Registrar story points no planning poker e comparar estimado × realizado no fim da sprint (nova) | Story points nas issues; velocidade nas métricas |
| MC | Mover o cartão no dia em que o estado muda (2); limite de 2 cartões em andamento por pessoa (4); Review e Retrospectiva registradas (13) | Nenhuma resolução em lote; WIP ≤ 8; atas em `docs/sprints/` |
| PR | Exigir uma aprovação antes do merge (12) e usar um checklist de revisão (1) | Aprovações registradas nos PRs |
| VV | Executar o plano de testes: suíte completa com Docker, casos manuais TM-01 a TM-12, sessões de validação (SMAN-187); criar TI-01 a TI-03 e TF-01 a TF-03 (10, 11) | Seção 8 do plano de testes preenchida |
| PI | Pipeline para o `alert-service` e o front-end (7); print do pipeline verde (SMAN-192) | 3 de 3 serviços no CI |
| CM | Tag `v0.2.0` ao fechar o Deliverable 2 (12) | `git tag` com a versão entregue |
| PQA | Definition of Done (1); uma auditoria de acompanhamento por sprint, como a AUD-04 | DoD publicada; AUD-05 na Sprint 4 |
| MPM | Medir cobertura (9) e repetir as métricas a cada sprint (14), usando-as na planning | Terceira coleta com cobertura registrada |
| GOV / II | Time aprova a DoD e o fluxo do quadro; carga distribuída de forma mais equilibrada (6) | Nenhum integrante com mais da metade das entregas |
| RSK | Lista de riscos do projeto, revisada na planning (nova) | `docs/riscos.md` ou issues do tipo risco |

Se as ações 1 a 4, 9, 12 e 13 forem cumpridas na Sprint 4, a próxima avaliação pode registrar capacidade 2 em PLAN, MC, PQA, PR e MPM. Seria o passo que falta para o nível de maturidade 2.

## Referências

- CMMI Institute, *CMMI Levels of Capability and Performance*: [cmmiinstitute.com/learning/appraisals/levels](https://cmmiinstitute.com/learning/appraisals/levels)
- Spyrosoft, *Process areas in CMMI 2.0 model* (lista das 25 áreas de prática): [spyro-soft.com/blog/process-areas-in-cmmi-2-0-model](https://spyro-soft.com/blog/process-areas-in-cmmi-2-0-model)
