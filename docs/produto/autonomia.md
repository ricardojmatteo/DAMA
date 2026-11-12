# Nível de autonomia: o que é real e o que é só interface

Resposta ao item 2.5 da avaliação de Projeto Integrador ("autonomia real vs. na interface").

## Como está hoje

| Parte | Situação |
|---|---|
| Saldo de AI Credits | **Real.** Derivado das transações (`ai_credit_transacao`), com bloqueio contra débitos concorrentes |
| Nível de autonomia | **Real como cálculo.** O maior nível cujo mínimo o saldo atinge (`GET /colaboradores/{id}/ai-credits/saldo`); cai se o saldo cair |
| Exibição | **Real.** Tela de AI Credits e painel do colaborador mostram nível e próximo nível |
| Efeito sobre o acesso às ferramentas de IA | **Não existe.** Nenhum sistema consulta o nível para liberar ou restringir ferramenta, modelo ou cota |

Ou seja: a plataforma decide **quem merece** mais autonomia, mas ainda não **aplica** essa decisão. Hoje o nível é uma recomendação para o gestor.

## O que cada nível deveria liberar (proposta)

| Nível (seed) | Saldo mínimo | Política de acesso proposta |
|---|---|---|
| Básico | 0 | Ferramentas aprovadas só com dados públicos; cota diária baixa; sem envio de documentos internos |
| Ampliado | 100 | Documentos internos não sensíveis; cota maior; assistente de código no IDE |
| Avançado | 250 | Integrações e automações (agentes que chamam sistemas internos), com log |
| Total | 500 | Todas as ferramentas homologadas; pode publicar automações para a equipe, com revisão periódica do gestor |

Os nomes e mínimos de cada nível estão em `ai_nivel_autonomia`; a política acima é proposta do PO, a validar nas sessões com usuários.

## Como tornar real (próximo passo)

A plataforma não precisa controlar as ferramentas diretamente. Basta que o sistema que já controla o acesso consulte o nível:

1. **Gateway de IA** (ex.: um proxy corporativo na frente dos provedores): antes de cada chamada, consulta o nível do usuário e aplica cota e lista de modelos permitidos.
2. **Grupos no provedor de identidade** (ex.: Entra ID): o DAMA sincroniza um grupo por nível (`dama-autonomia-avancado`), e as ferramentas usam esses grupos nas próprias regras de acesso.

Para o MVP, a demonstração pode ser feita com o fluxo completo dentro do DAMA (item 2.6 da avaliação): registrar atividade elegível → crédito automático → saldo sobe → nível muda → painel mostra o novo nível; e o caso negativo, atividade sem regra ativa → registrada sem crédito → nível não muda.
