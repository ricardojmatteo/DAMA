# ADR-0012: Módulos de leitura podem consultar tabelas de outros módulos

- **Status:** Aceita (complementa a [ADR-0002](0002-monolito-modular.md))
- **Data:** 2026-11-12

## Contexto

A ADR-0002 diz que módulos conversam só por Facade ou evento. Na prática, `analytics`, `dashboard`, `conquista` e o novo `maturidade` fazem consultas SQL que juntam tabelas de vários módulos (metas, pontuação, tarefas, AI Credits). A revisão de arquitetura apontou que a regra escrita não era a regra seguida.

Fazer essas agregações só por Facade significaria carregar listas inteiras em memória e juntar em Java (N+1 consultas), o que a ADR-0005 já rejeitou ao escolher JDBC e views SQL.

## Decisão

Reconhecer dois tipos de módulo:

- **Módulos de escrita** (`colaborador`, `meta`, `indicador`, `pontuacao`, `aicredit`, `desafio`, `reconhecimento`, `orcamento`, `auth`): donos das suas tabelas. Só eles escrevem nelas, e outros módulos só os chamam pela Facade.
- **Módulos de leitura** (`analytics`, `dashboard`, `maturidade` e a avaliação de critérios em `conquista`): podem **ler** tabelas de outros módulos em SQL, nunca escrever.

Quando um módulo de escrita muda uma coluna lida por um módulo de leitura, a mudança vale como quebra de contrato: os testes de integração e esta lista precisam ser revistos.

## Alternativas consideradas

- **Views SQL publicadas por cada módulo dono** (ex.: `vw_meta_publica`). É o próximo passo: o dono decide o que expõe, e os módulos de leitura passam a depender só das views.
- **Réplica de leitura alimentada por eventos (CQRS).** Desacopla de verdade, mas exige fila e reprocessamento, desproporcional para o tamanho do sistema.

## Consequências

- A regra escrita agora corresponde ao código, e a dependência fica visível.
- Ainda não há verificação automática. Um teste ArchUnit que proíba `insert/update/delete` fora do módulo dono e imports de `internal` entre módulos é a evolução indicada.
