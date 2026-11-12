# DAMA Intelligence

Plataforma que transforma o uso produtivo de IA em desempenho mensurável: indicadores, metas, pontuação, conquistas e **AI Credits**, que liberam níveis maiores de autonomia no acesso às ferramentas de IA da empresa.

Projeto do 4º semestre de Ciência de Dados e Negócios da ESPM (2026) — Ana Luiza Proença, Gustavo Gomes, Kaique Lira e Ricardo Matteo.

## Estrutura do repositório

```
DAMA/
├── backend/          API REST — Spring Boot 3, Java 17, monólito modular
├── frontend/         Front-end (SPA em JavaScript) + BFF Node/Express
├── alert-service/    Serviço Node.js dos alertas de gestão (chamado pelo backend)
├── database/         Scripts PostgreSQL: schema, views, seed e dados de demonstração
├── docs/             Produto, arquitetura (C4), ADRs, API e rastreabilidade
├── Deliverable1/     Entregas acadêmicas do Deliverable 1 por disciplina
├── Deliverable2/     Entregas do Deliverable 2 (índice por disciplina, Qualidade, Pesquisa Operacional, Estatística, Machine Learning, Análise Preditiva)
├── projetos-aula/    Projetos-modelo das aulas (camadas, modular, microservices)
└── docker-compose.yml
```

## Como rodar

Pré-requisito: Docker.

```bash
docker compose up --build
```

Abra http://localhost:3000 e entre com um dos usuários de demonstração (senha `dama123` para todos; os botões da tela de login já preenchem):

| E-mail | Usuário | Perfil |
|---|---|---|
| admin@empresa-demonstracao.com | Administrador | Administrador (operador da plataforma, vê todas as empresas) |
| mariana.souza@demo.dama | Mariana Souza | Gestor |
| ricardo.nogueira@demo.dama | Ricardo Nogueira | Gestor |
| lucas.pereira@demo.dama | Lucas Pereira | Colaborador |

O acesso usa e-mail e senha com token JWT ([ADR-0010](docs/adr/0010-autenticacao-jwt.md)), e cada usuário só vê a própria empresa ([ADR-0011](docs/adr/0011-isolamento-por-empresa.md)). Em produção, defina `DAMA_JWT_SEGREDO` (32 caracteres ou mais).

| Serviço | Endereço |
|---|---|
| Front-end | http://localhost:3000 |
| API | http://localhost:8080/api (saúde em `/actuator/health`) |
| Serviço de alertas | http://localhost:4000 (saúde em `/health`) |
| PostgreSQL | localhost:5432, banco/usuário/senha `dama` |

Para recriar o banco do zero (necessário depois de mudar os scripts de `database/`):

```bash
docker compose down -v
```

Portas e credenciais podem ser alteradas copiando `.env.example` para `.env`. Para subir sem os dados de demonstração, remova a linha do `04_demo.sql` no `docker-compose.yml`.

## Desenvolvimento sem Docker

```bash
# 1. PostgreSQL local com os scripts de database/ aplicados (01, 02, 03 e, se quiser, demo/04)
# 2. Serviço de alertas (opcional: sem ALERTAS_URL a API usa as regras locais)
cd alert-service && npm start
# 3. API
cd backend && ALERTAS_URL=http://localhost:4000 mvn spring-boot:run
# 4. Front-end (em outro terminal)
cd frontend && npm install && API_BASE_URL=http://localhost:8080 npm start
```

## Testes

```bash
cd backend && mvn test
cd alert-service && npm test
```

Unitários para regras de metas, conquistas e análises de gestão; testes de integração da API contra PostgreSQL real via Testcontainers (exigem Docker, senão são pulados). No `alert-service`, as regras e a API HTTP são testadas com `node --test` (sem dependências). `AlertaGestaoNodeTest` cobre o contrato e o fallback do backend com um servidor HTTP simulado.

## Documentação

- [Produto e requisitos](docs/produto/)
- [Arquitetura C4](docs/arquitetura/) e [ADRs](docs/adr/)
- [Referência da API](docs/api/)
- [Rastreabilidade e regras de negócio](docs/requisitos/)
- [Como o ranking é calculado e o que falta para integrar com o Jira](docs/requisitos/ranking.md)
- [AI Maturity Score: dimensões e pesos](docs/produto/ams.md) e [nível de autonomia: real vs. interface](docs/produto/autonomia.md)
- [Curadoria do código com IA (code review)](docs/qualidade/code-review-ia.md)
- [Deliverable 2](Deliverable2/): [Qualidade de Software](Deliverable2/04_Qualidade_de_Software/), [Pesquisa Operacional](Deliverable2/07_Pesquisa_Operacional_e_Calculo_Aplicado/), [Estatística Multivariada](Deliverable2/02_Estatistica_Multivariada/), [Machine Learning](Deliverable2/05_Machine_Learning/) e [Análise Preditiva](Deliverable2/08_Analise_Preditiva/)

## Uso de IA

Este projeto foi desenvolvido com o auxílio de ferramentas de inteligência artificial generativa, usadas para apoiar a codificação, a análise de dados, a revisão e a redação da documentação. A equipe define o escopo e as decisões e responde pelo conteúdo entregue.
