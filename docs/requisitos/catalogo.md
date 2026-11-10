# Catálogo de requisitos

Catálogo consolidado do DAMA Intelligence em 10/11/2026: requisitos funcionais (RF), requisitos não funcionais (RNF), regras de negócio (RN) e critérios de aceite (CA) por RF. A visão está em [visao.md](../produto/visao.md), a priorização em [priorizacao-mvp.md](priorizacao-mvp.md) e a rastreabilidade em [rastreabilidade.md](rastreabilidade.md).

## Convenção de identificadores

Até aqui, o projeto usava três numerações diferentes:

| Fonte | Numeração | Onde aparece |
|---|---|---|
| Documento Completo do D1 (`docs/produto/DAMA-Intelligence-Documento-Completo.docx`) | RF01–RF23, RNF01–RNF10, RN01–RN30, US01–US25 | Entrega do D1 |
| Documento de requisitos do backend (épico POAN-109) | RF01–RF38, RNF01–RNF08, RN01–RN07 | Jira (POAN-110 a 124), código, `database/01_schema.sql`, testes |
| Este catálogo | Ver abaixo | Todos os documentos daqui em diante |

Decisões tomadas:

- **RF:** vale a numeração **RF01–RF38** do backend, porque é a que está no código, no schema, nos testes e no Jira. Os RFs novos começam em RF39. A coluna "D1" da tabela de RFs mostra o RF e a história de usuário do Documento Completo que cada um atende.
- **RNF:** vale a numeração **RNF01–RNF10** do Documento Completo, que é a mais completa. Os três RNFs citados no código são equivalentes: RNF02 do código = RNF01 (segurança), RNF04 do código = RNF06 (integridade) e RNF08 do código = RNF02 (usabilidade).
- **RN:** valem as **RN01–RN30** do Documento Completo. As regras que o backend acrescentou viram RN31 a RN38. Os RNs citados no código são equivalentes: RN01/RN07 do código = RN02 e RN22, RN02 do código = RN14, RN04 do código = RN06, RN05 do código = RN16 e RN06 do código = RN20.
- **CA:** cada RF tem seus critérios de aceite, numerados como CA-RFxx.n. Eles substituem os CA01–CA15 citados no POAN-123, dos quais só os de US07, US10, US12 e US15 estavam escritos. Esses quatro foram incorporados aos RFs correspondentes (CA-RF12, CA-RF27, CA-RF04 e CA-RF32).

**Situação:** *Implementado* = código no `main` e coberto por teste automatizado. *Implementado sem teste dedicado* = código no `main`, sem teste automatizado próprio. *Parcial* = só parte dos CAs atendida. *Não implementado* = ainda sem código.

## 1. Requisitos funcionais

