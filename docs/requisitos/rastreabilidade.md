# Rastreabilidade: necessidade → RF → CA → história e card → Sprint → evidência

Situação em 10/11/2026. Liga cada requisito do [catálogo](catalogo.md) à necessidade da [visão](../produto/visao.md#3-personas-e-necessidades), à prioridade da [priorização](priorizacao-mvp.md), aos critérios de aceite, à história no Jira-PO (POAN), ao card no Jira-SM (SMAN), à sprint e à evidência.

## Como ler

- **Necessidade:** N01 a N08 da visão.
- **Prioridade:** M = Must, S = Should, C = Could, W = Won't (agora).
- **Sprint** (projeto SMAN): S2 = 24/09 a 30/09 (fechada em 13/10); S3 = 13/10 a 23/10 (fechada em 05/11); S4 = 05/11 a 13/11 (ativa). O asterisco (S2\*) indica que **o card não tem sprint no Jira** e a sprint foi deduzida pela data do commit no `main`. Os cards SMAN-175 a 185 foram registrados no Jira depois da implementação, que entrou em 28/09 (commits `8b7ed86` e `922eb33`).
- **Evidência:** testes em `backend/src/test` (classe.método), em `alert-service/test` ou casos do plano de testes. O inventário completo está em `Deliverable2/04_Qualidade_de_Software/evidencias/inventario-testes.csv`. A execução completa mais recente é de 23/10/2026 (`mvn -B clean verify`: 82 testes, 0 falhas, gate de 70% do JaCoCo atendido).
- **Estimativas e capacidade:** nenhum card tem story points registrados, por isso não há coluna de estimativa (ação 15 do plano de melhoria).

## 1. Matriz

| RF | Nec. | Prior. | CA | História | Card | Sprint | Evidência | Situação |
|---|---|---|---|---|---|---|---|---|
| RF01 | N06 | M | CA-RF01.1–3 | POAN-110 | SMAN-175 | S2\* | `ColaboradorServiceTest.criarValidaEmpresaDepartamentoEEmailAntesDeAuditar`; `DamaApiIntegrationTest` (cadastro em todos os cenários) | Implementado |
| RF02 | N06 | S | CA-RF02.1–3 | POAN-110 | SMAN-175 | S2\* | `ColaboradorServiceTest.listaEBuscaExigemExistencia`, `.atualizarEStatusValidamEDepoisRetornamEstadoAtual` | Implementado |
| RF03 | N01 | M | CA-RF03.1–2 | POAN-112 | SMAN-176 | S2\* | `IndicadorServiceTest` (4 testes) | Implementado |
| RF04 | N01 | M | CA-RF04.1–3 | POAN-114 | SMAN-176 | S2\* | Restrição de responsável no banco (`database/01_schema.sql`). **Sem teste automatizado dos 3 CAs** | Implementado sem teste dedicado |
| RF05 | N03 | M | CA-RF05.1–3 | POAN-113 | SMAN-179; testes: SMAN-182 | S2\*; S4 | `DashboardServiceTest.dashboardDoColaboradorAgregaIndicadoresMetasPontosCreditosENivel` (unitário). Integração TI-02 pendente (SMAN-182) | Implementado |
| RF06 | N01 | S | CA-RF06.1–2 | POAN-112 | SMAN-176 | S2\* | `IndicadorServiceTest.registrarValorAceitaColaboradorOuDepartamentoEHistoricoValidaPeriodo` | Implementado |
| RF07 | N03 | M | CA-RF07.1–3 | POAN-115 | SMAN-177 | S2\* | `PontuacaoServiceTest.lancamentoExigeColaboradorAtivoRegraAtivaEDaMesmaEmpresa`, `.lancamentoUsaDescricaoDaRegraQuandoAusenteEPublIcaEvento`; `DamaApiIntegrationTest.soAdministradorLancaPontosGestorRecebe403` | Implementado |
| RF08 | N03 | M | CA-RF08.1–3 | POAN-115, POAN-107 | SMAN-177; tela: SMAN-89, SMAN-323 | S2\*; S3 | `DamaApiIntegrationTest.perfilColaboradorVeRankingMasNaoVeAlertasNemAuditoria`; `RankingRegrasTest.notaVaiDeZeroADezRelativaAMaiorPontuacaoDaEmpresa`, `.semNenhumaEntregaTodasAsNotasSaoZero` | Implementado |
| RF09 | N03 | C | CA-RF09.1–3 | POAN-116 | SMAN-177 | S2\* | `DesafioServiceTest` (4 testes) | Implementado |
| RF10 | N04 | C | CA-RF10.1–2 | POAN-116 | SMAN-177 | S2\* | `ReconhecimentoServiceTest` (2 testes); `DamaApiIntegrationTest.conquistaCriadaDepoisVaiParaQuemJaCumpreOCriterio` | Implementado |
| RF11 | N03 | S | CA-RF11.1 | POAN-115 | SMAN-177 | S2\* | `PontuacaoServiceTest.resumoECreditoAutomaticoUsamDadosDoRepositorio` (resumo). Ordem do histórico sem teste | Implementado sem teste dedicado |
| RF12 | N04 | M | CA-RF12.1–3 | POAN-117, POAN-106 | SMAN-185, SMAN-88; testes: SMAN-182 | S2\*; S4 | `AiCreditServiceTest.atividadeElegivelGeraUmCreditoFixoPorRegraSemMultiplicarQuantidade`, `.atividadeSemRegraContinuaRegistradaEQuantidadeAusenteValeUm`. Integração TI-01 pendente (SMAN-182) | Implementado |
| RF13 | N05 | M | CA-RF13.1–2 | POAN-118 | SMAN-179 | S2\* | `DamaApiIntegrationTest.visaoConsolidadaSomaPontosSemMultiplicarPorDepartamentosEMetas`; `AnalyticsServiceTest.consultasExigemEmpresaERetornamZeroQuandoNaoHaVisaoConsolidada` | Implementado |
| RF14 | N05 | S | CA-RF14.1–3 | POAN-118 | SMAN-179, SMAN-299 | S2\*; S3\* | `RankingRegrasTest.equipePequenaComMelhorMediaSuperaEquipeGrande` | Implementado |
| RF15 | N02 | S | CA-RF15.1–4 | POAN-121 | SMAN-179 | S2\* | `AnaliseGestaoRegrasTest` (9), `AlertaGestaoNodeTest` (5), `alert-service/test` (19), `AnalyticsServiceTest.analiseAgrupaSomenteValoresDaMesmaSerieEOrdenaPorSeveridade`, `DamaApiIntegrationTest.alertasApontamMetaVencidaEIndicadorEmQueda` | Implementado |
| RF16 | N06 | M | CA-RF16.1–2 | POAN-110 | SMAN-175 | S2\* | `OrganizacaoServiceTest` (3 testes) | Implementado |
| RF17 | N06 | M | CA-RF17.1 | POAN-110 | SMAN-175 | S2\* | `ColaboradorServiceTest.criarValidaEmpresaDepartamentoEEmailAntesDeAuditar`; `OrganizacaoServiceTest.delegaConsultasDePertencimentoEBuscaDepartamento` | Implementado |
| RF18 | N06, N08 | M | CA-RF18.1–4 | POAN-111 | SMAN-174, SMAN-175 | S3; S2\* | `PerfilServiceTest` (2); `DamaApiIntegrationTest.requisicaoSemCabecalhoNaoEhAutenticada`, `.perfilColaboradorVeRankingMasNaoVeAlertasNemAuditoria` | Implementado |
| RF19 | N04, N06 | M | CA-RF19.1–2 | POAN-117 | SMAN-185 | S2\* | **Sem teste automatizado** | Implementado sem teste dedicado |
| RF20 | N04 | M | CA-RF20.1–2 | POAN-117 | SMAN-185 | S2\* | `AiCreditServiceTest.atividadeSemRegraContinuaRegistradaEQuantidadeAusenteValeUm`, `.registroRecusaColaboradorInativoOuAtividadeDeOutraEmpresaOuInativa` | Implementado |
| RF21 | N06 | M | CA-RF21.1–3 | POAN-117 | SMAN-185 | S2\* | `AiCreditServiceTest.regraSaldoElegibilidadeENiveisRespeitamEmpresaEValoresDoRepositorio` | Implementado |
| RF22 | N04 | M | CA-RF22.1–3 | POAN-117 | SMAN-185 | S2\* | `AiCreditServiceTest.debitoExigeSaldoMasCreditoRequerColaboradorAtivoEPossuiEvento` | Implementado |
| RF23 | N04 | M | CA-RF23.1 | POAN-117 | SMAN-185 | S2\* | `AiCreditServiceTest.regraSaldoElegibilidadeENiveisRespeitamEmpresaEValoresDoRepositorio` | Implementado |
| RF24 | N04 | M | CA-RF24.1 | POAN-117 | SMAN-185 | S2\* | **Sem teste automatizado** | Implementado sem teste dedicado |
| RF25 | N06 | M | CA-RF25.1 | POAN-117, POAN-106 | SMAN-185, SMAN-88 | S2\*; S2 | `AiCreditServiceTest.regraSaldoElegibilidadeENiveisRespeitamEmpresaEValoresDoRepositorio`; níveis em `database/03_seed.sql` | Implementado |
| RF26 | N04 | M | CA-RF26.1–2 | POAN-117 | SMAN-185, SMAN-291 | S2\* | `AiCreditServiceTest.regraSaldoElegibilidadeENiveisRespeitamEmpresaEValoresDoRepositorio` | Implementado |
| RF27 | N03 | M | CA-RF27.1–3 | POAN-115 | SMAN-177 | S2\* | `PontuacaoServiceTest.resumoECreditoAutomaticoUsamDadosDoRepositorio` | Implementado |
| RF28 | N03 | M | CA-RF28.1–2 | POAN-115 | SMAN-299 | S3\* | `AnalyticsServiceTest.janelaPadraoSaoOsUltimos30DiasTerminandoHojeEPeriodoInvertidoEhRecusado`; `DamaApiIntegrationTest.rankingDescontaUsoExcessivoDeIaLimitaOBonusEFiltraPelaJanela` | Implementado |
| RF29 | N01, N03 | M | CA-RF29.1–2 | POAN-114 | SMAN-176 | S2\* | **Sem teste automatizado** | Implementado sem teste dedicado |
| RF30 | N01 | M | CA-RF30.1–3 | POAN-114 | SMAN-176 | S2\* | `MetaServiceTest` (4 testes); `DamaApiIntegrationTest.metaDeIndicadorMenorEhMelhorConcluiAoFicarAbaixoDoAlvo` | Implementado |
| RF31 | N03 | S | CA-RF31.1–4 | POAN-120 | SMAN-178 | S2\* | `ConquistaServiceTest` (9); `DamaApiIntegrationTest.conquistaAutomaticaEhConcedidaAoAtingirOCriterioENivelSobe`, `.conquistaCriadaDepoisVaiParaQuemJaCumpreOCriterio`, `.conquistaManualEhConcedidaUmaVezSo` | Implementado |
| RF32 | N01 | M | CA-RF32.1–4 | POAN-121 | SMAN-179 | S2\* | `DashboardServiceTest.dashboardDaEquipeUsaDataDoClockEConsolidaDadosDaEquipe`; `DamaApiIntegrationTest.dashboardDaEquipeConsolidaMembros` | Implementado |
| RF33 | N01 | S | CA-RF33.1 | POAN-118, POAN-121 | SMAN-179 | S2\* | `DamaApiIntegrationTest.dashboardDaEquipeConsolidaMembros` (indireto); view em `database/02_views.sql` | Implementado |
| RF34 | N01 | S | CA-RF34.1–2 | POAN-112 | SMAN-176 | S2\* | `IndicadorServiceTest.registrarValorAceitaColaboradorOuDepartamentoEHistoricoValidaPeriodo`; `AnalyticsServiceTest.janelaPadrao...PeriodoInvertidoEhRecusado` | Implementado |
| RF35 | N02 | S | CA-RF35.1–2 | POAN-112 | SMAN-176 | S2\* | `DamaApiIntegrationTest.alertasApontamMetaVencidaEIndicadorEmQueda`; `AnalyticsServiceTest.analiseAgrupaSomenteValoresDaMesmaSerieEOrdenaPorSeveridade` | Implementado |
| RF36 | N05 | S | CA-RF36.1 | POAN-118 | SMAN-179 | S2\* | **Sem teste automatizado** | Implementado sem teste dedicado |
| RF37 | N03, N07 | M | CA-RF37.1–4 | POAN-115 | SMAN-301, SMAN-299, SMAN-323; tela de registro: SMAN-342 | S3\*; S4 | `RankingRegrasTest` (7 testes); `PontuacaoServiceTest.tarefaEntregueAssumeUrgenciaMediaETokensZeroQuandoOmitidosEAvisaOGestor`, `.tarefaRepetidaGeraConflitoEColaboradorInativoEhRecusado`, `.bonusDoGestorEhRegistradoParaColaboradorAtivo`; regra em [ranking.md](ranking.md) | Implementado |
| RF38 | N06 | M | CA-RF38.1–2 | POAN-119 | SMAN-179 | S2\* | `AuditoriaServiceTest` (1); `ColaboradorServiceTest.criarValidaEmpresaDepartamentoEEmailAntesDeAuditar`; `DamaApiIntegrationTest.perfilColaboradorVeRankingMasNaoVeAlertasNemAuditoria` | Implementado |
| RF39 | N08 | W | CA-RF39.1–3 | POAN-122 | — (isolamento: SMAN-344) | POAN Sprint 3; S4 | — | Não implementado |
| RF40 | N07 | M | CA-RF40.1–4 | POAN-6 (épico) | SMAN-43 (épico), SMAN-334, SMAN-335, SMAN-336 | S4 | `MaturidadeRegras` (cálculo puro); sem teste dedicado ainda | Implementado sem teste dedicado |
| RF41 | N05, N07 | M | CA-RF41.1–3 | POAN-6 (épico) | SMAN-340 | S4 | — | Parcial (falta o quadrante AMS × investimento) |
| RF42 | N04, N07 | S | CA-RF42.1–2 | POAN-105 | SMAN-343 | S4 | — | Não implementado |
| RF43 | N07 | C | CA-RF43.1 | POAN-10 (épico) | SMAN-337 | S4 | Modelo em `Deliverable2/05_Machine_Learning` | Não implementado |
| RF44 | N05 | C | CA-RF44.1 | — | SMAN-338, SMAN-339 | S4 | Modelo em `Deliverable2/07_Pesquisa_Operacional_e_Calculo_Aplicado` | Não implementado |
| RF45 | N05 | C | CA-RF45.1 | — | SMAN-341 | S4 | Modelo em `Deliverable2/08_Analise_Preditiva` | Não implementado |

### Requisitos não funcionais com evidência

| RNF | Card | Evidência | Situação |
|---|---|---|---|
| RNF01 Segurança | SMAN-174 | Testes de autorização do `DamaApiIntegrationTest` (RF18) | Parcial (depende do RF39) |
| RNF02 Usabilidade | SMAN-187 | Roteiro em `docs/validacao/validacao-parcial.md`, sem sessões | Não verificado |
| RNF04 Disponibilidade | SMAN-192 | `PrintDocker.png` (23/10), Anexo A da Qualidade | Atendido na demo |
| RNF05 Manutenibilidade | SMAN-183, SMAN-359 | Execução de 23/10 com gate de 70% do JaCoCo atendido | Atendido |
| RNF06 Integridade | SMAN-171, SMAN-172 | Views e restrições em `database/`; CA-RF05.2 e CA-RF23.1 | Atendido |
| RNF07 Auditoria | SMAN-179 | RF38 | Atendido em 8 módulos |
| RNF09 Privacidade | SMAN-344 | — | Não atendido |
| RNF10 Compatibilidade | SMAN-198, SMAN-358 | `docs/evidencias/responsividade` | Parcial |

RNF03 (desempenho) e RNF08 (escalabilidade) não têm card nem medição.

## 2. Lacunas da matriz

Situação dos 70 critérios de aceite dos 28 RFs Must:

| Lacuna | CAs afetados | Onde será tratada |
|---|---|---|
| RFs Must sem nenhum teste automatizado: RF04, RF19, RF24 e RF29 | 8 | SMAN-182 (Sprint 4) |
| RFs Must ainda incompletos: RF41 (quadrante AMS × investimento) | 3 | SMAN-340 (Sprint 4) |
| RF05 e RF12 com teste só unitário (falta a integração TI-01 e TI-02) | 6 | SMAN-182 (Sprint 4) |
| Casos manuais TM-01 a TM-12 não executados (cobririam os CAs de tela, como CA-RF26.2 e CA-RF32.3) | — | SMAN-356 (Sprint 4) |

Os demais CAs dos RFs Must têm pelo menos um teste automatizado na classe indicada. O mapeamento fino, de cada CA para cada método de teste, é a entrega do SMAN-182.

**Sprint sem registro no Jira:** dos 38 RFs implementados, 34 só têm a sprint de implementação deduzida pela data do commit (S2\* ou S3\*). As exceções são RF08, RF12, RF18 e RF25, que têm card com sprint no Jira. Para a próxima entrega, os cards precisam ser movidos na sprint em que o trabalho acontece (ação 2 do plano de melhoria).

## 3. Histórias, módulos, endpoints e telas

| História | RFs | Módulo / endpoints | Tela |
|---|---|---|---|
| POAN-110 Cadastro e gestão de colaboradores | RF01, RF02, RF16, RF17 | `colaborador`, `organizacao`: `/empresas/{id}/colaboradores`, `/colaboradores/{id}` | Pessoas e perfis |
| POAN-111 Perfis e permissões | RF18 | `perfil`, `shared.security`, `sessao`: `/perfis`, `/me` | Pessoas e perfis, login |
| POAN-112 Indicadores, evolução e filtro por período | RF03, RF06, RF34, RF35 | `indicador`: `/indicadores/{id}/valores?inicio&fim` | Indicadores |
| POAN-113 Dashboard do colaborador | RF05 | `dashboard`: `/colaboradores/{id}/dashboard` | Meu painel |
| POAN-114 Metas e progresso | RF04, RF29, RF30 | `meta`: `/metas/{id}/progresso` | Metas |
| POAN-115 Pontuação, histórico e ranking | RF07, RF08, RF11, RF27, RF28, RF37 | `pontuacao`, `analytics`: `/colaboradores/{id}/pontuacao`, `/analytics/ranking-colaboradores` | Ranking e pontuação |
| POAN-116 Desafios e reconhecimentos | RF09, RF10 | `desafio`, `reconhecimento` | Desafios, Reconhecimentos |
| POAN-117 AI Credits | RF12, RF19–RF26 | `aicredit`: atividades, regras, saldo, histórico, níveis | AI Credits |
| POAN-118 Visão consolidada e comparação entre equipes | RF13, RF14, RF33, RF36 | `analytics`: `/visao-consolidada`, `/ranking-departamentos`, `/indicadores-departamentos` | Análises, Equipes |
| POAN-119 Auditoria | RF38 | `auditoria`: `/empresas/{id}/auditoria` | Auditoria |
| POAN-120 Conquistas e níveis de evolução | RF31 | `conquista`: `/conquistas`, `/evolucao`, `/niveis-evolucao` | Evolução e conquistas |
| POAN-121 Análises de gestão e dashboard por equipe | RF15, RF32, RF33 | `analytics`: `/analytics/alertas`; `dashboard`: `/departamentos/{id}/dashboard` | Análises de gestão, Equipes |
| POAN-122 Autenticação real e isolamento entre empresas | RF39, RNF09 | não implementado | — |
| POAN-123 Testes e rastreabilidade | todos | `backend/src/test`, este documento | — |

## Como rodar os testes

```bash
cd backend
mvn -B clean verify   # unitários + integração (Testcontainers) + regra de 70% do JaCoCo
```

`DamaApiIntegrationTest` sobe um PostgreSQL 16 com Testcontainers e aplica os scripts de `database/`. Sem Docker, ele é pulado e os testes unitários continuam rodando.
