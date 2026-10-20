# Plano de testes

Plano de testes do DAMA Intelligence na versão 0.2.0, organizado segundo a estrutura da norma ISO/IEC/IEEE 29119-3 (que sucedeu a IEEE 829). Consolida os testes que já existem, define os que faltam e estabelece os critérios para considerar uma entrega testada.

| Item | Valor |
|---|---|
| Produto | DAMA Intelligence: backend (Spring Boot), `alert-service` (Node.js), front-end (SPA + BFF), banco (PostgreSQL 16) |
| Versão sob teste | 0.2.0 (`backend/pom.xml`), branch `main` |
| Data | 20/10/2026 (Sprint 3) |
| Relacionados | [rastreabilidade](../../docs/requisitos/rastreabilidade.md), [auditorias](auditorias.md) (AUD-03), [plano de melhoria](plano-melhoria.md) (ações 9, 10 e 11) |
| Inventário completo | [evidencias/inventario-testes.csv](evidencias/inventario-testes.csv) (88 casos automatizados) |

## 1. Escopo

**Dentro do escopo:**
- regras de negócio dos 13 módulos do backend;
- API REST e permissões por perfil;
- integração com o PostgreSQL real;
- contrato entre o backend e o `alert-service`, incluindo o fallback local;
- telas do front-end para as três personas;
- responsividade (375 px, 768 px e desktop);
- subida do ambiente completo com `docker compose`.

**Fora do escopo nesta versão:**
- login real e isolamento entre empresas (POAN-122, ainda não implementado, ver ADR-0006);
- testes de carga e desempenho;
- testes de segurança além das permissões por perfil;
- compatibilidade entre navegadores além do Chrome.

## 2. Estratégia por nível

| Nível | O que verifica | Ferramenta | Onde | Automação | Casos | Situação |
|---|---|---|---|---|---:|---|
| Unitário (backend) | Regras de cada serviço com repositório simulado | JUnit 5, Mockito | `backend/src/test/.../internal` | Sim | 55 | Existe |
| Integração (backend) | API HTTP + banco real com os scripts de `database/` | Spring Boot Test, Testcontainers (PostgreSQL 16) | `DamaApiIntegrationTest` | Sim, exige Docker | 9 | Existe; pulado quando não há Docker |
| Contrato | Backend envia o contrato certo ao `alert-service` e cai no fallback quando ele falha | JUnit 5 + servidor HTTP simulado | `AlertaGestaoNodeTest` | Sim | 5 | Existe |
| Unitário (`alert-service`) | Regras dos alertas de gestão | `node --test` | `alert-service/test/regras.test.js` | Sim | 12 | Existe |
| API HTTP (`alert-service`) | Rotas, validação de entrada e códigos de erro | `node --test` | `alert-service/test/server.test.js` | Sim | 7 | Existe |
| Front-end | Componentes e páginas da SPA | `node --test` (proposto) | `frontend/test/` | A criar | 0 | **Lacuna** (ação 10) |
| Sistema (ponta a ponta) | Fluxos completos nas telas, com a stack no ar | Roteiro manual (seção 5.2) | — | Manual | 12 | Definido neste plano |
| Aceitação | A solução resolve a dor de cada persona | Sessões com usuários | [validacao-parcial.md](../../docs/validacao/validacao-parcial.md) | Manual | 3 personas | Roteiro pronto, sessões pendentes (SMAN-187) |
| Não funcional: responsividade | Layout em 375 px, 768 px e desktop | Capturas de tela | [docs/evidencias/responsividade](../../docs/evidencias/responsividade/) | Manual | 2 (TM-09, TM-10) | Capturas existem, feitas com API simulada |

**Princípios:**
- Toda regra de negócio nova entra com teste unitário.
- Todo endpoint novo ganha pelo menos um cenário em `DamaApiIntegrationTest`.
- Toda mudança no contrato com o `alert-service` é testada dos dois lados, porque as regras existem em Java e em Node (ADR-0009).

## 3. Critérios

