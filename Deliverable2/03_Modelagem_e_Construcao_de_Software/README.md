# Modelagem e Construção de Software — Deliverable 2

**DAMA Intelligence** · Grupo 1 · Ana Luiza Proença, Gustavo Gomes, Kaique Lira e Ricardo Matteo · Ciência de Dados e Negócios, ESPM, 4º semestre, 2026

O código e a documentação técnica ficam no repositório: https://github.com/ricardojmatteo/DAMA

| Entrega | Onde está | Situação |
|---|---|---|
| Arquitetura e padrões adotados | ADRs 0001 a 0012 em [`docs/adr`](https://github.com/ricardojmatteo/DAMA/tree/main/docs/adr) | Pronto |
| Modelo C4 e justificativa das decisões | Contexto, contêineres e componentes do backend em [`docs/arquitetura`](https://github.com/ricardojmatteo/DAMA/tree/main/docs/arquitetura) | Pronto |
| Implementação da arquitetura e dos serviços | [`backend/`](https://github.com/ricardojmatteo/DAMA/tree/main/backend), [`alert-service/`](https://github.com/ricardojmatteo/DAMA/tree/main/alert-service), [`frontend/`](https://github.com/ricardojmatteo/DAMA/tree/main/frontend) e [`database/`](https://github.com/ricardojmatteo/DAMA/tree/main/database); sobe com `docker compose up --build` | Pronto |
| Evidências de TDD | Testes em `backend/src/test` e `alert-service/test`; prints da execução e da cobertura JaCoCo em [DAMA Intelligence evidências de testes e cobertura JaCoCo.docx](DAMA%20Intelligence%20evidências%20de%20testes%20e%20cobertura%20JaCoCo.docx); análise do histórico em [`docs/qualidade/tdd.md`](https://github.com/ricardojmatteo/DAMA/blob/main/docs/qualidade/tdd.md) | Parcial. Os testes existem, mas o histórico não mostra o teste escrito antes do código |
| Curadoria do código com IA (code review) | Segurança, SRP/ISP em AI Credits e injeção do cliente HTTP em [`docs/qualidade/code-review-ia.md`](../../docs/qualidade/code-review-ia.md); autenticação JWT e isolamento por empresa nas ADRs 0010 e 0011 | Atividades 1 a 3 no código; testes novos e ciclo TDD (atividade 4) pendentes |
| Pipeline de Integração Contínua | Jenkinsfiles do [`backend`](https://github.com/ricardojmatteo/DAMA/blob/main/backend/Jenkinsfile), do [`alert-service`](https://github.com/ricardojmatteo/DAMA/blob/main/alert-service/Jenkinsfile) e do [`frontend`](https://github.com/ricardojmatteo/DAMA/blob/main/frontend/Jenkinsfile); configuração em [`docs/qualidade/jenkins.md`](https://github.com/ricardojmatteo/DAMA/blob/main/docs/qualidade/jenkins.md); prints dos pipelines do alert-service e do frontend no documento de evidências desta pasta | Pronto para alert-service e frontend. O backend tem Jenkinsfile, mas falta o print do pipeline rodando |
| Inovação do projeto por sprint | Hipótese e situação atual em [`09_Projeto_Integrador_IV/hipotese-de-inovacao.md`](../09_Projeto_Integrador_IV/hipotese-de-inovacao.md) | A fazer. Falta o plano por sprint |
| Visão revisada, requisitos e rastreabilidade | Visão com AI Maturity Score e hipótese mensurável em [`docs/produto/visao.md`](../../docs/produto/visao.md); catálogo de RF, RNF e regras com critérios de aceite em [`docs/requisitos/catalogo.md`](../../docs/requisitos/catalogo.md); priorização MoSCoW e recorte do MVP em [`docs/requisitos/priorizacao-mvp.md`](../../docs/requisitos/priorizacao-mvp.md); matriz necessidade → RF → CA → card → Sprint → evidência em [`docs/requisitos/rastreabilidade.md`](../../docs/requisitos/rastreabilidade.md) | Pronto. RF40 e RF41 (AI Maturity Score na plataforma) na Sprint 4 |
| Repositório GitHub | https://github.com/ricardojmatteo/DAMA | — |

No arquivo .zip da entrega, esta pasta leva também uma cópia de `docs/arquitetura`, `docs/adr` e dos Jenkinsfiles, para leitura sem abrir o GitHub.
