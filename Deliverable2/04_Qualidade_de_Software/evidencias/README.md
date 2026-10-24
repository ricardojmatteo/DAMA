# Evidências de Qualidade de Software

| Arquivo | O que é | Como foi gerado |
|---|---|---|
| `jira-sprint3-2026-10-20.csv` | As 118 issues da Sprint 3 com status, responsável, datas de criação e resolução | Consulta `sprint = 111` no Jira (SMAN) em 20/10/2026 |
| `alert-service-testes-2026-10-20.txt` | Saída da suíte do `alert-service`: 19 de 19 passando | `cd alert-service && node --test --test-reporter=spec` em 20/10/2026 |
| `backend-surefire-2026-09-29/` | Relatórios do Maven Surefire da última execução do backend: 31 testes em 4 classes, 0 falhas, 9 de integração pulados por falta de Docker | `cd backend && mvn test` na máquina da equipe em 29/09/2026 (copiados de `backend/target/surefire-reports`) |
| `inventario-testes.csv` | Os 88 casos automatizados com ID, nível, classe, caso e história | Leitura das anotações `@Test` em `backend/src/test` e das chamadas `test(...)` em `alert-service/test` |

## Pendente

A execução completa do backend com cobertura ainda não foi feita. Ela precisa de uma máquina com Docker e acesso ao Maven Central:

```bash
cd backend
mvn -B clean verify          # roda unitários + integração (Testcontainers) e a regra de 70% do JaCoCo
```

Depois, salvar aqui:

- `backend/target/surefire-reports/*.txt`, numa pasta `backend-surefire-AAAA-MM-DD/`;
- o resumo de `backend/target/site/jacoco/index.html`, como print ou o `jacoco.csv`.

Por fim, registrar o resultado na seção 8 do [plano de testes](../plano-de-testes.md) e em [metricas.md](../metricas.md).