| ID | Requisito | D1 | Módulo | Situação |
|---|---|---|---|---|
| RF01 | Cadastrar colaborador | RF01 / US02 | `colaborador` | Implementado |
| RF02 | Consultar e editar dados do colaborador, incluindo desligamento | RF02 / US03 | `colaborador` | Implementado |
| RF03 | Cadastrar indicadores de desempenho e registrar seus valores | — | `indicador` | Implementado |
| RF04 | Criar meta de desempenho | RF14 / US12 | `meta` | Implementado sem teste dedicado |
| RF05 | Visualizar dashboard de desempenho do colaborador | RF17 / US15 | `dashboard` | Implementado |
| RF06 | Consultar histórico de desempenho | RF22 / US21 | `indicador` | Implementado |
| RF07 | Lançar pontos a partir de uma regra de pontuação | RF12 / US10 | `pontuacao` | Implementado |
| RF08 | Visualizar ranking de colaboradores | RF13 / US11 | `analytics` | Implementado |
| RF09 | Criar desafios e gerenciar inscrição e conclusão | — | `desafio` | Implementado |
| RF10 | Reconhecer colaborador | — | `reconhecimento` | Implementado |
| RF11 | Consultar histórico de pontuação | US09 | `pontuacao` | Implementado sem teste dedicado |
| RF12 | Conceder AI Credits ao registrar atividade elegível | RF07 / US07 | `aicredit` | Implementado |
| RF13 | Visualizar visão consolidada da empresa | US19 | `analytics` | Implementado |
| RF14 | Comparar equipes por pontuação | RF21 / US17 | `analytics` | Implementado |
| RF15 | Gerar análises de gestão (alertas) | US24 | `analytics`, `alert-service` | Implementado |
| RF16 | Cadastrar empresas e equipes | US22 | `organizacao` | Implementado |
| RF17 | Associar colaborador a uma equipe | US22 | `colaborador`, `organizacao` | Implementado |
| RF18 | Gerenciar perfis e permissões de acesso | RF03 / US04 | `perfil`, `shared.security` | Implementado |
| RF19 | Cadastrar atividades produtivas | RF04 / US05 | `aicredit` | Implementado sem teste dedicado |
| RF20 | Registrar atividade realizada pelo colaborador | RF05 / US06 | `aicredit` | Implementado |
| RF21 | Definir regras de elegibilidade de atividades para AI Credits | RF06 | `aicredit` | Implementado |
| RF22 | Conceder ou debitar AI Credits manualmente | RF07 / US07 | `aicredit` | Implementado |
| RF23 | Consultar saldo de AI Credits | RF08 / US08 | `aicredit` | Implementado |
| RF24 | Consultar histórico de AI Credits | RF09 / US09 | `aicredit` | Implementado sem teste dedicado |
| RF25 | Definir níveis de autonomia de acesso à IA | RF10 | `aicredit` | Implementado |
| RF26 | Consultar nível de autonomia atual | RF10, RF11 | `aicredit` | Implementado |
| RF27 | Calcular pontuação total do colaborador | RF12 / US10 | `pontuacao` | Implementado |
| RF28 | Calcular ranking em uma janela de tempo | RF13 / US11 | `analytics` | Implementado |
| RF29 | Listar e acompanhar metas por colaborador, equipe e status | RF15 / US13 | `meta` | Implementado sem teste dedicado |
| RF30 | Registrar progresso de meta | RF15 / US13 | `meta` | Implementado |
| RF31 | Conceder conquistas e calcular níveis de evolução | RF16 / US14 | `conquista` | Implementado |
| RF32 | Visualizar dashboard da equipe | RF17 / US15 | `dashboard` | Implementado |
| RF33 | Consultar indicadores da equipe | RF18 | `analytics`, `dashboard` | Implementado |
| RF34 | Filtrar indicadores por período | RF19 / US16 | `indicador` | Implementado |
| RF35 | Visualizar evolução de um indicador por colaborador ou equipe | RF20 / US18 | `indicador` | Implementado |
| RF36 | Comparar indicadores entre equipes | RF21 / US17 | `analytics` | Implementado sem teste dedicado |
| RF37 | Pontuar tarefas entregues (story points, urgência, uso de IA e bônus do gestor) | RF12 / US10 | `pontuacao`, `analytics` | Implementado |
| RF38 | Registrar movimentações para auditoria | RF23 / US23 | `auditoria` | Implementado |
| RF39 | Autenticar usuário com credenciais (login real) | US01 | — | Não implementado |
| RF40 | Calcular o AI Maturity Score por equipe | — | — | Não implementado |
| RF41 | Visualizar painel de maturidade (AMS, componentes e quadrante de investimento) | — | — | Não implementado |
| RF42 | Calcular o AI Literacy Score do colaborador | — | — | Não implementado |
| RF43 | Visualizar recomendações de licença do modelo de ML | US25 | — | Não implementado |
| RF44 | Visualizar o plano ótimo de licenças e seus cenários | — | — | Não implementado |
| RF45 | Visualizar previsão de demanda e de estouro de orçamento | US25 | — | Não implementado |

