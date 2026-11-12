# Hipótese de inovação — situação e incorporação ao MVP

## A hipótese

Fonte: `docs/produto/DAMA-Intelligence-Documento-Completo.docx` e [docs/produto/README.md](../../docs/produto/README.md).

Dar **AI Credits** pelo uso produtivo de IA, e usar o saldo para liberar **níveis de autonomia** de acesso às ferramentas de IA da empresa, faz o colaborador usar IA de forma mais produtiva e segura do que um acesso igual para todos.

## Onde ela está no MVP

| Parte da hipótese | Situação |
|---|---|
| Atividades produtivas e AI Credits | Implementado |
| Elegibilidade, saldo e histórico | Implementado |
| Níveis de autonomia ligados ao saldo | Implementado como cálculo e exibição; **ainda não restringe o acesso às ferramentas** ([autonomia.md](../../docs/produto/autonomia.md)) |
| Conquistas, níveis de evolução e ranking | Implementado; o ranking passou a considerar retrabalho (qualidade), não só volume |
| AI Maturity Score por departamento (adoção, capacitação, governança, produtividade, eficiência) | Implementado ([ams.md](../../docs/produto/ams.md)); falta o quadrante AMS × investimento |
| Eficiência no uso de tokens | Implementado como dimensão do AMS e como penalidade no ranking |
| Alerta de orçamento de IA em risco por centro de custo | Implementado (usa a previsão de Análise Preditiva quando carregada) |
| Integração automática com o Jira (tarefas, story points, prioridade e tokens) | **Não implementado**; o caminho está em [ranking.md](../../docs/requisitos/ranking.md#integração-com-o-jira-fora-do-escopo-próximos-passos) |
| Login real e isolamento entre empresas | Implementado ([ADR-0010](../../docs/adr/0010-autenticacao-jwt.md), [ADR-0011](../../docs/adr/0011-isolamento-por-empresa.md)) |

## Evidências de apoio das outras disciplinas

Resultados dos dados sintéticos do projeto, que dão sentido ao desenho, mas **não validam a hipótese com usuários**:
- **Estatística Multivariada:** treinamento sozinho não move o uso nem reduz incidentes; os departamentos com uso alto e pouca capacitação são os de maior risco de governança. Isso apoia ligar o acesso a um critério além do curso ([README](../02_Estatistica_Multivariada/README.md)).
- **Pesquisa Operacional:** exigir capacitação para licenças além da básica reduz o valor da alocação em 30%, o que mostra o peso do critério de acesso ([README](../07_Pesquisa_Operacional_e_Calculo_Aplicado/README.md)).

## Situação

**Parcial.** A parte de AI Credits e autonomia está no MVP, mas ainda não foi testada com usuários, então não há como dizer se a hipótese foi confirmada, ajustada ou refutada. O que decide isso é o roteiro em [validacao.md](validacao.md). **Resultado: a confirmar pela equipe após as sessões.**
