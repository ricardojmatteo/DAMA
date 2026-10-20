# Avaliação da maturidade dos processos

Avaliação feita a partir de evidências do repositório e do Jira. Os níveis são uma proposta inspirada nas áreas de processo do MPS.BR e do CMMI, e devem ser conferidos pela equipe antes da entrega. A correspondência formal com as áreas de prática do CMMI e a conclusão sobre o nível de maturidade estão em [cmmi.md](cmmi.md).

Escala usada:

| Nível | Significado |
|---|---|
| 0 | Não existe |
| 1 | Inicial: acontece, mas sem regra e sem repetição garantida |
| 2 | Gerenciado: planejado e acompanhado no projeto |
| 3 | Definido: padronizado, documentado e aplicado de forma consistente |

## Resultado por área

| Área | Nível | Evidências | Lacunas |
|---|---|---|---|
| Gerência de requisitos | 2 | Histórias no Jira-PO (épico POAN-109), requisitos numerados (RF, RN, RNF) e matriz de rastreabilidade história → requisito → endpoint → teste em `docs/requisitos/rastreabilidade.md` | Duas histórias (AI Credits e dashboard do colaborador) constam como "verificado no front e na demo", sem teste automatizado. Autenticação real (POAN-122) não implementada |
| Gerência de projeto | 1 | Sprints, quadro com 8 estados e responsáveis em todas as issues da Sprint 3 | Sprints sem meta; Sprint 2 fechada 13 dias após o fim previsto; 22 issues atualizadas em lote; 31 issues em andamento ao mesmo tempo; 7 duplicadas |
| Gerência de configuração | 2 | Git com branches e pull requests (#1, #2, #4, #5), `.gitignore`, `.env.example`, versionamento de scripts de banco | Sem regra de revisão praticada nos PRs, sem tags ou releases, sem política de nome de branch |
| Verificação e testes | 2 | 69 testes no backend (unitários e de integração com Testcontainers), 19 no `alert-service`, meta de 70% de cobertura com JaCoCo; estratégia descrita no [plano de testes](plano-de-testes.md) | Nenhum teste no front-end, cobertura ainda não medida, testes de integração dependem de Docker |
| Integração contínua | 1 | `Jenkinsfile` do backend com testes, build da imagem e relatório JUnit | Só o backend tem pipeline; `alert-service` e front-end ficam de fora; não há análise estática |
| Garantia da qualidade | 1 | Auditorias iniciadas neste documento (ver `auditorias.md`) | Sem Definition of Done escrita, sem checklist de revisão de código, sem análise estática |
| Medição e análise | 1 | Primeira coleta de métricas em `metricas.md` | Métricas não eram coletadas de forma sistemática; cobertura nunca registrada |
| Documentação e arquitetura | 3 | Modelo C4 (3 níveis), 9 ADRs, referência da API, documento de visão, READMEs por módulo | Ata de decisões de sprint e registro das Reviews e Retrospectivas ainda não existem |

## Resumo

| Nível | Áreas |
|---|---|
| 3 | 1 |
| 2 | 3 |
| 1 | 4 |

Média simples: 1,6. O processo técnico (arquitetura, testes, versionamento) é mais maduro que o de gestão e de qualidade. Os maiores ganhos estão em medir, revisar e padronizar o que já é feito. As ações correspondentes estão em [plano-melhoria.md](plano-melhoria.md).