Ficou fora do catálogo a US20 (relatórios exportáveis), classificada como *Won't have* na [priorização](priorizacao-mvp.md).

## 2. Critérios de aceite por RF

Cada critério é verificável por teste automatizado, caso manual ou inspeção. O teste ou evidência de cada RF está na [matriz de rastreabilidade](rastreabilidade.md).

**RF01 — Cadastrar colaborador**
- CA-RF01.1: Um usuário com a permissão `COLABORADOR_ESCREVER` (Administrador ou Gestor) cadastra um colaborador informando empresa, equipe, nome e e-mail. O colaborador aparece na lista da empresa.
- CA-RF01.2: O cadastro é recusado se a empresa ou a equipe não existir ou se o e-mail já estiver em uso.
- CA-RF01.3: O cadastro gera um registro de auditoria com autor, data e ação `CRIACAO`.

**RF02 — Consultar e editar dados do colaborador**
- CA-RF02.1: A consulta de um colaborador inexistente retorna 404.
- CA-RF02.2: A edição retorna os dados já atualizados e gera auditoria `ATUALIZACAO`.
- CA-RF02.3: O desligamento é lógico (muda a situação para desligado) e preserva o histórico do colaborador.

**RF03 — Cadastrar indicadores e registrar valores**
- CA-RF03.1: O indicador tem nome, unidade e sentido ("maior é melhor" ou "menor é melhor"). Sem informar o sentido, vale "maior é melhor".
- CA-RF03.2: Cada valor pertence a exatamente um colaborador **ou** uma equipe da mesma empresa. Sem dono, com os dois ou com dono de outra empresa, o registro é recusado.

**RF04 — Criar meta** (inclui o CA de US12 do D1)
- CA-RF04.1: O gestor seleciona um colaborador **ou** uma equipe e informa objetivo, valor alvo, data de início e data de fim.
- CA-RF04.2: Uma meta sem responsável é recusada.
- CA-RF04.3: A meta criada fica disponível para acompanhamento (RF29) com situação "em andamento".

**RF05 — Dashboard do colaborador**
- CA-RF05.1: O dashboard mostra os indicadores, as metas, os pontos, o saldo de AI Credits e o nível do colaborador.
- CA-RF05.2: Os números são calculados a partir dos dados de origem no momento da consulta, sem cópia armazenada (RNF06).
- CA-RF05.3: Só abre para quem tem a permissão `DESEMPENHO_LER`.

**RF06 — Histórico de desempenho**
- CA-RF06.1: A consulta devolve todos os valores registrados de um indicador, em ordem cronológica.
- CA-RF06.2: Valores antigos não são sobrescritos: um novo registro acrescenta um ponto à série.

**RF07 — Lançar pontos**
- CA-RF07.1: Só o Administrador lança pontos (`PONTOS_LANCAR`). O Gestor recebe 403.
- CA-RF07.2: O lançamento exige uma regra de pontuação ativa e um colaborador ativo, ambos da mesma empresa.
- CA-RF07.3: Sem descrição informada, o lançamento usa a descrição da regra. O lançamento dispara a avaliação de conquistas.

**RF08 — Ranking de colaboradores**
- CA-RF08.1: O perfil Colaborador consegue ver o ranking.
- CA-RF08.2: O ranking é ordenado pela pontuação do RF37 e mostra uma nota de 0 a 10, em que 10 é a maior pontuação da empresa.
- CA-RF08.3: Sem nenhuma entrega na janela, todas as notas são 0.

**RF09 — Desafios**
- CA-RF09.1: O desafio tem período e recompensa. A empresa e o período são validados na criação.
- CA-RF09.2: A inscrição só é aceita se o desafio estiver "Planejado" ou "Em andamento", se o colaborador estiver ativo e for da mesma empresa, e se não houver inscrição duplicada.
- CA-RF09.3: A conclusão credita a recompensa quando ela é positiva e impede nova conclusão da mesma participação.

