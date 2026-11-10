# Evidências de Qualidade de Software

Arquivos brutos da Parte 7 do [documento de Qualidade](../DAMA_Qualidade_de_Software_Deliverable2.docx). O print do Docker Desktop (Anexo A) está na pasta acima, em [PrintDocker.png](../PrintDocker.png).

| Arquivo | Anexo | O que é | Como foi gerado |
|---|---|---|---|
| `alert-service-testes-2026-10-20.txt` | B | Saída da suíte do `alert-service`: 19 de 19 passando | `cd alert-service && node --test --test-reporter=spec` em 20/10/2026 |
| `backend-surefire-2026-09-29/` | C | Relatórios do Maven Surefire da execução do backend em 29/09: 31 testes em 4 classes, 0 falhas, 0 erros, 9 de integração pulados por falta de Docker | `cd backend && mvn test` na máquina da equipe em 29/09/2026 (copiados de `backend/target/surefire-reports`) |
| `inventario-testes.csv` | D | Os 88 casos automatizados com ID, nível, classe, caso e história | Leitura das anotações `@Test` em `backend/src/test` e das chamadas `test(...)` em `alert-service/test` |
| `jira-sprint3-2026-10-20.csv` | E | As 118 issues da Sprint 3 com status, responsável, datas de criação e resolução | Consulta `sprint = 111` no Jira (SMAN) em 20/10/2026 |

## Execução completa do backend (23/10/2026)

A suíte completa do backend foi executada em 23/10/2026 com `mvn -B clean verify`, com Docker: 82 testes, 0 falhas, 0 erros e 0 pulados. A regra de cobertura de 70% do JaCoCo foi atendida e o build terminou com sucesso. Os prints estão no [documento de evidências de testes](../../03_Modelagem_e_Construcao_de_Software/DAMA%20Intelligence%20evid%C3%AAncias%20de%20testes%20e%20cobertura%20JaCoCo.docx).

Em 23 e 24/10 também entraram no main os pipelines do `alert-service` e do `frontend` no Jenkins. As Partes 1 a 6 do documento registram a situação das coletas de 15/10 e 20/10, anteriores a essas entregas.
