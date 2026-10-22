# Ranking: como a pontuação é calculada

Vale para o ranking de colaboradores (RF08/RF28) e para a comparação entre equipes (RF14/RF36). Tudo é calculado dentro de uma empresa, nunca entre empresas-cliente. O código está em `RankingRegras` (módulo `analytics`) e os testes em `RankingRegrasTest`.

## A conta

```
pontos da tarefa = story points × multiplicador da urgência
penalidade de IA = pontos da tarefa × fração penalizada
pontuação        = max(0, Σ (pontos da tarefa − penalidade de IA) + bônus do gestor)
nota (0 a 10)    = 10 × pontuação ÷ maior pontuação da empresa na janela
```

A soma é feita sobre as tarefas **entregues** pelo colaborador dentro da janela de tempo. O ranking é ordenado pela **pontuação**; a **nota** só deixa o número mais legível.

### 1. Pontos da tarefa

A tarefa vem do Jira com seus **story points**, então tarefas maiores valem mais. A urgência, definida pelo SM ao criar a tarefa, multiplica os pontos:

| Urgência | Multiplicador |
|---|---|
| BAIXA | 1,00 |
| MEDIA (padrão) | 1,25 |
| ALTA | 1,50 |
| CRITICA | 2,00 |

Exemplo: 5 story points com urgência ALTA valem 5 × 1,5 = 7,5 pontos.

### 2. Penalidade de IA (a subtração)

Cada tarefa tem um **limite de tokens** proporcional ao seu tamanho:

```
limite da tarefa = story points × 20.000 tokens
uso              = tokens gastos ÷ limite
fração penalizada = clamp((uso − 25%) ÷ (100% − 25%), 0, 1)
```

- Os **primeiros 25% do limite são grátis**: nenhum desconto.
- Depois disso o desconto cresce de forma linear até 100% dos pontos da tarefa, que é atingido quando o uso chega ao limite.
- Acima do limite o desconto continua em 100% (a tarefa não fica negativa).
- Como o limite depende dos story points, tarefa grande pode gastar mais tokens sem ser punida.

A penalidade é expressa **em pontos** (pontos da tarefa × fração), então já pode ser subtraída.

Exemplo para uma tarefa de 5 story points e 7,5 pontos (limite de 100.000 tokens):

| Tokens gastos | Uso | Fração penalizada | Penalidade | Pontos líquidos |
|---|---|---|---|---|
| até 25.000 | até 25% | 0 | 0 | 7,50 |
| 62.500 | 62,5% | 0,50 | 3,75 | 3,75 |
| 100.000 ou mais | 100% ou mais | 1,00 | 7,50 | 0 |

### 3. Bônus do gestor

O gestor ou coordenador concede pontos extras com um motivo (`POST /colaboradores/{id}/bonus`). No cálculo o bônus tem **teto de 15% dos pontos líquidos das tarefas** do colaborador na janela:

```
bônus aplicado = min(bônus concedido, 15% × pontos líquidos)
```

Quem não entregou nada não recebe bônus, porque 15% de zero é zero.

### 4. Piso e escala

- A pontuação nunca fica abaixo de **0**.
- A **nota vai de 0 a 10**: a maior pontuação da empresa na janela vale 10 e as demais são proporcionais. Se ninguém pontuou, todas as notas são 0.
- Equipes seguem a mesma lógica, usando a pontuação por pessoa.

## Janela de tempo

| Parâmetro | Detalhe |
|---|---|
| `inicio`, `fim` | Datas `AAAA-MM-DD`, inclusivas, na query string dos dois endpoints de ranking |
| Padrão | Últimos **30 dias** terminando hoje (`dama.ranking.janela-padrao-dias`) |
| Validação | `inicio` depois de `fim` devolve 400 |
| Tela | Seletor com 7, 30, 90 dias, 1 ano e "Todo o período" (`?dias=` na URL) |

Tarefas entram pela data de entrega (`entregue_em`) e bônus pela data de concessão. A posição da equipe no dashboard da equipe e o alerta "equipe abaixo da média" usam a janela padrão, para ficar coerentes com a tela de ranking.

## Equipes

```
pontuação por pessoa = soma das pontuações dos colaboradores ativos da equipe ÷ colaboradores ativos
```

A posição vem da pontuação por pessoa, para equipes grandes não terem vantagem. Equipes sem colaborador ativo aparecem com zero.

## Exemplo completo

Parâmetros padrão. Na janela:

| Colaborador | Tarefas | Pontos | Tokens | Penalidade | Bônus pedido | Bônus aplicado | Pontuação | Nota |
|---|---|---|---|---|---|---|---|---|
| Ana | 5 SP, ALTA | 7,5 | 0 | 0 | — | 0 | **7,50** | 10,00 |
| Caio | 2 SP, BAIXA | 2,0 | 0 | 0 | 5 | 0,30 (15% de 2) | **2,30** | 3,07 |
| Bia | 5 SP, ALTA | 7,5 | 100.000 | 7,5 | — | 0 | **0,00** | 0,00 |
| Dani | nenhuma | 0 | — | 0 | 10 | 0 | **0,00** | 0,00 |

Bia e Dani empatam em 0 e dividem a 3ª posição.

## Posições e empates

- Ordem decrescente de pontuação; empates dividem a posição (a seguinte pula, como em `RANK()`).
- Só colaboradores **ativos** entram.

## Origem dos dados