**RF10 — Reconhecer colaborador**
- CA-RF10.1: Quem concede é sempre o usuário autenticado. O colaborador reconhecido precisa estar ativo e ser da mesma empresa.
- CA-RF10.2: O reconhecimento dispara a avaliação de conquistas, inclusive as criadas depois.

**RF11 — Histórico de pontuação**
- CA-RF11.1: O histórico lista os lançamentos do colaborador, dos mais recentes para os mais antigos, com regra, pontos e descrição.

**RF12 — Conceder AI Credits por atividade elegível** (inclui o CA de US07 do D1)
- CA-RF12.1: Ao registrar uma atividade elegível (RF20), cada regra ativa da atividade gera um crédito com o valor fixo da regra. A quantidade informada não multiplica o crédito.
- CA-RF12.2: O crédito fica registrado como movimentação, soma ao saldo (RF23) e aparece no histórico (RF24).
- CA-RF12.3: Uma atividade sem regra ativa continua registrada, mas não gera crédito.

**RF13 — Visão consolidada**
- CA-RF13.1: A visão soma os pontos da empresa sem multiplicá-los por número de equipes ou de metas.
- CA-RF13.2: Uma empresa sem dados retorna zeros, e não erro.

**RF14 — Comparar equipes por pontuação**
- CA-RF14.1: As equipes são comparadas pela pontuação média por colaborador ativo, na mesma janela do ranking.
- CA-RF14.2: Uma equipe pequena com média maior fica à frente de uma equipe grande com média menor.
- CA-RF14.3: A comparação acontece só entre equipes da mesma empresa.

**RF15 — Análises de gestão**
- CA-RF15.1: São gerados os cinco tipos de alerta da RN35 (meta vencida, meta em risco, indicador em queda, equipe abaixo da média e sem atividade recente), com as severidades definidas.
- CA-RF15.2: Os alertas vêm ordenados da maior para a menor severidade, e a queda é calculada só dentro da mesma série.
- CA-RF15.3: Se o `alert-service` não responder, o backend aplica as mesmas regras localmente.
- CA-RF15.4: O perfil Colaborador não vê os alertas.

**RF16 — Cadastrar empresas e equipes**
- CA-RF16.1: O Administrador cria e lista empresas e as equipes de cada empresa.
- CA-RF16.2: Uma equipe com nome repetido na mesma empresa, ou em empresa inexistente, é recusada.

**RF17 — Associar colaborador a uma equipe**
- CA-RF17.1: Todo colaborador pertence a uma equipe da própria empresa. Uma equipe de outra empresa é recusada.

**RF18 — Perfis e permissões**
- CA-RF18.1: Existem três perfis padrão (Administrador, Gestor e Colaborador), com as permissões da RN37.
- CA-RF18.2: Um perfil com permissão desconhecida é recusado.
- CA-RF18.3: Uma requisição sem identificação do usuário retorna 401.
- CA-RF18.4: O perfil Colaborador vê o ranking, mas recebe 403 em alertas e auditoria.

**RF19 — Cadastrar atividades produtivas**
- CA-RF19.1: O Gestor ou Administrador (`AI_CREDITS_GERENCIAR`) cadastra uma atividade da empresa com nome e descrição. A atividade nasce ativa, e uma atividade inativa não aceita registros (CA-RF20.2).
- CA-RF19.2: O cadastro gera auditoria.

**RF20 — Registrar atividade realizada**
- CA-RF20.1: O registro informa colaborador, atividade e data. Sem quantidade, vale 1.
- CA-RF20.2: O registro é recusado se o colaborador estiver inativo ou se a atividade estiver inativa ou for de outra empresa.

