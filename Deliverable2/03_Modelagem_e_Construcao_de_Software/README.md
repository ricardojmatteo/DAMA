# Modelagem e Construção de Software — Deliverable 2

**DAMA Intelligence** · Grupo 1 · Ana Luiza Proença, Gustavo Gomes, Kaique Lira e Ricardo Matteo · Ciência de Dados e Negócios, ESPM, 4º semestre, 2026

O código e a documentação técnica ficam no repositório: https://github.com/ricardojmatteo/DAMA

| Entrega | Onde está | Situação |
|---|---|---|
| Arquitetura e padrões adotados | ADRs 0001 a 0009 em [`docs/adr`](https://github.com/ricardojmatteo/DAMA/tree/main/docs/adr) | Pronto |
| Modelo C4 e justificativa das decisões | Contexto, contêineres e componentes do backend em [`docs/arquitetura`](https://github.com/ricardojmatteo/DAMA/tree/main/docs/arquitetura) | Pronto |
| Implementação da arquitetura e dos serviços | [`backend/`](https://github.com/ricardojmatteo/DAMA/tree/main/backend), [`alert-service/`](https://github.com/ricardojmatteo/DAMA/tree/main/alert-service), [`frontend/`](https://github.com/ricardojmatteo/DAMA/tree/main/frontend) e [`database/`](https://github.com/ricardojmatteo/DAMA/tree/main/database); sobe com `docker compose up --build` | Pronto |
| Evidências de TDD | Testes em `backend/src/test` e `alert-service/test`; prints da execução e da cobertura JaCoCo em [DAMA Intelligence evidências de testes e cobertura JaCoCo.docx](DAMA%20Intelligence%20evidências%20de%20testes%20e%20cobertura%20JaCoCo.docx); análise do histórico em `docs/qualidade/tdd.md` (branch [`jenkins`](https://github.com/ricardojmatteo/DAMA/tree/jenkins)) | Parcial. Os testes existem, mas o histórico não mostra o teste escrito antes do código |
| Pipeline de Integração Contínua | [`backend/Jenkinsfile`](https://github.com/ricardojmatteo/DAMA/blob/main/backend/Jenkinsfile) no main; Jenkinsfiles do `alert-service` e do `frontend` e `docs/qualidade/jenkins.md` na branch [`jenkins`](https://github.com/ricardojmatteo/DAMA/tree/jenkins); prints dos pipelines no documento de evidências desta pasta | Parcial. Os pipelines do alert-service e do frontend ainda não estão no main |
| Inovação do projeto por sprint | Hipótese e situação atual em [`09_Projeto_Integrador_IV/hipotese-de-inovacao.md`](../09_Projeto_Integrador_IV/hipotese-de-inovacao.md) | A fazer. Falta o plano por sprint |
| Repositório GitHub | https://github.com/ricardojmatteo/DAMA | — |

No arquivo .zip da entrega, esta pasta leva também uma cópia de `docs/arquitetura`, `docs/adr` e dos Jenkinsfiles, para leitura sem abrir o GitHub.