| Dado | Tabela | Como entra |
|---|---|---|
| Tarefa entregue | `tarefa_entregue` | `POST /colaboradores/{id}/tarefas`, pelo gestor ou por uma integração com o Jira |
| Bônus do gestor | `bonus_gestor` | `POST /colaboradores/{id}/bonus` |

Os pontos lançados por regras de pontuação e por desafios **não entram no ranking**. Eles continuam alimentando níveis e conquistas.

Cada tarefa é registrada uma vez por colaborador (`chave` única); repetir a mesma chave devolve 409. O campo `tokensIa` (padrão 0) deve ser preenchido com os tokens gastos naquela tarefa.

## Configuração (`application.yml`)

```yaml
dama:
  ranking:
    tokens-por-ponto: 20000
    franquia-gratis: 0.25
    bonus-maximo: 0.15
    janela-padrao-dias: 30
```

Também podem ser definidas por variável de ambiente: `RANKING_TOKENS_POR_PONTO`, `RANKING_FRANQUIA_GRATIS`, `RANKING_BONUS_MAXIMO` e `RANKING_JANELA_DIAS`. Os valores valem para todas as empresas. Os multiplicadores de urgência ficam em `RankingRegras.multiplicadorUrgencia`.

## API

| Método | Caminho | Permissão |
|---|---|---|
| GET | `/empresas/{id}/analytics/ranking-colaboradores?inicio&fim` | ANALYTICS_LER ou GAMIFICACAO_LER |
| GET | `/empresas/{id}/analytics/ranking-departamentos?inicio&fim` | ANALYTICS_LER |
| POST | `/colaboradores/{id}/tarefas` `{ "chave", "descricao", "storyPoints", "urgencia", "tokensIa", "entregueEm" }` | DESEMPENHO_ESCREVER |
| GET | `/colaboradores/{id}/tarefas` | GAMIFICACAO_LER |
| POST | `/colaboradores/{id}/bonus` `{ "pontos", "motivo" }` | DESEMPENHO_ESCREVER |
| GET | `/colaboradores/{id}/bonus` | GAMIFICACAO_LER |

Campos do ranking de colaboradores: `tarefasEntregues`, `pontosTarefas`, `penalidadeIa`, `bonusGestor` (aplicado, já com o teto), `pontuacao`, `nota`, `posicao`. Do ranking de equipes: `colaboradoresAtivos`, `tarefasEntregues`, `pontuacaoTotal`, `pontuacaoPorPessoa`, `nota`, `posicao`.

## Limitações conhecidas

- **Os tokens são informados, não medidos.** Quem registra a tarefa informa os tokens gastos. Para medir de verdade, a ferramenta de IA ou o gateway precisa fornecer esse número.
- **A integração com o Jira não faz parte deste sistema.** Os dados chegam pela API; quem os envia (SM ou um script) é responsável pelos story points e pela urgência corretos.
- **O bônus do gestor é subjetivo.** O teto de 15% e o registro de quem concedeu e por quê (com auditoria) limitam o risco, mas não o eliminam.
- **A nota é relativa.** Ela muda se o melhor da empresa mudar; use a pontuação para comparar períodos diferentes.
- **Tokens e urgência são os mesmos para todas as empresas.** Em outra empresa, ajuste `tokens-por-ponto` conforme o tipo de trabalho.

## Integração com o Jira (fora do escopo; próximos passos)

O sistema **não** lê o Jira. Hoje as tarefas entram pela API (`POST /colaboradores/{id}/tarefas`) e o bônus por `POST /colaboradores/{id}/bonus`. Ligar o Jira a essas entradas é um trabalho futuro, de quem for implementar. O que precisa ser feito:

**No Jira**
1. Garantir que toda tarefa tenha **story points** (campo de estimativa) antes de ser concluída.
2. Usar o campo **Prioridade** como urgência: Highest/Mais alta → `CRITICA`, High/Alta → `ALTA`, Medium/Média → `MEDIA`, Low/Baixa e Lowest/Mais baixa → `BAIXA`. O SM é quem define a prioridade ao criar a tarefa.
3. Criar um campo numérico **"Tokens IA"**, preenchido por quem fez a tarefa, com os tokens gastos nela.
4. Manter o **e-mail** do responsável no Jira igual ao e-mail do colaborador cadastrado no DAMA. É por ele que se descobre de quem é a tarefa.

**Na ligação (a construir)**
5. Quando a tarefa for para "Concluído", enviar ao DAMA: chave (`DAMA-123`), descrição, e-mail do responsável, story points, prioridade convertida em urgência, tokens e data de conclusão.
   - Opções: uma regra do **Jira Automation** com "Send web request", ou um script/serviço que consulta a API do Jira.
   - O DAMA hoje só aceita chamadas autenticadas por colaborador (`X-Colaborador-Id`, ver [ADR-0006](../adr/0006-autenticacao-por-cabecalho.md)). Quem integrar precisará definir como autenticar o Jira (segredo compartilhado, token de serviço etc.).
   - O Jira precisa alcançar o DAMA por uma URL pública (em ambiente de teste, um túnel).
6. Decidir o que fazer com os casos de borda: tarefa sem story points, responsável sem cadastro no DAMA, tarefa reaberta depois de entregue e reenvio da mesma chave (hoje a API devolve 409).
7. Enquanto a ligação não existir, o SM ou um Administrador registra as tarefas e os bônus manualmente pela API.

Passo a passo do Jira Automation e detalhes de campos ficam a cargo de quem implementar, pois variam por instância (os ids de campos personalizados são diferentes em cada Jira).
