# Sprint 3 — Review e Retrospectiva

**Período:** 13/10 a 23/10/2026

## Review

**Números do Jira:** 113 issues na sprint, todas concluídas. Ricardo Matteo e Ana Luiza Proença conduziram a maior parte das entregas.

**O que foi entregue:** o Deliverable 2 em todas as disciplinas.

| Frente | Entrega | Issues |
|---|---|---|
| Backend e banco | Schema PostgreSQL para os RFs, backend Spring Boot modular com autorização por perfil e os módulos de negócio (AI Credits, metas, pontuação, conquistas, dashboards, analytics e auditoria) | SMAN-171 a 185 |
| Interface | SPA com 13 páginas e BFF Node/Express, responsividade em celular, tablet e desktop | SMAN-180, 198, 290 a 295 |
| Integração e CI | alert-service Node com fallback no backend, Docker Compose e pipelines Jenkins dos três serviços | SMAN-283 a 287, 296 |
| Qualidade | CMMI, auditorias, métricas, plano de testes e plano de melhoria (5W2H); suíte do backend com 82 testes e cobertura de 89,7% das linhas e 80,4% das ramificações no build de 23/10 | SMAN-204 a 207, 253 a 263 |
| Dados e IA | Machine Learning, Análise Preditiva, Pesquisa Operacional e Estatística Multivariada do D2 | SMAN-214 a 252 |
| Marketing | Posicionamento, proposta de valor, análise competitiva, Business Model Canvas e Pitch Deck | SMAN-264 a 268 |
| Correções do D1 | ML, Análise Preditiva, Estatística e Pesquisa Operacional, com o relatório de mudanças | SMAN-269 a 278, 326 a 328 |
| Gestão | Ranking por tarefas entregues, reorganização das pastas do D2 e roteiro de validação | SMAN-186, 299 a 306 |

**Feedback recebido na review (Deliverable 2):**
- trocar a autenticação por cabeçalho por um login real;
- isolar os dados entre empresas-cliente;
- dividir o módulo de AI Credits, que concentrava responsabilidades demais;
- injetar o `HttpClient` do cliente de alertas para facilitar os testes.

## Retrospectiva

| O que foi bem | O que pode melhorar | O que vamos mudar |
|---|---|---|
| O Deliverable 2 saiu em todas as disciplinas, com o MVP integrado rodando | **Pontualidade:** a sprint terminou em 23/10, mas só foi encerrada no Jira em 05/11 | Encerrar a sprint no Jira no dia da review |
| Cobertura de 89,7% das linhas e 80,4% das ramificações, acima da meta de 70%, e CI no Jenkins para backend, alert-service e front-end | **Organização:** o quadro nem sempre foi atualizado no mesmo dia do trabalho, e parte das tasks foi detalhada só no fim da sprint | Criar a task quando o trabalho começa e mover o card no mesmo dia |
| O Jira ajudou bastante: o fluxo em seis passos deu visibilidade do andamento, e as tasks por disciplina facilitaram acompanhar cada entrega do Deliverable 2 | | |

**Ações com responsável e prazo:**

| Ação | Responsável | Prazo | Situação |
|---|---|---|---|
| Login por e-mail e senha com JWT e isolamento por empresa | Ricardo Matteo | 12/11/2026 | Feito (SMAN-360 e 344; ADR-0010 e 0011) |
| Dividir o módulo de AI Credits e injetar o `HttpClient` dos alertas | Ricardo Matteo | 12/11/2026 | Feito (SMAN-361) |
| Manter o quadro atualizado diariamente | Gustavo Gomes | Sprint 4 | Feito |
| Executar as sessões de validação com usuários | Ricardo Matteo | Sprint 4 | Feito (registro em anexo no SMAN-187) |
