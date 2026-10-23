# Hipótese de inovação — situação e incorporação ao MVP

## A hipótese

Fonte: `docs/produto/DAMA-Intelligence-Documento-Completo.docx` e [docs/produto/README.md](../../docs/produto/README.md).

Dar **AI Credits** pelo uso produtivo de IA, e usar o saldo para liberar **níveis de autonomia** de acesso às ferramentas de IA da empresa, faz o colaborador usar IA de forma mais produtiva e segura do que um acesso igual para todos.

## Onde ela está no MVP

| Parte da hipótese | Situação |
|---|---|
| Atividades produtivas e AI Credits | Implementado |
| Elegibilidade, saldo e histórico | Implementado |
| Níveis de autonomia ligados ao saldo | Implementado |
| Conquistas, níveis de evolução e ranking | Implementado |
| AI Literacy Score (eficiência no uso de tokens) | **Não implementado** |
| Integração automática com o Jira (tarefas, story points, prioridade e tokens) | **Não implementado**; a regra de pontuação está descrita em [ranking.md](../../docs/requisitos/ranking.md) |
| Login real e isolamento entre empresas | **Não implementado** ([ADR-0006](../../docs/adr/0006-autenticacao-por-cabecalho.md)) |

## Evidências de apoio das outras disciplinas

Resultados dos dados sintéticos do projeto, que dão sentido ao desenho, mas **não validam a hipótese com usuários**:
- **Estatística Multivariada:** treinamento sozinho não move o uso nem reduz incidentes; o grupo "uso sem capacitação" é o de maior risco de governança. Isso apoia ligar o acesso a um critério além do curso ([README](../Estatistica%20Multivariada/README.md)).
- **Pesquisa Operacional:** exigir capacitação para licenças além da básica reduz o valor da alocação em 30%, o que mostra o peso do critério de acesso ([README](../Pesquisa%20Operacional/README.md)).

## Situação

**Parcial.** A parte de AI Credits e autonomia está no MVP, mas ainda não foi testada com usuários, então não há como dizer se a hipótese foi confirmada, ajustada ou refutada. O que decide isso é o roteiro em [validacao.md](validacao.md). **Resultado: a confirmar pela equipe após as sessões.**
