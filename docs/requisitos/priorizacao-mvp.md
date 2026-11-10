# Priorização dos requisitos e recorte do MVP

Priorização dos 45 RFs do [catálogo](catalogo.md) pelo método **MoSCoW** e definição de um **recorte único do MVP**, a versão v0.2.0 do Deliverable 2, com critérios de conclusão. Substitui a seção 8.3 do Documento Completo do D1, que priorizava histórias de usuário e dividia o produto em três MVPs.

## 1. Método

O Product Owner classificou cada RF em uma categoria do MoSCoW com quatro critérios, nesta ordem:

| # | Critério | Pergunta |
|---|---|---|
| C1 | Hipótese e visão | Sem este RF, a demonstração deixa de mostrar o ciclo da hipótese (atividade → AI Credits → nível de autonomia) ou a governança por maturidade (AI Maturity Score)? |
| C2 | Necessidade da persona | Este RF atende sozinho a uma necessidade de N01 a N08 da [visão](../produto/visao.md#3-personas-e-necessidades)? |
| C3 | Dependência | Outro RF classificado como Must depende deste? |
| C4 | Custo e risco | Dá para fazer e testar dentro da Sprint 4 (até 13/11)? |

| Categoria | Regra de classificação | Destino |
|---|---|---|
| **Must have** | C1 = sim, ou C3 = sim | Recorte do MVP v0.2.0 |
| **Should have** | C2 = sim, mas a demonstração da hipótese funciona sem ele | Entra no v0.2.0 se já estiver pronto; senão, no piloto |
| **Could have** | Acrescenta valor analítico ou de engajamento, sem ligação direta com a hipótese | Só se sobrar capacidade |
| **Won't have (agora)** | Fora do recorte, com motivo registrado | Piloto (D3) ou backlog futuro |

## 2. Classificação

### Must have — 28 RFs

| RF | Requisito | Justificativa | Situação |
|---|---|---|---|
| RF18 | Perfis e permissões | C1: governança é o centro da visão. C3: todos os RFs dependem dele | Implementado |
| RF16 | Empresas e equipes | C3: o AMS (RF40) e os dashboards são por equipe | Implementado |
| RF01 | Cadastrar colaborador | C3: base de tudo | Implementado |
| RF17 | Colaborador na equipe | C3: AMS e dashboard da equipe | Implementado |
| RF38 | Auditoria | C1: a visão promete regras auditáveis (N06) | Implementado |
| RF19 | Atividades produtivas | C1: início do ciclo da hipótese | Implementado sem teste dedicado |
| RF20 | Registrar atividade | C1 | Implementado |
| RF21 | Regras de elegibilidade | C1: define o que é "uso produtivo" | Implementado |
| RF12 | AI Credits por atividade elegível | C1 | Implementado |
| RF22 | Crédito ou débito manual | C1: o gestor corrige e reconhece fora das regras | Implementado |
| RF23 | Saldo | C1 | Implementado |
| RF24 | Histórico de AI Credits | C1: o colaborador precisa entender de onde veio o saldo (N04) | Implementado sem teste dedicado |
| RF25 | Níveis de autonomia | C1: fim do ciclo da hipótese | Implementado |
| RF26 | Nível atual | C1 | Implementado |
| RF40 | Calcular o AI Maturity Score | C1: métrica de governança da visão e indicador I1 da hipótese | Não implementado (Sprint 4) |
| RF41 | Painel de maturidade | C1: sem painel, o diretor não vê o AMS (N07) | Não implementado (Sprint 4) |
| RF13 | Visão consolidada | C2 e C1: o diretor decide o investimento (N05) | Implementado |
| RF03 | Indicadores e valores | C3: dashboards, metas e alertas dependem dele | Implementado |
| RF04 | Criar meta | C2: N01 | Implementado sem teste dedicado |
| RF29 | Acompanhar metas | C2: N01 e N03 | Implementado sem teste dedicado |
| RF30 | Progresso de meta | C3: conclusão de meta alimenta conquistas | Implementado |
| RF05 | Dashboard do colaborador | C2: N03 | Implementado |
| RF32 | Dashboard da equipe | C2: N01 | Implementado |
| RF07 | Lançar pontos | C3: pontuação e níveis de evolução | Implementado |
| RF27 | Pontuação total | C3 | Implementado |
| RF37 | Pontuar tarefas entregues | C1: mede eficiência no uso de tokens (indicador I4 da hipótese) | Implementado |
| RF28 | Ranking por janela | C3: base do RF08 e do RF14 | Implementado |
| RF08 | Ver ranking | C2: N03 | Implementado |

### Should have — 11 RFs e 1 RNF

| RF | Requisito | Justificativa |
|---|---|---|
| RF02 | Editar colaborador e desligar | A demo funciona com o cadastro inicial; necessário para uso contínuo |
| RF06 | Histórico de desempenho | Útil para a gestora (N01), mas a demo mostra o valor atual |
| RF11 | Histórico de pontuação | Explica a pontuação (N03); o ranking já mostra o resultado |
| RF14 | Comparar equipes por pontuação | N05; o diretor já tem a visão consolidada e o painel de maturidade |
| RF15 | Análises de gestão | N02; acelera a gestora, mas não é parte do ciclo da hipótese |
| RF31 | Conquistas e níveis de evolução | Engajamento (N03); não afeta autonomia |
| RF33 | Indicadores da equipe | Apoia o dashboard da equipe |
| RF34 | Filtrar por período | Usado nas análises; a demo usa a janela padrão |
| RF35 | Evolução do indicador | US18 era Should no D1: depende de vários meses de dados |
| RF36 | Comparar indicadores entre equipes | Complementa o RF14 |
| RF42 | AI Literacy Score | Refina a hipótese no nível individual, mas depende de dados de tokens por tarefa. O RF37 já cobre a parte de eficiência |
| RNF09 (isolamento entre empresas) | Verificar e bloquear acesso a dados de outra empresa (SMAN-344) | Obrigatório antes do piloto. No MVP, fica como limitação declarada |

### Could have — 5 RFs

| RF | Requisito | Justificativa |
|---|---|---|
| RF09 | Desafios | Engajamento, sem ligação com a hipótese |
| RF10 | Reconhecimentos | Engajamento, sem ligação com a hipótese |
| RF43 | Recomendações de licença (ML) | Resultado de outra disciplina; acrescenta valor analítico |
| RF44 | Plano ótimo de licenças (Pesquisa Operacional) | Idem |
| RF45 | Previsão de demanda e orçamento (Análise Preditiva) | Idem; era US25, Could no D1 |

### Won't have (agora)

| Item | Motivo | Quando |
|---|---|---|
| RF39 Login real | A demo usa identificação por cabeçalho ([ADR-0006](../adr/0006-autenticacao-por-cabecalho.md)), o que basta para mostrar regras e permissões. A limitação está declarada na [visão](../produto/visao.md#limites-da-demonstração) | Obrigatório antes do piloto (D3) |
| US20 Relatórios exportáveis | Baixa prioridade no D1, e os painéis cobrem a necessidade da demo | Backlog futuro |
| Integração automática com Jira e ferramentas de IA | Os dados entram por cadastro e CSV | Piloto (D3) |

### Mudanças em relação ao D1

| Item | D1 | Agora | Por quê |
|---|---|---|---|
| Unidade priorizada | Histórias de usuário (US) | RFs, com a US de origem no catálogo | A avaliação pediu a técnica aplicada ao conjunto de RFs |
| Releases | MVP 1, 2 e 3 | Um recorte (v0.2.0) + piloto | A avaliação pediu um recorte único do MVP |
| Login (US01) | Must | Won't (agora), com limitação declarada | O MVP é uma demonstração; o login real entra antes do piloto |
| Auditoria (US23) | Should | Must | A visão revisada põe a governança no centro |
| Conquistas (US14) | Should | Should | Sem mudança |
| AI Maturity Score | Não existia | Must | Pedido da avaliação e centro da visão revisada |

## 3. Recorte do MVP v0.2.0

O MVP é o conjunto dos **28 RFs Must** acima, mais os Should e Could que já estão implementados (RF02, RF06, RF09, RF10, RF11, RF14, RF15, RF31 e RF33 a RF36), que seguem na versão sem fazer parte do critério de conclusão.

### Critérios de conclusão

O MVP v0.2.0 só está concluído quando os sete critérios forem cumpridos:

| # | Critério | Evidência | Situação em 10/11 |
|---|---|---|---|
| M1 | Os 28 RFs Must estão implementados no `main` | Código e [matriz](rastreabilidade.md) | 26 de 28. Faltam RF40 e RF41 (SMAN-334 a 336 e SMAN-340) |
| M2 | Cada CA dos RFs Must é verificado por teste automatizado passando ou por caso manual executado e registrado | [Matriz](rastreabilidade.md), seção 8 do plano de testes | Parcial. RF04, RF19, RF24 e RF29 não têm teste dedicado, e os casos manuais TM-01 a TM-12 não foram executados (SMAN-356) |
| M3 | Suíte completa passando, com cobertura de pelo menos 70% de linhas e ramos | Relatório do JaCoCo da versão final | Atendido em 23/10; refazer depois das integrações (SMAN-348 e SMAN-359) |
| M4 | O ambiente sobe com `docker compose up` e os pipelines dos três serviços estão verdes | Print do Docker e dos pipelines | Docker atendido (23/10). Pipeline do backend precisa de correção (SMAN-345) |
| M5 | Validação com pelo menos 1 participante por persona, avaliada pelos critérios da [visão, seção 5.3](../produto/visao.md#53-validação-de-curto-prazo-deliverable-2) | Tabela de registro em `docs/validacao/validacao-parcial.md` | Não iniciado (SMAN-187) |
| M6 | Tag `v0.2.0` no commit entregue | `git tag` | Não iniciado (ação 12 do plano de melhoria) |
| M7 | Limites da demonstração (login e isolamento) declarados | [Visão, seção 6](../produto/visao.md#limites-da-demonstração) | Atendido |

**Situação em 10/11/2026: MVP não concluído.** Os critérios M1, M2, M5 e M6 dependem do trabalho da Sprint 4 (05/11 a 13/11), cuja meta é "colocar os resultados analíticos dentro da plataforma, validar com usuários e fechar a demonstração para a empresa".

## 4. Estimativas e capacidade

Os RFs Must que faltam têm cards na Sprint 4, mas **as issues não têm story points registrados** no Jira, então não há estimativa nem capacidade por pessoa para mostrar aqui. Essa lacuna é a ação 15 do plano de melhoria (Qualidade de Software, Parte 6). Até ela ser cumprida, a ordem de execução da Sprint 4 segue esta priorização: RF40 e RF41 → testes dos RFs Must sem teste dedicado → casos manuais → validação → tag.
