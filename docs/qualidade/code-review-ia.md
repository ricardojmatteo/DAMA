# Curadoria do código com IA (code review)

Resposta ao item 3 da avaliação de Modelagem e Construção de Software. Para cada atividade: o problema, o que a IA recomendou, o que a equipe decidiu (incluindo o que foi **rejeitado**), o código antes e depois e as evidências. A IA usada foi o Claude (Anthropic), em revisão sobre o repositório completo.

> **Situação das evidências de teste.** As atividades 1 a 3 já estão no código. Os testes existentes foram ajustados para as novas assinaturas, mas os testes novos de cada atividade (marcados como *pendente* abaixo) e a execução com o relatório do JaCoCo ficam a cargo da equipe, no ciclo da atividade 4.

---

## Atividade 1 — Segurança: autenticação pelo cliente

**Arquivos revisados:** `ColaboradorHeaderAuthenticationFilter.java`, `SecurityConfig.java`.

**Problema.** O filtro aceitava qualquer valor no cabeçalho `X-Colaborador-Id` e o usava como identidade. Mandar `X-Colaborador-Id: 1` dava acesso de Administrador. Além disso, nada impedia um usuário de uma empresa de ler recursos de outra trocando o id na URL, e colaborador desligado continuava com as permissões (só `/api/me` barrava).

**Recomendação da IA.**
1. Trocar o cabeçalho por credencial verificável: login com e-mail e senha (BCrypt) emitindo JWT assinado.
2. Carregar permissões do banco a cada requisição em vez de colocá-las no token, para que desligar alguém tenha efeito imediato.
3. Adicionar autorização contextual por empresa nos services, com exceção explícita para o operador da plataforma.
4. Ignorar campos de autoria enviados pelo cliente (`registradoPor`).

**Decisão.** Aceitas as quatro, registradas na [ADR-0010](../adr/0010-autenticacao-jwt.md) e na [ADR-0011](../adr/0011-isolamento-por-empresa.md).

**Recomendações rejeitadas ou adiadas (análise crítica).**
- *Row Level Security no PostgreSQL*: isolaria no banco, mas exige propagar a empresa em cada conexão e mexe em todas as consultas de analytics. Adiado como reforço.
- *Expressões SpEL no `@PreAuthorize`*: não resolve rotas como `/metas/{id}`, cuja empresa só se conhece depois de carregar a meta.
- *Refresh token e revogação*: desproporcional agora; como as permissões vêm do banco, desligar o colaborador já corta o acesso.

**Antes** (`ColaboradorHeaderAuthenticationFilter`):

```java
String header = request.getHeader("X-Colaborador-Id");
Long colaboradorId = Long.valueOf(header);                 // o cliente diz quem é
Set<String> permissoes = perfilFacade.permissoesDoColaborador(colaboradorId);
SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(colaboradorId, null, authorities));
```

**Depois** (`JwtAuthenticationFilter` + `EscopoEmpresa`):

```java
Long colaboradorId = jwt.validar(token);                    // assinatura e validade conferidas
ColaboradorView colaborador = colaboradores.buscarPorId(colaboradorId);
if (!colaborador.ativo()) return;                           // desligado não autentica
var principal = new UsuarioAtual.Principal(colaboradorId, colaborador.empresaId(), colaborador.nome());
SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(principal, null, authorities));

// em cada service, antes de ler ou alterar
escopo.exigir(meta.empresaId());                            // 403 se for de outra empresa
```

**Evidências.**
- `DamaApiIntegrationTest` passou a autenticar com token emitido pelo `JwtService`; `requisicaoSemTokenNaoEhAutenticada` cobre o 401.
- *Pendente:* testes de login inválido (401), colaborador desligado com token válido (401) e acesso cruzado entre empresas (403) em ao menos metas, AI Credits e auditoria.

---

## Atividade 2 — SOLID: `AiCreditService` e `AiCreditFacade` (SRP e ISP)

**Problema.** Um service de 205 linhas e uma facade com 13 operações cobriam três assuntos que mudam por motivos diferentes: o catálogo e registro de atividades, as regras de elegibilidade e a carteira (transações, saldo, níveis). O único consumidor externo, `PontuacaoService`, precisava de uma operação (`atividadePertenceAEmpresa`) e dependia das 13.

**Recomendação da IA.** Separar em três contratos orientados a caso de uso, cada um com seu service e repositório:

