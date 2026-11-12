# AI Maturity Score (AMS) na plataforma

O AMS mede, de 0 a 100, o quanto um departamento usa IA de forma ampla, capacitada, controlada e produtiva. É calculado na leitura (`GET /api/empresas/{id}/ams`) a partir do que a plataforma já registra; nada é armazenado. Código: módulo `maturidade` (`MaturidadeRegras` tem o cálculo puro). Tela: **Gestão → Maturidade em IA**.

## Por que não é o IMIA da Estatística Multivariada

O índice IMIA ([visão, seção 4](visao.md#4-ai-maturity-score)) usa oito indicadores de uso (requisições, ferramentas, licenças ociosas, tokens por requisição) que vêm da base sintética, não de dados que a plataforma coleta. Para o AMS existir no produto agora, ele foi montado com o que o produto já tem: atividades registradas, tarefas do Jira com tokens e retrabalho, e capacitações. O IMIA continua como referência metodológica; comparar a ordenação das equipes nos dois índices é o critério CA-RF40.4, ainda pendente.

## As cinco dimensões

Todas vão de 0 a 100 e usam os colaboradores **ativos** do departamento. A janela padrão é de 90 dias.

| Dimensão | Fórmula | Fonte | O que responde |
|---|---|---|---|
| **Adoção** | % de colaboradores com ao menos uma atividade produtiva registrada ou uma tarefa com tokens > 0 na janela | `atividade_registro`, `tarefa_entregue` | Quantas pessoas de fato usam IA? |
| **Capacitação** | % de colaboradores com treinamento em IA concluído nos últimos 12 meses | `capacitacao` | A equipe foi preparada para usar? |
| **Governança** | % das tarefas entregues dentro do limite de tokens (story points × 20.000) **e** sem retrabalho | `tarefa_entregue` | O uso respeita as regras e gera entrega que não volta? |
| **Produtividade** | Story points entregues por colaborador, relativo ao melhor departamento da empresa (= 100) | `tarefa_entregue` | A equipe entrega? |
| **Eficiência** | 100 × (1 − fração média penalizada das tarefas), com a mesma fração do [ranking](../requisitos/ranking.md#2-penalidade-de-ia-a-subtração) | `tarefa_entregue` | Quanto do consumo de IA ficou dentro do razoável? |

```
AMS do departamento = 0,20 × adoção + 0,20 × capacitação + 0,20 × governança + 0,20 × produtividade + 0,20 × eficiência
AMS da empresa      = média dos departamentos ponderada pelos colaboradores ativos
```

Departamento sem colaborador ativo tem AMS 0. Departamento sem tarefas na janela fica com governança, eficiência e produtividade 0.

## Por que pesos iguais (20% cada)

- **Não há dado real para calibrar.** Pesos diferentes seriam uma opinião sem evidência; pesos iguais deixam claro que a calibração é trabalho futuro.
- **Cada dimensão responde a uma pergunta diferente do gestor**, e a ausência de qualquer uma derruba a maturidade: adoção sem capacitação é risco, capacitação sem adoção é desperdício, uso sem governança é custo e uso sem produtividade não paga a conta.
- **A Estatística Multivariada já usou peso igual** entre as dimensões Uso e Capacitação do IMIA, pelo mesmo motivo.
- **Os pesos são configuráveis** (`dama.ams.pesos.*` ou `AMS_PESO_*`), precisam somar 100 (a API não sobe caso contrário) e voltam na resposta da API, para a tela mostrar com que pesos o número foi calculado.

Proposta de calibração: depois das sessões de validação, pedir a três gestores que ordenem os departamentos por maturidade percebida e ajustar os pesos para maximizar a correlação de Spearman entre essa ordem e o AMS.

## Leitura

| Faixa | Leitura | Ação sugerida |
|---|---|---|
| 0 a 39 | Adoção baixa | Rever licenças ociosas e começar pela capacitação |
| 40 a 69 | Em desenvolvimento | Capacitar quem usa muito e ampliar o acesso aos capacitados |
| 70 a 100 | Avançado | Ampliar níveis de autonomia e usar a equipe como referência |

## Transparência

A resposta traz, para cada departamento, as contagens que geraram cada dimensão (`evidencias`: com uso de IA, capacitados, tarefas conformes, story points por pessoa, penalidade média). A tela mostra essas contagens numa tabela "De onde vêm os números", para o gestor conferir o resultado em vez de confiar num número opaco.

## Configuração

```yaml
dama:
  ams:
    pesos: { adocao: 20, capacitacao: 20, governanca: 20, produtividade: 20, eficiencia: 20 }
    janela-dias: 90
    capacitacao-validade-meses: 12
```

O limite de tokens e a franquia grátis vêm de `dama.ranking`, para o AMS e o ranking usarem a mesma régua.

## Limitações

- **Tokens são informados, não medidos** (a mesma limitação do ranking). A eficiência é tão boa quanto o registro.
- **Produtividade é relativa** ao melhor departamento da empresa: não compara empresas entre si.
- **Falta o investimento.** O quadrante AMS × investimento (RF41) depende do orçamento por centro de custo, que agora existe em `orcamento_ia`; cruzar os dois é o próximo passo.
