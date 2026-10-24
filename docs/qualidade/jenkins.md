# Pipelines Jenkins

Foram adicionados pipelines declarativos independentes para os modulos Node.js do monorepositorio:

| Job | Caminho do script | Validacoes |
| --- | --- | --- |
| Alert service | `alert-service/Jenkinsfile` | sintaxe Node, testes `node --test` via `npm test` e imagem Docker |
| Frontend | `frontend/Jenkinsfile` | sintaxe Node, `npm ci`, testes quando houver script `test` e imagem Docker |

## Configuracao no Jenkins

1. Crie um job do tipo **Pipeline** para cada modulo e escolha **Pipeline script from SCM**.
2. Aponte o SCM para a raiz do repositorio DAMA e selecione a branch desejada.
3. No job de alertas, informe `alert-service/Jenkinsfile` como *Script Path*; no job do frontend, informe `frontend/Jenkinsfile`.
4. Execute o primeiro build. O agente precisa ter Docker acessivel para a etapa de imagem e Node.js 20 ou superior. Node 22 e a versao usada pelas imagens Docker do projeto.

Os `Jenkinsfile` usam `dir(...)` porque o repositorio e um monorepo: o checkout ocorre na raiz e cada etapa entra somente na pasta do seu modulo. O frontend possui `package-lock.json`, por isso usa `npm ci`. O alert-service nao possui dependencias de terceiros e executa diretamente os testes nativos do Node.

## Sobre Java 21

Java/JDK 21 **nao e necessario** nos jobs `alert-service` e `frontend`, pois eles sao servicos Node.js. Configurar um JDK nesses jobs nao aumenta a cobertura nem participa da compilacao.

Para um job Maven separado, configure JDK 21 no agente ou como ferramenta global do Jenkins e confirme com `java -version` antes de `mvn clean verify`. O ambiente local usado nesta verificacao possui JDK 21. O `backend/pom.xml` atualmente declara alvo Java 17; JDK 21 pode compila-lo, desde que a compatibilidade de linguagem continue sendo Java 17.

## Validacao local equivalente

```bash
# alert-service
cd alert-service
node --check src/regras.js
node --check src/server.js
npm test
docker build -t dama-alert-service:local .

# frontend
cd ../frontend
node --check server.js
npm ci
npm test --if-present
docker build -t dama-frontend:local .
```

O comando do frontend nao falha enquanto nao existir script `test`, mas o log deixa clara essa lacuna. Ao adicionar testes, inclua o script `test` no `package.json`; o mesmo Jenkinsfile passara a executa-los sem alteracao.