**RF21 — Regras de elegibilidade**
- CA-RF21.1: Uma atividade é elegível quando tem ao menos uma regra de crédito ativa.
- CA-RF21.2: A regra define a quantidade fixa de créditos e um critério de elegibilidade descritivo, que é lido pelo gestor e não interpretado pelo sistema.
- CA-RF21.3: A regra só se aplica a atividades da mesma empresa.

**RF22 — Crédito ou débito manual**
- CA-RF22.1: Só Gestor e Administrador lançam (`AI_CREDITS_GERENCIAR`).
- CA-RF22.2: O crédito exige colaborador ativo e um motivo (evento).
- CA-RF22.3: Um débito maior que o saldo é recusado.

**RF23 — Saldo**
- CA-RF23.1: O saldo é sempre créditos menos débitos, calculado na consulta, nunca armazenado.

**RF24 — Histórico de AI Credits**
- CA-RF24.1: Cada crédito ou débito aparece com data, valor, tipo, motivo e origem (regra e atividade registrada, ou lançamento manual). O autor fica na auditoria (RF38).

**RF25 — Níveis de autonomia**
- CA-RF25.1: O nível tem nome, mínimo de créditos, ordem e a descrição do acesso que libera. A base sai com quatro níveis: Básico (0), Ampliado (100), Avançado (250) e Total (500).

**RF26 — Nível de autonomia atual**
- CA-RF26.1: O nível atual é o maior nível cujo mínimo o saldo atinge. Se o saldo cair, o nível cai.
- CA-RF26.2: O saldo e o nível são mostrados juntos ao colaborador.

**RF27 — Pontuação total** (inclui o CA de US10 do D1)
- CA-RF27.1: A pontuação é calculada automaticamente a partir dos lançamentos registrados.
- CA-RF27.2: O valor é atualizado logo após um novo lançamento.
- CA-RF27.3: O colaborador consegue ver a própria pontuação.

**RF28 — Ranking por janela de tempo**
- CA-RF28.1: Sem período informado, a janela vai dos últimos 30 dias até hoje. Um período com início depois do fim é recusado.
- CA-RF28.2: Só entram as tarefas entregues dentro da janela.

**RF29 — Acompanhar metas**
- CA-RF29.1: As metas podem ser listadas por colaborador, equipe e situação.
- CA-RF29.2: Cada meta mostra o valor atual, o alvo e o percentual atingido.

**RF30 — Registrar progresso de meta**
- CA-RF30.1: A meta é concluída automaticamente quando o valor atual atinge o alvo: valor ≥ alvo, ou ≤ alvo se o indicador for "menor é melhor".
- CA-RF30.2: Uma meta concluída ou cancelada recusa novo progresso.
- CA-RF30.3: A conclusão dispara a avaliação de conquistas.

**RF31 — Conquistas e níveis de evolução**
- CA-RF31.1: As conquistas automáticas são avaliadas a cada movimentação do colaborador, e cada uma é obtida uma vez só.
- CA-RF31.2: Uma conquista criada depois vai para quem já cumpre o critério.
- CA-RF31.3: A conquista manual é concedida por um gestor, uma vez só.
- CA-RF31.4: O nível de evolução é o maior nível cujo mínimo de pontos foi atingido. O perfil mostra quanto falta para o próximo.

**RF32 — Dashboard da equipe** (inclui o CA de US15 do D1)
- CA-RF32.1: O gestor abre o dashboard da equipe, com os indicadores da equipe, as metas da equipe e dos membros, os pontos, os AI Credits, as conquistas e a posição no ranking de equipes.
- CA-RF32.2: Os indicadores usam os dados registrados, e a data de referência é a do relógio do sistema.
- CA-RF32.3: É possível ver a evolução dos indicadores (RF35).
- CA-RF32.4: O acesso exige `DESEMPENHO_LER`.

**RF33 — Indicadores da equipe**
- CA-RF33.1: A consulta devolve o valor mais recente de cada indicador para cada equipe.

