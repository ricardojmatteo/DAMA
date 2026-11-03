# Evidências de Qualidade de Software

| Arquivo | O que é | Como foi gerado |
|---|---|---|
| `jira-sprint3-2026-10-20.csv` | As 118 issues da Sprint 3 com status, responsável, datas de criação e resolução | Consulta `sprint = 111` no Jira (SMAN) em 20/10/2026 |
| `alert-service-testes-2026-10-20.txt` | Saída da suíte do `alert-service`: 19 de 19 passando | `cd alert-service && node --test --test-reporter=spec` em 20/10/2026 |
| `backend-surefire-2026-09-29/` | Relatórios do Maven Surefire da última execução do backend: 31 testes em 4 classes, 0 falhas, 9 de integração pulados por falta de Docker | `cd backend && mvn test` na máquina da equipe em 29/09/2026 (copiados de `backend/target/surefire-reports`) |
| `inventario-testes.csv` | Os 88 casos automatizados com ID, nível, classe, caso e história | Leitura das anotações `@Test` em `backend/src/test` e das chamadas `test(...)` em `alert-service/test` |

## Execução completa do backend

Feita em 23/10/2026 com `mvn -B clean verify`, com Docker: 82 testes, 0 falhas, 0 pulados, e a regra de 70% de cobertura do JaCoCo atendida. Os prints estão no [documento de evidências de testes](../../03_Modelagem_e_Construcao_de_Software/DAMA%20Intelligence%20evidências%20de%20testes%20e%20cobertura%20JaCoCo.docx).