| Critério | Definição |
|---|---|
| **Entrada** | O código compila, `docker compose up --build` sobe os 4 contêineres e o banco foi recriado com `database/01` a `03`, mais `demo/04` para os testes manuais |
| **Saída (aprovação de uma entrega)** | 100% dos testes automatizados passando; nenhum teste de integração pulado no CI; cobertura de linhas e de ramos de pelo menos 70% nos serviços e regras (regra JaCoCo do `pom.xml`); todos os casos manuais da seção 5.2 executados, sem defeito de severidade alta aberto |
| **Suspensão** | Ambiente não sobe, ou mais de 20% dos casos manuais bloqueados pelo mesmo defeito |
| **Retomada** | Defeito bloqueante corrigido e o caso que o revelou passando |

**Severidade de defeitos:**
- **Alta:** impede um fluxo de persona ou expõe dados sem permissão.
- **Média:** resultado errado com contorno.
- **Baixa:** visual ou texto.

## 4. Ambiente e ferramentas

| Componente | Requisito |
|---|---|
| Backend | Java 17, Maven 3.9, acesso ao Maven Central |
| Integração | Docker em execução (Testcontainers baixa `postgres:16`) |
| `alert-service` | Node.js 20 ou superior, sem dependências |
| Sistema | `docker compose up --build`, navegador Chrome; dados de `database/demo/04_demo.sql` (usuários 1 a 4) |
| CI | Jenkins com o `backend/Jenkinsfile` (`mvn -B clean verify` + imagem Docker). `alert-service` e front-end ainda sem pipeline (ação 7) |

## 5. Casos de teste

### 5.1 Automatizados: resumo por história

| História | Unitário | Integração | Contrato / API | Total | Observação |
|---|---:|---:|---:|---:|---|
| POAN-110 Colaboradores | 6 | — | — | 6 | Cadastro também exercitado em todos os cenários de integração |
| POAN-111 Perfis e permissões | 2 | 2 | — | 4 | |
| POAN-112 Indicadores | 4 | — | — | 4 | Sem cenário de integração próprio |
| POAN-113 Dashboard do colaborador | 1 | — | — | 1 | **Lacuna:** sem integração (TI-02) |
| POAN-114 Metas | 4 | 1 | — | 5 | |
| POAN-115 Pontuação e ranking | 5 | — | — | 5 | |
| POAN-116 Desafios e reconhecimentos | 6 | — | — | 6 | |
| POAN-117 AI Credits | 5 | — | — | 5 | **Lacuna:** a hipótese de inovação não tem teste de integração (TI-01) |
| POAN-118 Visão consolidada | 2 | 1 | — | 3 | |
| POAN-119 Auditoria | 1 | 1 | — | 2 | |
| POAN-120 Conquistas e níveis | 9 | 3 | — | 12 | |
| POAN-121 Análises de gestão | 22 | 2 | 12 | 36 | Inclui os 19 do `alert-service` |
| POAN-122 Autenticação real | — | — | — | 0 | Não implementado |
| **Total** | **67** | **9** | **12** | **88** | 69 no backend, 19 no `alert-service` |

A matriz de rastreabilidade ainda registra POAN-113 e POAN-117 como "verificado no front e na demo", mas já existem testes unitários para as duas (`DashboardServiceTest`, `AiCreditServiceTest`). O que falta é o teste de integração.

### 5.2 Manuais (sistema e não funcional)

Executar com a stack no ar e os dados de demonstração. Registrar o resultado (Passou / Falhou / Bloqueado) e o defeito, se houver.

