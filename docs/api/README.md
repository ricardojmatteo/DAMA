# API REST

Base: `http://localhost:8080/api` (ou `http://localhost:3000/api` pelo BFF do front).
Toda requisição, exceto o login, precisa de `Authorization: Bearer <token>` ([ADR-0010](../adr/0010-autenticacao-jwt.md)); a permissão exigida está na coluna **Permissão**.
Além da permissão, o recurso precisa ser da empresa do usuário; só o Administrador (`PLATAFORMA_ADMINISTRAR`) enxerga todas as empresas ([ADR-0011](../adr/0011-isolamento-por-empresa.md)).
Erros voltam como `400` (regra violada), `401` (sem token válido ou login recusado), `403` (perfil sem permissão ou recurso de outra empresa), `404` (não encontrado) ou `409` (conflito), sempre com `{ "status", "message" }`.

## Perfis padrão

| Permissão | Administrador | Gestor | Colaborador |
|---|:-:|:-:|:-:|
| `COLABORADOR_LER` / `COLABORADOR_ESCREVER` | ✓ | ✓ | |
| `PERFIL_GERENCIAR` | ✓ | | |
| `DESEMPENHO_LER` | ✓ | ✓ | ✓ |
| `DESEMPENHO_ESCREVER` | ✓ | ✓ | |
| `GAMIFICACAO_LER` | ✓ | ✓ | ✓ |
| `GAMIFICACAO_ESCREVER` | ✓ | ✓ | |
| `PONTOS_LANCAR` | ✓ | | |
| `AI_CREDITS_LER` | ✓ | ✓ | ✓ |
| `AI_CREDITS_GERENCIAR` | ✓ | ✓ | |
| `ANALYTICS_LER` | ✓ | ✓ | |
| `PLATAFORMA_ADMINISTRAR` | ✓ | | |

## Autenticação e sessão

| Método | Caminho | Permissão | O que faz |
|---|---|---|---|
| POST | `/auth/login` | pública | `{ "email", "senha" }` → `{ "token", "tipo": "Bearer", "expiraEm", "colaboradorId" }`; 401 se inválido ou desligado |
| POST | `/auth/senha` | autenticado | Troca a própria senha: `{ "senhaAtual", "novaSenha" }` (8 a 72 caracteres) |
| PUT | `/colaboradores/{id}/senha` | COLABORADOR_ESCREVER | Define ou redefine a senha de um colaborador da empresa: `{ "senha" }` |
| GET | `/me` | autenticado | Colaborador logado, perfis, permissões e `administraPlataforma` |

## Organização e pessoas

| Método | Caminho | Permissão | O que faz |
|---|---|---|---|
| GET | `/empresas` | COLABORADOR_LER | Lista empresas (só a própria, exceto para o Administrador) |
| POST | `/empresas` | PLATAFORMA_ADMINISTRAR | Cria empresa-cliente |
| GET | `/empresas/{empresaId}/departamentos` | COLABORADOR_LER | Lista equipes |
| POST | `/empresas/{empresaId}/departamentos` | PERFIL_GERENCIAR | Cria equipe |
| GET | `/empresas/{empresaId}/colaboradores` | COLABORADOR_LER | Lista colaboradores |
| POST | `/empresas/{empresaId}/colaboradores` | COLABORADOR_ESCREVER | Cadastra colaborador (e-mail único na plataforma; `senha` opcional) |
| GET | `/colaboradores/{id}` | COLABORADOR_LER | Consulta colaborador |
| PUT | `/colaboradores/{id}` | COLABORADOR_ESCREVER | Edita dados |
| PATCH | `/colaboradores/{id}/status` | COLABORADOR_ESCREVER | Desliga ou reativa (`{ "ativo": false }`) |
| GET | `/perfis` · `/permissoes` | PERFIL_GERENCIAR | Lista perfis e permissões |
| POST | `/perfis` | PERFIL_GERENCIAR | Cria perfil com uma lista de códigos de permissão |
| GET / POST | `/colaboradores/{id}/perfis` | PERFIL_GERENCIAR | Consulta ou atribui perfil |
| DELETE | `/colaboradores/{id}/perfis/{perfilId}` | PERFIL_GERENCIAR | Remove perfil |

## Desempenho