**RF34 — Filtrar por período**
- CA-RF34.1: As consultas de série aceitam início e fim opcionais e devolvem só os valores do intervalo.
- CA-RF34.2: Um período inválido (início depois do fim) é recusado.

**RF35 — Evolução de um indicador**
- CA-RF35.1: A série pode ser filtrada por colaborador ou por equipe, em ordem cronológica.
- CA-RF35.2: É a base do alerta de indicador em queda (RF15).

**RF36 — Comparar indicadores entre equipes**
- CA-RF36.1: A comparação mostra o último valor de um indicador (ou de todos) para cada equipe da empresa, nunca entre empresas.

**RF37 — Pontuar tarefas entregues**
- CA-RF37.1: Os pontos da tarefa são os story points multiplicados pela urgência (baixa 1,00; média 1,25; alta 1,50; crítica 2,00). Sem urgência informada, vale média.
- CA-RF37.2: Os primeiros 25% do limite de tokens da tarefa (20.000 tokens por story point) não descontam nada. Depois disso, a penalidade cresce de forma linear até 100% dos pontos.
- CA-RF37.3: O bônus do gestor fica limitado a 15% dos pontos líquidos, e a pontuação nunca fica negativa.
- CA-RF37.4: Uma tarefa repetida é recusada, assim como uma tarefa de colaborador inativo.

**RF38 — Auditoria**
- CA-RF38.1: A criação e a alteração de colaboradores, AI Credits, regras, atividades, metas, indicadores, pontuação, desafios, reconhecimentos e conquistas registram empresa, autor, entidade, ação e data.
- CA-RF38.2: Só o Administrador consulta a auditoria.

**RF39 — Login real**
- CA-RF39.1: O usuário entra com credenciais e recebe uma sessão ou token com prazo de validade.
- CA-RF39.2: Uma requisição com identidade forjada (por exemplo, trocar o identificador no cabeçalho) é recusada.
- CA-RF39.3: Um usuário de uma empresa não acessa dados de outra (RNF09).