| ID | Persona / usuário | Passos | Resultado esperado | História | Resultado | Data |
|---|---|---|---|---|---|---|
| TM-01 | Lucas (#4, Colaborador) | Entrar como Lucas | Menu sem Análises de gestão, Pessoas e Auditoria | POAN-111 | | |
| TM-02 | Lucas (#4) | Abrir Meu painel | Pontos, AI Credits, nível, conquistas, metas e evolução dos indicadores exibidos | POAN-113 | | |
| TM-03 | Mariana (#2, Gestor) | AI Credits → Registrar atividade elegível para Lucas | Saldo de Lucas aumenta e a movimentação aparece no histórico | POAN-117 | | |
| TM-04 | Mariana (#2) | AI Credits → débito maior que o saldo | Operação recusada com mensagem clara; saldo inalterado | POAN-117 | | |
| TM-05 | Mariana (#2) | Metas → Nova meta para uma equipe; registrar progresso até o alvo | Meta criada; status muda para concluída ao atingir o alvo | POAN-114 | | |
| TM-06 | Mariana (#2) | Análises de gestão com o `alert-service` no ar | Alertas listados por severidade | POAN-121 | | |
| TM-07 | Mariana (#2) | Parar o `alert-service` (`docker compose stop alert-service`) e recarregar | A tela continua funcionando com as regras locais; o log do backend registra o fallback | POAN-121 | | |
| TM-08 | Ricardo N. (#3, Gestor) | Equipes → comparar duas equipes; Análises → visão consolidada | Totais da empresa e ranking entre equipes exibidos | POAN-118 | | |
| TM-09 | Lucas (#4) | Janela em 375 px: abrir o menu, escolher uma tela, reabrir e apertar Esc | Menu abre como gaveta e fecha ao escolher, ao tocar fora e com Esc; tabelas rolam na horizontal | RNF | | |
| TM-10 | Lucas (#4) | Janela em 768 px e em desktop | Tablet: tabela completa; desktop: menu fixo e grade de três colunas | RNF | | |
| TM-11 | Administrador (#1) e Mariana (#2) | Abrir Auditoria com cada um | Administrador vê os eventos; Gestor não tem acesso | POAN-119 | | |
| TM-12 | — | Clone limpo + `docker compose up --build` | 4 contêineres saudáveis; `/actuator/health` e `/health` respondem UP; front em http://localhost:3000 | Execução | | |

### 5.3 A criar

| ID | Nível | Caso | Motivo | Ação do plano | Responsável sugerido |
|---|---|---|---|---|---|
| TI-01 | Integração | Registrar atividade elegível credita AI Credits, atualiza saldo e nível de autonomia; débito sem saldo é recusado | Hipótese de inovação sem teste de ponta a ponta | 11 | Gustavo |
| TI-02 | Integração | Dashboard do colaborador agrega indicadores, metas, pontos, créditos e nível | História sem teste de integração | 11 | Gustavo |
| TI-03 | Integração | Indicador: valor registrado aparece na evolução filtrada por período | POAN-112 só tem testes unitários | 11 | Gustavo |
| TF-01 | Front-end | `api.js` envia `X-Colaborador-Id` e trata erros 400/403/404/409 | Front sem nenhum teste | 10 | Ana |
| TF-02 | Front-end | Menu mostra só as telas permitidas pelas permissões da sessão (`sessao.js`, `app.js`) | Regra de acesso duplicada no front | 10 | Ana |
| TF-03 | Front-end | Componentes de `ui.js` (tabela, formulário, modal) renderizam e validam campos obrigatórios | Base das telas | 10 | Ana |
| TC-01 | CI | Pipeline do `alert-service` (`npm test`) e do front (`npm test` + imagem) | Só o backend tem CI | 7 | Gustavo |

## 6. Riscos

| Risco | Probabilidade | Impacto | Mitigação |
|---|---|---|---|
| Testes de integração pulados sem ninguém perceber (sem Docker no ambiente) | Alta | Alto | No CI, falhar o build se `DamaApiIntegrationTest` for pulado; registrar a contagem de pulados nas métricas |
| Regras de alerta divergirem entre Java e Node | Média | Médio | Mesmos casos nos dois lados (`AnaliseGestaoRegrasTest` e `regras.test.js`), revisão obrigatória quando um dos dois mudar |
| Front quebrar sem aviso | Alta | Médio | TF-01 a TF-03 e o roteiro manual a cada sprint |
| Dependência de uma pessoa para executar e registrar testes | Alta | Médio | Distribuir os casos manuais entre os quatro integrantes |
| Cobertura real abaixo da meta de 70% | Desconhecida | Médio | Medir na próxima execução com Maven (ação 9) |

## 7. Cronograma

| Quando | O quê |
|---|---|
| Sprint 3 (até 23/10) | Executar TM-01 a TM-12; rodar `mvn verify` com Docker e registrar cobertura; preencher a seção 8 |
| Sprint 4 | TI-01 a TI-03; TF-01 a TF-03; TC-01 |
| Cada sprint | Reexecutar a suíte, atualizar `metricas.md` e registrar a execução na seção 8 |

## 8. Execuções registradas

| Data | O quê | Resultado | Evidência |
|---|---|---|---|
| 29/09/2026 | `mvn test` no backend (máquina da equipe) | 31 testes em 4 classes, 0 falhas, 9 de integração pulados por falta de Docker | [evidencias/backend-surefire-2026-09-29/](evidencias/backend-surefire-2026-09-29/) |
| 20/10/2026 | `node --test` no `alert-service` | 19 de 19 passando | [evidencias/alert-service-testes-2026-10-20.txt](evidencias/alert-service-testes-2026-10-20.txt) |
| 20/10/2026 | `mvn test` no backend | Não executado: o ambiente usado na coleta não tinha acesso ao Maven Central | — |
| pendente | Suíte completa do backend com Docker + JaCoCo; casos manuais TM-01 a TM-12 | | |

A suíte atual do backend tem 69 testes em 16 classes. Doze delas foram criadas depois da última execução registrada (`AlertaGestaoNodeTest` em 02/10 e as outras onze em 05/10). Por isso a próxima execução é a primeira da suíte inteira.

## 9. Métricas do teste

As métricas são acompanhadas em [metricas.md](metricas.md):
- testes por nível;
- taxa de aprovação;
- testes pulados;
- cobertura de linhas e de ramos (JaCoCo);
- defeitos encontrados por severidade;
- casos manuais executados sobre o total.

## Apêndice: inventário dos casos automatizados

| ID | Nível | Classe / arquivo | Caso | História |
|---|---|---|---|---|
| TB-01 | Integração (API + PostgreSQL) | `DamaApiIntegrationTest` | `visaoConsolidadaSomaPontosSemMultiplicarPorDepartamentosEMetas` | POAN-118 |
| TB-02 | Integração (API + PostgreSQL) | `DamaApiIntegrationTest` | `conquistaAutomaticaEhConcedidaAoAtingirOCriterioENivelSobe` | POAN-120 |
| TB-03 | Integração (API + PostgreSQL) | `DamaApiIntegrationTest` | `conquistaCriadaDepoisVaiParaQuemJaCumpreOCriterio` | POAN-120 |
| TB-04 | Integração (API + PostgreSQL) | `DamaApiIntegrationTest` | `conquistaManualEhConcedidaUmaVezSo` | POAN-120 |
| TB-05 | Integração (API + PostgreSQL) | `DamaApiIntegrationTest` | `metaDeIndicadorMenorEhMelhorConcluiAoFicarAbaixoDoAlvo` | POAN-114 |
| TB-06 | Integração (API + PostgreSQL) | `DamaApiIntegrationTest` | `alertasApontamMetaVencidaEIndicadorEmQueda` | POAN-121 |
| TB-07 | Integração (API + PostgreSQL) | `DamaApiIntegrationTest` | `dashboardDaEquipeConsolidaMembros` | POAN-121 |
| TB-08 | Integração (API + PostgreSQL) | `DamaApiIntegrationTest` | `perfilColaboradorVeRankingMasNaoVeAlertasNemAuditoria` | POAN-111/119 |
| TB-09 | Integração (API + PostgreSQL) | `DamaApiIntegrationTest` | `requisicaoSemCabecalhoNaoEhAutenticada` | POAN-111 |
| TB-10 | Unitário | `AiCreditServiceTest` | `atividadeElegivelGeraUmCreditoFixoPorRegraSemMultiplicarQuantidade` | POAN-117 |
| TB-11 | Unitário | `AiCreditServiceTest` | `atividadeSemRegraContinuaRegistradaEQuantidadeAusenteValeUm` | POAN-117 |
| TB-12 | Unitário | `AiCreditServiceTest` | `registroRecusaColaboradorInativoOuAtividadeDeOutraEmpresaOuInativa` | POAN-117 |
| TB-13 | Unitário | `AiCreditServiceTest` | `debitoExigeSaldoMasCreditoRequerColaboradorAtivoEPossuiEvento` | POAN-117 |
| TB-14 | Unitário | `AiCreditServiceTest` | `regraSaldoElegibilidadeENiveisRespeitamEmpresaEValoresDoRepositorio` | POAN-117 |
| TB-15 | Contrato (backend → alert-service) | `AlertaGestaoNodeTest` | `enviaOsDadosNoContratoEUsaOsAlertasDevolvidosPeloNode` | POAN-121 |
| TB-16 | Contrato (backend → alert-service) | `AlertaGestaoNodeTest` | `quandoOServicoRespondeErroUsaAsRegrasLocais` | POAN-121 |
| TB-17 | Contrato (backend → alert-service) | `AlertaGestaoNodeTest` | `respostaForaDoContratoUsaAsRegrasLocais` | POAN-121 |
| TB-18 | Contrato (backend → alert-service) | `AlertaGestaoNodeTest` | `servicoForaDoArUsaAsRegrasLocais` | POAN-121 |
| TB-19 | Contrato (backend → alert-service) | `AlertaGestaoNodeTest` | `motorLocalAplicaAsRegrasNaMesmaOrdemDoContrato` | POAN-121 |
| TB-20 | Unitário | `AnaliseGestaoRegrasTest` | `metaComPrazoEncerradoEhVencidaComSeveridadeAlta` | POAN-121 |
| TB-21 | Unitário | `AnaliseGestaoRegrasTest` | `metaAtrasadaEmRelacaoAoTempoDecorridoFicaEmRisco` | POAN-121 |
| TB-22 | Unitário | `AnaliseGestaoRegrasTest` | `metaNoRitmoEsperadoNaoGeraAlerta` | POAN-121 |
| TB-23 | Unitário | `AnaliseGestaoRegrasTest` | `metaDeIndicadorMenorEhMelhorNaoEntraNaRegraDeRisco` | POAN-121 |
| TB-24 | Unitário | `AnaliseGestaoRegrasTest` | `quedaDeVintePorCentoEhMediaEDeTrintaEhAlta` | POAN-121 |
| TB-25 | Unitário | `AnaliseGestaoRegrasTest` | `variacaoPequenaOuMelhoraNaoGeraAlerta` | POAN-121 |
| TB-26 | Unitário | `AnaliseGestaoRegrasTest` | `indicadorMenorEhMelhorPioraQuandoSobe` | POAN-121 |
| TB-27 | Unitário | `AnaliseGestaoRegrasTest` | `equipeComMenosDaMetadeDaMediaDaEmpresaGeraAlerta` | POAN-121 |
| TB-28 | Unitário | `AnaliseGestaoRegrasTest` | `semPontosNaEmpresaNaoHaComparacaoEntreEquipes` | POAN-121 |
| TB-29 | Unitário | `AnalyticsServiceTest` | `consultasExigemEmpresaERetornamZeroQuandoNaoHaVisaoConsolidada` | POAN-118 |
| TB-30 | Unitário | `AnalyticsServiceTest` | `analiseAgrupaSomenteValoresDaMesmaSerieEOrdenaPorSeveridade` | POAN-118 |
| TB-31 | Unitário | `AuditoriaServiceTest` | `registraJsonVazioQuandoDetalhesSaoNulosEMantemJsonInformado` | POAN-119 |
| TB-32 | Unitário | `ColaboradorServiceTest` | `listaEBuscaExigemExistencia` | POAN-110 |
| TB-33 | Unitário | `ColaboradorServiceTest` | `criarValidaEmpresaDepartamentoEEmailAntesDeAuditar` | POAN-110 |
| TB-34 | Unitário | `ColaboradorServiceTest` | `atualizarEStatusValidamEDepoisRetornamEstadoAtual` | POAN-110 |
| TB-35 | Unitário | `ConquistaServiceTest` | `nivelAtualEProgressoAteOProximo` | POAN-120 |
| TB-36 | Unitário | `ConquistaServiceTest` | `noNivelMaisAltoNaoHaProximoNivel` | POAN-120 |
| TB-37 | Unitário | `ConquistaServiceTest` | `criterioAutomaticoExigeValorMinimo` | POAN-120 |
| TB-38 | Unitário | `ConquistaServiceTest` | `conquistaManualNaoAceitaValorMinimo` | POAN-120 |
| TB-39 | Unitário | `ConquistaServiceTest` | `criterioDesconhecidoEhRecusado` | POAN-120 |
| TB-40 | Unitário | `ConquistaServiceTest` | `conquistaAutomaticaCriadaJaEhAvaliadaParaAEmpresa` | POAN-120 |
| TB-41 | Unitário | `ConquistaServiceTest` | `conquistaAutomaticaNaoPodeSerConcedidaManualmente` | POAN-120 |
| TB-42 | Unitário | `ConquistaServiceTest` | `conquistaManualRepetidaEhConflito` | POAN-120 |
| TB-43 | Unitário | `ConquistaServiceTest` | `colaboradorInativoNaoEhAvaliado` | POAN-120 |
| TB-44 | Unitário | `DashboardServiceTest` | `dashboardDoColaboradorAgregaIndicadoresMetasPontosCreditosENivel` | POAN-113 |
| TB-45 | Unitário | `DashboardServiceTest` | `dashboardDaEquipeUsaDataDoClockEConsolidaDadosDaEquipe` | POAN-121 |
| TB-46 | Unitário | `DesafioServiceTest` | `validaEmpresaStatusEPeriodoAoListarOuCriar` | POAN-116 |
| TB-47 | Unitário | `DesafioServiceTest` | `inscricaoExigeStatusAbertoMesmoEmpresaColaboradorAtivoENaoDuplicidade` | POAN-116 |
| TB-48 | Unitário | `DesafioServiceTest` | `concluirCreditaRecompensaPositivaENaoCreditaZeroEImpedeParticipacaoFinalizada` | POAN-116 |
| TB-49 | Unitário | `DesafioServiceTest` | `alteraStatusEDesistenciaRetornamEstadoAtualizado` | POAN-116 |
| TB-50 | Unitário | `IndicadorServiceTest` | `criarAplicaMaiorMelhorComoPadraoEExigeEmpresa` | POAN-112 |
| TB-51 | Unitário | `IndicadorServiceTest` | `registrarValorExigeExatamenteUmDonoEQueElePertençaAEmpresa` | POAN-112 |
| TB-52 | Unitário | `IndicadorServiceTest` | `registrarValorAceitaColaboradorOuDepartamentoEHistoricoValidaPeriodo` | POAN-112 |
| TB-53 | Unitário | `IndicadorServiceTest` | `consultasDelegamEIndicadorAusenteDa404` | POAN-112 |
| TB-54 | Unitário | `MetaServiceTest` | `metaSemIndicadorConcluiQuandoAtingeOuSuperaOAlvo` | POAN-114 |
| TB-55 | Unitário | `MetaServiceTest` | `metaDeIndicadorMenorEhMelhorConcluiQuandoFicaAbaixoDoAlvo` | POAN-114 |
| TB-56 | Unitário | `MetaServiceTest` | `conclusaoAutomaticaPublicaEventoParaConquistas` | POAN-114 |
| TB-57 | Unitário | `MetaServiceTest` | `progressoEmMetaEncerradaEhRecusado` | POAN-114 |
| TB-58 | Unitário | `OrganizacaoServiceTest` | `criaListaEConsultaEmpresasEDepartamentos` | POAN-110 |
| TB-59 | Unitário | `OrganizacaoServiceTest` | `criaDepartamentoTrataDuplicidadeEEmpresaInexistente` | POAN-110 |
| TB-60 | Unitário | `OrganizacaoServiceTest` | `delegaConsultasDePertencimentoEBuscaDepartamento` | POAN-110 |
| TB-61 | Unitário | `PerfilServiceTest` | `criarPerfilRejeitaPermissaoDesconhecidaECriaComPermissoesResolvidas` | POAN-111 |
| TB-62 | Unitário | `PerfilServiceTest` | `atribuirExigePerfilExistenteEOsDemaisMetodosDelegam` | POAN-111 |
| TB-63 | Unitário | `PontuacaoServiceTest` | `criarRegraExigeEmpresaEAtividadeDaMesmaEmpresa` | POAN-115 |
| TB-64 | Unitário | `PontuacaoServiceTest` | `regraPodeSerAtivadaOuDesativadaEInexistenteGera404` | POAN-115 |
| TB-65 | Unitário | `PontuacaoServiceTest` | `lancamentoExigeColaboradorAtivoRegraAtivaEDaMesmaEmpresa` | POAN-115 |
| TB-66 | Unitário | `PontuacaoServiceTest` | `lancamentoUsaDescricaoDaRegraQuandoAusenteEPublIcaEvento` | POAN-115 |
| TB-67 | Unitário | `PontuacaoServiceTest` | `resumoECreditoAutomaticoUsamDadosDoRepositorio` | POAN-115 |
| TB-68 | Unitário | `ReconhecimentoServiceTest` | `listarExigeEmpresaEMantemConsultaDoColaborador` | POAN-116 |
| TB-69 | Unitário | `ReconhecimentoServiceTest` | `criarExigeEmpresaColaboradorDoMesmoTenantEAtivoEDisparaEvento` | POAN-116 |
| TA-01 | Unitário (regras) | `regras.test.js` | Meta com prazo encerrado é vencida com severidade alta | POAN-121 |
| TA-02 | Unitário (regras) | `regras.test.js` | Meta atrasada em relação ao tempo decorrido fica em risco | POAN-121 |
| TA-03 | Unitário (regras) | `regras.test.js` | Meta no ritmo esperado não gera alerta | POAN-121 |
| TA-04 | Unitário (regras) | `regras.test.js` | Meta de indicador menor-é-melhor não entra na regra de risco | POAN-121 |
| TA-05 | Unitário (regras) | `regras.test.js` | Queda de 20% é média e de 30% é alta | POAN-121 |
| TA-06 | Unitário (regras) | `regras.test.js` | Variação pequena ou melhora não gera alerta | POAN-121 |
| TA-07 | Unitário (regras) | `regras.test.js` | Indicador menor-é-melhor piora quando sobe | POAN-121 |
| TA-08 | Unitário (regras) | `regras.test.js` | Limites exatos de 10% e 25% de piora contam como alerta | POAN-121 |
| TA-09 | Unitário (regras) | `regras.test.js` | Equipe com menos da metade da média da empresa gera alerta | POAN-121 |
| TA-10 | Unitário (regras) | `regras.test.js` | Sem pontos na empresa não há comparação entre equipes | POAN-121 |
| TA-11 | Unitário (regras) | `regras.test.js` | Colaborador sem atividade gera alerta de severidade baixa com a data de corte | POAN-121 |
| TA-12 | Unitário (regras) | `regras.test.js` | Avaliar mantém a ordem metas, tendências, equipes e sem atividade | POAN-121 |
| TA-13 | API HTTP | `server.test.js` | GET /health responde UP | POAN-121 |
| TA-14 | API HTTP | `server.test.js` | POST /avaliar devolve os alertas calculados | POAN-121 |
| TA-15 | API HTTP | `server.test.js` | Listas ausentes são tratadas como vazias | POAN-121 |
| TA-16 | API HTTP | `server.test.js` | Entrada sem dataReferencia é rejeitada com 400 | POAN-121 |
| TA-17 | API HTTP | `server.test.js` | Meta sem campo obrigatório é rejeitada com 400 e aponta o campo | POAN-121 |
| TA-18 | API HTTP | `server.test.js` | JSON inválido é rejeitado com 400 | POAN-121 |
| TA-19 | API HTTP | `server.test.js` | Rota inexistente devolve 404 e GET em /avaliar devolve 405 | POAN-121 |