| Método | Caminho | Permissão | O que faz |
|---|---|---|---|
| GET | `/empresas/{empresaId}/indicadores` | DESEMPENHO_LER | Lista indicadores |
| POST | `/empresas/{empresaId}/indicadores` | DESEMPENHO_ESCREVER | Cria indicador (`maiorMelhor: false` para "menor é melhor") |
| POST | `/indicadores/{id}/valores` | DESEMPENHO_ESCREVER | Registra valor mensal para um colaborador **ou** uma equipe (o autor é sempre o usuário autenticado) |
| GET | `/indicadores/{id}/valores?colaboradorId&departamentoId&inicio&fim` | DESEMPENHO_LER | Evolução filtrada por série e período |
| GET | `/empresas/{empresaId}/metas?colaboradorId&departamentoId&status` | DESEMPENHO_LER | Lista metas com progresso atual |
| POST | `/empresas/{empresaId}/metas` | DESEMPENHO_ESCREVER | Cria meta para colaborador ou equipe |
| GET | `/metas/{id}` | DESEMPENHO_LER | Consulta meta |
| PATCH | `/metas/{id}/status` | DESEMPENHO_ESCREVER | Altera status |
| POST / GET | `/metas/{id}/progresso` | ESCREVER / LER | Registra progresso (conclui ao atingir o alvo) / histórico |
| GET | `/colaboradores/{id}/dashboard` | DESEMPENHO_LER | Dashboard individual |
| GET | `/departamentos/{id}/dashboard` | DESEMPENHO_LER | Dashboard da equipe com membros |

## Gamificação

| Método | Caminho | Permissão | O que faz |
|---|---|---|---|
| GET / POST | `/empresas/{empresaId}/regras-pontuacao` | GAMIFICACAO_LER / PONTOS_LANCAR | Regras de pontuação (criar só o Administrador) |
| PATCH | `/regras-pontuacao/{id}/ativa` | PONTOS_LANCAR | Ativa ou desativa regra |
| POST | `/colaboradores/{id}/pontuacao` | PONTOS_LANCAR | Lança pontos a partir de uma regra ativa |
| GET | `/colaboradores/{id}/pontuacao` | GAMIFICACAO_LER | Total e histórico de pontos |
| GET / POST | `/empresas/{empresaId}/desafios` | LER / ESCREVER | Desafios |
| GET | `/desafios/{id}` | GAMIFICACAO_LER | Consulta desafio |
| PATCH | `/desafios/{id}/status` | GAMIFICACAO_ESCREVER | Planejado, Em andamento, Encerrado, Cancelado |
| GET / POST | `/desafios/{id}/participantes` | LER / ESCREVER | Participantes / inscrição |
| POST | `/desafios/{id}/participantes/{colaboradorId}/concluir` | GAMIFICACAO_ESCREVER | Conclui e credita a recompensa |
| POST | `/desafios/{id}/participantes/{colaboradorId}/desistir` | GAMIFICACAO_ESCREVER | Registra desistência |
| GET / POST | `/empresas/{empresaId}/reconhecimentos` | LER / ESCREVER | Reconhecimentos da empresa / novo reconhecimento |
| GET | `/colaboradores/{id}/reconhecimentos` | GAMIFICACAO_LER | Reconhecimentos recebidos |
| GET | `/conquistas/criterios` | GAMIFICACAO_LER | Critérios disponíveis |
| GET / POST | `/empresas/{empresaId}/conquistas` | LER / ESCREVER | Catálogo / nova conquista (já concede a quem cumpre) |
| POST | `/empresas/{empresaId}/conquistas/reavaliar` | GAMIFICACAO_ESCREVER | Reavalia todas as conquistas automáticas |
| PATCH | `/conquistas/{id}/ativa` | GAMIFICACAO_ESCREVER | Ativa ou desativa conquista |
| GET | `/colaboradores/{id}/conquistas` | GAMIFICACAO_LER | Conquistas obtidas |
| POST | `/colaboradores/{id}/conquistas` | GAMIFICACAO_ESCREVER | Concede conquista MANUAL |
| GET | `/colaboradores/{id}/evolucao` | GAMIFICACAO_LER | Nível atual, próximo nível e conquistas |
| GET / POST | `/niveis-evolucao` | LER / ESCREVER | Níveis de evolução |

## AI Credits