**RF40 — Calcular o AI Maturity Score**
- CA-RF40.1: O AMS é calculado por equipe com os 8 indicadores e a fórmula da [visão, seção 4.2](../produto/visao.md#42-cálculo), de 0 a 100.
- CA-RF40.2: Equipes com menos de 5 colaboradores ativos ficam de fora. Equipes sem uso de IA recebem 0.
- CA-RF40.3: Com a base sintética, a plataforma reproduz os valores do relatório de Estatística Multivariada (por exemplo, a média de 82,4 em Tecnologia), com diferença máxima de 0,1.

**RF41 — Painel de maturidade**
- CA-RF41.1: O diretor vê o AMS de cada equipe, as dimensões Uso e Capacitação, a faixa e a ação sugerida.
- CA-RF41.2: O painel mostra o quadrante AMS × IINV e destaca as equipes no quadrante de desperdício.
- CA-RF41.3: O perfil Colaborador não abre o painel.

**RF42 — AI Literacy Score**
- CA-RF42.1: O score combina resultado, tokens usados e complexidade da tarefa.
- CA-RF42.2: Entre duas tarefas de mesma complexidade e mesmo resultado, a que usa menos tokens tem score maior. Uma tarefa mais complexa não é penalizada só por usar mais tokens.

**RF43 — Recomendações de licença**
- CA-RF43.1: A tela mostra a recomendação do modelo por colaborador e equipe e as métricas de desempenho do modelo no teste.

**RF44 — Plano ótimo de licenças**
- CA-RF44.1: A tela mostra o plano ótimo e a comparação entre cenários de orçamento e de exigência de capacitação.

**RF45 — Previsão de demanda e orçamento**
- CA-RF45.1: O painel mostra a demanda prevista por mês e destaca as equipes com risco de estourar o orçamento.

## 3. Requisitos não funcionais

| ID | Requisito | Critério verificável | Situação |
|---|---|---|---|
| RNF01 | Segurança | Toda rota exige usuário identificado e a permissão do perfil. Sem identificação, 401; sem permissão, 403 | Parcial: as permissões funcionam, mas a identificação é por cabeçalho, sem senha ([ADR-0006](../adr/0006-autenticacao-por-cabecalho.md)). Depende do RF39 |
| RNF02 | Usabilidade | Na validação, pelo menos 2 de 3 tarefas concluídas sem ajuda e nota de facilidade média de pelo menos 4 de 5. Indicadores com nome e unidade | Não verificado: sessões pendentes (SMAN-187) |
| RNF03 | Desempenho | Dashboards e ranking respondem em menos de 2 s com a base de demonstração | Não medido |
| RNF04 | Disponibilidade | O ambiente sobe com `docker compose up` e responde em `/actuator/health` (API) e `/health` (alertas) | Atendido no ambiente de demonstração (Anexo A da Qualidade) |
| RNF05 | Manutenibilidade | Monólito modular com `api`/`internal` por módulo, decisões em ADR, cobertura de linhas e ramos de pelo menos 70% (regra do JaCoCo) | Atendido (execução de 23/10) |
| RNF06 | Integridade dos dados | Saldo, pontuação e dashboards são derivados, nunca armazenados. As restrições do banco impedem dados sem dono | Atendido |
| RNF07 | Auditoria e rastreabilidade | Alterações relevantes registram autor e data (RF38) | Atendido nos 8 módulos que alteram dados de desempenho e gamificação. Perfis e organização não são auditados |
| RNF08 | Escalabilidade | Ranking e dashboards mantêm o RNF03 com 10 vezes a base de demonstração | Não medido |
| RNF09 | Privacidade | O colaborador vê só os próprios dados, o gestor só os da sua equipe, e ninguém vê dados de outra empresa | **Não atendido**: o acesso é controlado só por perfil, sem checar dono, equipe ou empresa (SMAN-344) |
| RNF10 | Compatibilidade | O front funciona em Chrome, Edge e Firefox atuais, nas larguras de celular, tablet e desktop | Parcial: evidências de responsividade em `docs/evidencias/responsividade`. Navegadores não testados |

## 4. Regras de negócio

As regras do Documento Completo (RN01–RN30), com a forma como foram implementadas. As regras concretas do backend estão em [regras-de-negocio.md](regras-de-negocio.md), referidas aqui como "regra n".

| ID | Regra (D1) | Implementação | Situação |
|---|---|---|---|
| RN01 | Identificação única do colaborador | Identificador próprio e e-mail único (CA-RF01.2) | Implementada |
| RN02 | Perfil de acesso por função | Três perfis padrão (RF18), regras 15 e 16 | Implementada |
| RN03 | Colaborador associado a uma equipe | RF17 | Implementada |
| RN04 | Atividade com colaborador, data e contexto | RF20 | Implementada |
| RN05 | Só atividades elegíveis geram benefício | Regra 7 (RF21) | Implementada |
| RN06 | Atividade elegível gera AI Credits | Regra 7 (RF12) | Implementada |
| RN07 | Créditos proporcionais ao valor da atividade | Crédito fixo por regra; critério descritivo, não interpretado (regra 8) | Parcial |
| RN08 | Histórico de créditos | RF24 | Implementada |
| RN09 | Saldo = acumulado − utilizado | Regra 9 (RF23) | Implementada |
| RN10 | Créditos desbloqueiam níveis de acesso | O nível é derivado do saldo (regra 10). Não há "compra" de um período de acesso com créditos | Parcial |
| RN11 | Acesso respeita as políticas de segurança | A descrição de cada nível diz que dados ele permite. Quem aplica o bloqueio é a TI da empresa | Fora do sistema |
| RN12 | Níveis de acesso definidos pela empresa | RF25 | Implementada |
| RN13 | Fluência em IA considerada no nível | O ranking desconta o uso excessivo de tokens (RF37). O AI Literacy Score (RF42) está na Sprint 4 | Parcial |
| RN14 | Pontuação a partir de critérios definidos | Regra 4 e RN36 (RF07, RF37) | Implementada |
| RN15 | Ranking por período e contexto | Janela de tempo, dentro da empresa (RF28) | Implementada |
| RN16 | Metas acompanháveis | Regras 1 e 2 (RF04, RF30) | Implementada |
| RN17 | Progresso calculado a partir dos indicadores | O progresso é informado no RF30; a conclusão é automática, mas o valor não é lido do indicador | Parcial |
| RN18 | Conquistas por critério | Regras 11 a 14 (RF31) | Implementada |
| RN19 | Gestor vê o consolidado da equipe conforme permissão | Permissão por perfil, sem restringir à equipe do gestor | Parcial |
| RN20 | Indicadores calculados com dados da plataforma | Derivados na consulta (RNF06) | Implementada |
| RN21 | Atualização após movimentações | Pontos, saldo e conquistas recalculados a cada movimentação | Implementada |
| RN22 | Acesso conforme o nível de permissão | Permissões por perfil, sem restrição por dono ou empresa (RNF09) | Parcial |
| RN23 | Colaborador vê os próprios dados | Vê, mas também consegue consultar dados de outros colaboradores | Parcial |
| RN24 | Gestor vê os colaboradores das suas equipes | Vê, mas não fica restrito às próprias equipes | Parcial |
| RN25 | Movimentação de crédito sempre com colaborador e evento | CA-RF22.2 | Implementada |
| RN26 | Impedir créditos sem critério atendido | Crédito automático só com regra ativa. O crédito manual exige motivo e fica auditado | Implementada |
| RN27 | Registro para auditoria | RF38 | Implementada |
| RN28 | Histórico de desempenho preservado | Séries sem sobrescrita e desligamento lógico (RF06, CA-RF02.3) | Implementada |
| RN29 | Desbloqueio não elimina controles de segurança | Diretriz para a TI da empresa | Fora do sistema |
| RN30 | Autonomia ligada à produtividade demonstrada | O nível vem do saldo, que vem de atividades produtivas (RF12, RF26) | Implementada |

Regras acrescentadas no D2:

| ID | Regra | Origem | Situação |
|---|---|---|---|
| RN31 | Colaborador desligado não recebe pontos, créditos, reconhecimentos nem conquistas e não se inscreve em desafios | Regra 5 | Implementada |
| RN32 | Meta concluída ou cancelada não aceita novo progresso | Regra 3 | Implementada |
| RN33 | Inscrição em desafio só com status "Planejado" ou "Em andamento"; conclusão só com "Em andamento" | Regra 6 | Implementada |
| RN34 | Conquistas não são retiradas se o critério deixar de ser cumprido | Regra 13 | Implementada |
| RN35 | Alertas de gestão: meta vencida (alta); meta em risco (média); indicador em queda de 10% ou mais (média; alta a partir de 25%); equipe abaixo de metade da média (média); sem atividade há 30 dias (baixa) | Tabela de alertas | Implementada |
| RN36 | Pontuação do ranking por tarefas entregues: urgência, franquia de 25% dos tokens, bônus do gestor de até 15% e nota de 0 a 10 | [ranking.md](ranking.md) | Implementada |
| RN37 | Pontos só pelo Administrador. Créditos, metas e reconhecimentos por Gestor ou Administrador. Colaborador só lê | Regra 15 e `database/03_seed.sql` | Implementada |
| RN38 | Cálculo do AI Maturity Score: 8 indicadores em duas dimensões, mínimo de 5 colaboradores ativos, escala de 0 a 100 e investimento fora do índice | [Visão, seção 4](../produto/visao.md#4-ai-maturity-score) | Não implementada |