| Contrato | Responsabilidade | Muda quando… |
|---|---|---|
| `AtividadeFacade` | Catálogo de atividades (RF19) e registro (RF20) | Muda o que a empresa reconhece como atividade |
| `RegraCreditoFacade` | Regras de elegibilidade (RF21) | Muda a política de créditos |
| `AiCreditFacade` | Transações, saldo e níveis de autonomia (RF22–RF26) | Muda como créditos se movimentam ou viram autonomia |

O registro de atividade passa a orquestrar: grava o registro, pergunta as regras ativas a `RegraCreditoFacade` e pede o crédito a `AiCreditFacade.creditarPorRegra`.

**Decisão.** Aceita. As rotas da API não mudaram (três controllers no lugar de um), então o front não foi alterado. `PontuacaoService` passou a depender só de `AtividadeFacade`.

**Rejeitado (análise crítica).** A IA também sugeriu separar *consulta* de *comando* na carteira (`SaldoQuery` e `TransacaoCommand`). Rejeitado: saldo e débito compartilham o bloqueio por colaborador (`pg_advisory_xact_lock`) e a mesma regra de saldo suficiente; separá-los espalharia uma regra que precisa ficar junta.

**Antes:** `AiCreditFacade` com 13 métodos; `AiCreditService` com 205 linhas e 5 dependências.
**Depois:** três facades (5, 4 e 6 métodos), três services de 70 a 120 linhas, três repositórios por tabela.

**Evidências.** `AtividadeServiceTest` (3 casos, portados do teste antigo) e `AiCreditServiceTest` (4 casos: débito sem saldo, crédito por regra sem evento duplicado, saldo/níveis, regras e elegibilidade). *Pendente:* rodar e anexar o JaCoCo das três classes.

---

## Atividade 3 — Injeção de dependência: `HttpClient` no `AlertaGestaoNode`

**Problema.** O construtor criava o próprio `HttpClient`, então não dava para configurar o cliente fora da classe nem simular falhas sem subir um servidor.

**Recomendação da IA.** Receber o `HttpClient` pelo construtor (DIP) e deixar a configuração montá-lo; manter um construtor de conveniência para não quebrar quem já usa.

**Decisão.** Aceita. `AlertaGestaoConfig` monta o cliente com o timeout configurado e o injeta.

**Rejeitado (análise crítica).** A IA sugeriu trocar `java.net.http.HttpClient` por `RestClient` do Spring. Rejeitado: o comportamento de timeout e fallback já está testado com o cliente atual, e a troca não traria ganho para a avaliação, só risco.

**Antes:**

```java
AlertaGestaoNode(String baseUrl, Duration timeout, AlertaGestaoClient fallback) {
    ...
    this.http = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(timeout).build();
}
```

**Depois:**

```java
AlertaGestaoNode(String baseUrl, Duration timeout, AlertaGestaoClient fallback, HttpClient http) { ... this.http = http; }

// AlertaGestaoConfig
return new AlertaGestaoNode(url.trim(), timeout, local, AlertaGestaoNode.clientePadrao(timeout));
```

**Evidências.** `AlertaGestaoNodeTest` continua usando o construtor de conveniência. *Pendente:* teste de timeout passando um `HttpClient` com timeout curto contra um servidor que não responde, e uma suíte de contrato (LSP) que rode os mesmos casos contra `AlertaGestaoLocal` e `AlertaGestaoNode`.

---

## Atividade 4 — TDD (a fazer pela equipe)

A avaliação pede o ciclo RED–GREEN–REFACTOR em **commits separados**, com o resultado de cada execução e o JaCoCo. Como isso só é verificável no histórico do repositório, a escolha da regra e os commits ficam com a equipe. Sugestão de regra nova e pequena, que também reforça a atividade 1:

**Regra:** bloquear o login por 15 minutos depois de 5 senhas erradas seguidas para o mesmo e-mail.

1. **RED** — commit só com `AuthServiceTest.bloqueiaAposCincoTentativasErradas` (e o caso de desbloqueio após 15 minutos com `Clock` fixo). Rodar `mvn test`, salvar a saída com a falha.
2. **GREEN** — commit com a implementação mínima em `AuthService` (contador em memória por e-mail). Rodar e salvar a saída verde.
3. **REFACTOR** — commit movendo o contador para uma classe `TentativasDeLogin` com `Clock` injetado, sem mudar o teste. Rodar, salvar a saída e o relatório `target/site/jacoco/index.html`.

As três saídas e o relatório vão para `docs/evidencias/tdd/`.