| Método | Caminho | Permissão | O que faz |
|---|---|---|---|
| GET / POST | `/empresas/{empresaId}/atividades-produtivas` | LER / GERENCIAR | Catálogo de atividades |
| GET / POST | `/empresas/{empresaId}/ai-credits/regras` | LER / GERENCIAR | Regras que tornam uma atividade elegível |
| GET | `/atividades-produtivas/{id}/elegibilidade` | AI_CREDITS_LER | Se a atividade gera créditos e por quais regras |
| POST | `/colaboradores/{id}/atividades` | AI_CREDITS_GERENCIAR | Registra atividade; credita se elegível |
| GET | `/colaboradores/{id}/atividades` | AI_CREDITS_LER | Atividades registradas |
| POST | `/colaboradores/{id}/ai-credits/transacoes` | AI_CREDITS_GERENCIAR | Crédito manual ou débito (recusado sem saldo) |
| GET | `/colaboradores/{id}/ai-credits/saldo` | AI_CREDITS_LER | Saldo e nível de autonomia |
| GET | `/colaboradores/{id}/ai-credits/historico` | AI_CREDITS_LER | Movimentações |
| POST / GET | `/colaboradores/{id}/tarefas` | DESEMPENHO_ESCREVER / GAMIFICACAO_LER | Tarefas entregues (Jira): `{ "chave", "storyPoints", "urgencia", "tokensIa", "retrabalho" }`; base do ranking (retrabalho vale metade) |
| POST / GET | `/colaboradores/{id}/bonus` | DESEMPENHO_ESCREVER / GAMIFICACAO_LER | Bônus do gestor (`{ "pontos", "motivo" }`), até 15% dos pontos líquidos no ranking |
| GET / POST | `/ai-credits/niveis` | LER / GERENCIAR | Níveis de autonomia |

## Analytics e auditoria

| Método | Caminho | Permissão | O que faz |
|---|---|---|---|
| GET | `/empresas/{empresaId}/analytics/visao-consolidada` | ANALYTICS_LER | Totais da empresa |
| GET | `/empresas/{empresaId}/analytics/ranking-colaboradores?inicio&fim` | ANALYTICS_LER ou GAMIFICACAO_LER | Ranking do período (padrão: 30 dias): tarefas entregues × urgência − uso de IA + bônus do gestor, nota de 0 a 10. Regras em [ranking.md](../requisitos/ranking.md) |
| GET | `/empresas/{empresaId}/analytics/ranking-departamentos?inicio&fim` | ANALYTICS_LER | Comparação entre equipes do período, por pontuação por pessoa (nota de 0 a 10) |
| GET | `/empresas/{empresaId}/analytics/indicadores-departamentos?indicadorId` | ANALYTICS_LER | Último valor de cada indicador por equipe |
| GET | `/empresas/{empresaId}/analytics/alertas` | ANALYTICS_LER | Análises de gestão (onde agir); as regras rodam no `alert-service` ([ADR-0009](../adr/0009-alert-service-node-para-alertas-de-gestao.md)) ou, se ele estiver fora do ar, no próprio backend |
| GET / PUT | `/empresas/{empresaId}/orcamentos-ia?mes` | ANALYTICS_LER / DESEMPENHO_ESCREVER | Orçamento de IA por departamento no mês, com gasto, previsão e faixa de 80%; o PUT cria ou atualiza (um por departamento e mês) |
| GET | `/empresas/{empresaId}/ams` | ANALYTICS_LER | AI Maturity Score da empresa e por departamento, com as cinco dimensões ([ams.md](../produto/ams.md)) |
| GET / POST | `/colaboradores/{id}/capacitacoes` | DESEMPENHO_LER / DESEMPENHO_ESCREVER | Capacitações em IA concluídas (`{ "nome", "cargaHoraria", "concluidaEm" }`) |
| GET | `/empresas/{empresaId}/auditoria?entidade&entidadeId` | PERFIL_GERENCIAR | Eventos de auditoria |

Saúde da aplicação (sem autenticação): `GET /actuator/health`.

## Exemplos

```bash
# entrar (usuários de demonstração usam a senha dama123) e guardar o token
TOKEN=$(curl -s -X POST -H "Content-Type: application/json" \
     -d '{"email":"lucas.pereira@demo.dama","senha":"dama123"}' \
     http://localhost:8080/api/auth/login | sed -E 's/.*"token":"([^"]+)".*/\1/')

# quem sou eu
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/me

# sem token: 401; com token de outra empresa: 403
curl -i http://localhost:8080/api/me

# AI Maturity Score (perfil com ANALYTICS_LER, ex.: mariana.souza@demo.dama)
curl -H "Authorization: Bearer $TOKEN_GESTOR" http://localhost:8080/api/empresas/1/ams
```
