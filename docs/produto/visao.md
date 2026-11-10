# Visão do produto — versão revisada (Deliverable 2)

**DAMA Intelligence** · Grupo 1 · Ana Luiza Proença, Gustavo Gomes, Kaique Lira e Ricardo Matteo · ESPM, 2026

Esta versão substitui a visão do Deliverable 1 ([DocVisao.doc](DocVisao.doc)), que apresentava o DAMA como "plataforma de gamificação para análise de desempenho". A revisão atende ao apontamento da avaliação do D1: **realinhar a visão à governança de IA** e explicitar o **AI Maturity Score** e seus componentes. Os requisitos estão em [catalogo.md](../requisitos/catalogo.md), a priorização em [priorizacao-mvp.md](../requisitos/priorizacao-mvp.md) e a rastreabilidade em [rastreabilidade.md](../requisitos/rastreabilidade.md).

## 1. Problema

As empresas estão liberando ferramentas de IA generativa sem saber responder três perguntas:

1. **Quem está usando bem?** O uso é medido por volume (licenças, requisições, tokens), e não pelo valor gerado.
2. **Onde está o risco?** Equipes que usam muito e foram pouco capacitadas convivem com equipes que pagam licenças que ninguém usa.
3. **Quanta autonomia dar a cada pessoa?** O acesso costuma ser igual para todos e definido só por política: ou se bloqueia tudo, ou se libera tudo.

Os dados sintéticos do projeto ilustram o problema: dos 106 departamentos analisados, 54 têm adoção baixa (31% de adoção e 71% de licenças ociosas), 23 estão no quadrante de desperdício (investimento acima da mediana e maturidade abaixo) e 16 usam IA acima da média com capacitação abaixo da média, a situação de maior risco de governança ([Estatística Multivariada](../../Deliverable2/02_Estatistica_Multivariada/README.md)). São dados sintéticos: ilustram o desenho, mas não comprovam o problema em empresas reais.

## 2. Declaração de visão

> **Para** empresas que estão adotando IA generativa e precisam equilibrar produtividade com governança,
> **o** DAMA Intelligence **é** uma plataforma de governança e desenvolvimento do uso de IA
> **que** mede a maturidade de cada equipe no uso de IA (AI Maturity Score), reconhece o uso produtivo de cada pessoa com AI Credits e libera níveis de autonomia de acesso às ferramentas conforme a evidência acumulada.
> **Diferente de** ferramentas que só contam uso ou de políticas que bloqueiam ou liberam acesso para todos,
> **o DAMA** liga a autonomia de cada pessoa à evidência de uso produtivo e responsável, com regras explícitas e auditáveis.

## 3. Personas e necessidades

As personas são as do Documento Completo e da demonstração (`database/demo/04_demo.sql`). As do D1 (Mariana, Rafael e Camila, no DocVisao) foram consolidadas nestas três.

| Persona | Na demo | Dor | Necessidades |
|---|---|---|---|
| **Gestora de pessoas** (Mariana, 38) | Mariana Souza, perfil Gestor | Recebe informação fragmentada e não sabe quem ou qual equipe precisa de atenção | N01, N02, N06 |
| **Colaborador** (Lucas, 26) | Lucas Pereira, perfil Colaborador | Não sabe como seu desempenho é percebido nem o que fazer para ganhar acesso a mais ferramentas de IA | N03, N04 |
| **Diretor** (Ricardo, 45) | Ricardo Nogueira, perfil Gestor | Tem muitos dados e não consegue decidir onde investir em IA e capacitação | N05, N07 |
| **Empresa** (área de TI, segurança e compliance) | Perfil Administrador | Precisa controlar quem acessa o quê, com registro auditável e sem expor dados individuais | N06, N08 |

Necessidades usadas na [matriz de rastreabilidade](../requisitos/rastreabilidade.md):

| ID | Necessidade |
|---|---|
| N01 | Acompanhar indicadores, metas e evolução da equipe em um só lugar |
| N02 | Identificar cedo quem ou qual equipe precisa de atenção |
| N03 | Saber como o próprio desempenho é percebido: metas, pontos, ranking e conquistas |
| N04 | Ser reconhecido pelo uso produtivo de IA e ter um caminho claro para ganhar autonomia |
| N05 | Ter visão consolidada e comparar equipes para decidir investimentos |
| N06 | Governar o acesso à IA com regras explícitas e auditáveis |
| N07 | Medir a maturidade no uso de IA por equipe para direcionar capacitação e licenças |
| N08 | Proteger os dados de desempenho: acesso por perfil e isolamento entre empresas |

## 4. AI Maturity Score

O **AI Maturity Score (AMS)** mede, de 0 a 100, o quanto uma equipe (departamento) usa IA de forma ampla, regular e eficiente e o quanto está capacitada para isso. É a métrica de governança do produto: orienta capacitação e licenças no nível da equipe. Os AI Credits fazem o mesmo papel no nível da pessoa.

O cálculo segue o índice IMIA, construído e validado na disciplina de [Estatística Multivariada](../../Deliverable2/02_Estatistica_Multivariada/analise_multivariada.Rmd).

### 4.1 Componentes

| Dimensão | Indicador | O que mede | Sentido |
|---|---|---|---|
| **Uso** | Adoção | % de colaboradores que usaram IA no período | Maior é melhor |
| | Intensidade | log(1 + requisições por colaborador por mês) | Maior é melhor |
| | Regularidade | Fração dos meses do período em que os usuários usaram IA | Maior é melhor |
| | Amplitude | Média de ferramentas diferentes por usuário | Maior é melhor |
| | Licenças ociosas | % de licenças ativas sem nenhum uso | Invertido |
| | Tokens por requisição | log(tokens por requisição) | Invertido |
| **Capacitação** | Capacitação | % de colaboradores com curso de IA concluído | Maior é melhor |
| | Horas de curso | Horas de curso por colaborador | Maior é melhor |

### 4.2 Cálculo

1. Calcular os 8 indicadores por departamento no período. Só entram colaboradores ativos e departamentos com pelo menos 5 deles.
2. Inverter o sinal dos indicadores invertidos e padronizar cada indicador (escore z) entre os departamentos da empresa.
3. Dimensão Uso = média dos 6 escores de uso. Dimensão Capacitação = média dos 2 escores de capacitação.
4. **AMS = (Uso + Capacitação) ÷ 2**, reescalado de 0 a 100 entre os departamentos.
5. Departamentos sem nenhum uso de IA no período recebem AMS 0.

Indicadores testados e deixados de fora: taxa de sucesso das requisições, nota nos cursos e incidentes de segurança. Os três têm correlação abaixo de 0,30 com todos os demais, ou seja, não medem a mesma coisa. Incidentes continuam acompanhados à parte, como indicador de risco (seção 5).

O investimento (custo por colaborador, licenças por colaborador e execução do orçamento) **não entra no AMS**. Ele forma um índice separado, o IINV, porque maturidade é o que a equipe faz com a IA e investimento é quanto ela gasta. O cruzamento dos dois gera os quadrantes do painel: no **quadrante de desperdício**, o investimento está acima da mediana e o AMS abaixo.

### 4.3 Leitura

| Faixa do AMS | Leitura | Ação sugerida |
|---|---|---|
| 0 a 39 | Adoção baixa | Rever licenças ociosas e começar pela capacitação |
| 40 a 69 | Em desenvolvimento | Capacitar quem usa muito e ampliar o acesso aos capacitados |
| 70 a 100 | Avançado | Ampliar níveis de autonomia e usar a equipe como referência |

As faixas são uma **proposta do PO**, a calibrar com dados reais. Na base sintética, a média por setor vai de 25,6 (Indústria) a 82,4 (Tecnologia).

### 4.4 Situação

| Parte | Situação |
|---|---|
| Método e resultado na base sintética | Pronto ([Estatística Multivariada](../../Deliverable2/02_Estatistica_Multivariada/README.md)) |
| Exportar AMS, IINV e perfis por departamento | Sprint 4, SMAN-334 |
| Tabelas e carga dos resultados na plataforma | Sprint 4, SMAN-335 |
| Leitura na API | Sprint 4, SMAN-336 |
| Painel de maturidade com interpretação | Sprint 4, SMAN-340 |

Na plataforma, o AMS corresponde aos requisitos RF40 e RF41 do [catálogo](../requisitos/catalogo.md). Nenhum dos dois está implementado.

## 5. Hipótese de inovação

### 5.1 Enunciado

> Ligar a autonomia de acesso à IA à evidência de uso produtivo (AI Credits e níveis de autonomia) e direcionar a capacitação pelo AI Maturity Score **aumenta a maturidade no uso de IA das equipes sem aumentar os incidentes de segurança**, em comparação com um acesso igual para todos.

### 5.2 Como medir

A hipótese é testada em um piloto com uma empresa: as equipes que usam o DAMA são comparadas com equipes da mesma empresa que seguem com acesso igual para todos. A linha de base é medida no início do piloto, e a comparação é feita depois de 3 meses.

| Indicador | Fonte | Critério para confirmar |
|---|---|---|
| I1. AMS médio das equipes | RF40 | Aumento de pelo menos 10 pontos nas equipes do piloto, acima da variação das equipes de comparação |
| I2. Licenças ociosas | Componente do AMS | Queda de pelo menos 15 pontos percentuais |
| I3. Incidentes de segurança por 100 colaboradores | Registro de incidentes da empresa | Não aumentar em relação à linha de base |
| I4. Tokens gastos por story point entregue | Ranking por tarefas (RF37) | Queda de pelo menos 10% |

**Leitura do resultado:**
- **Confirmada:** I1 e I3 atendidos, e pelo menos um entre I2 e I4.
- **Ajustada:** I1 atendido, mas I3 piora. A regra de autonomia precisa de mais controle.
- **Refutada:** I1 não atendido.

As metas numéricas são uma **proposta do PO** e devem ser revistas com a empresa do piloto antes de começar.

### 5.3 Validação de curto prazo (Deliverable 2)

O piloto de 3 meses não cabe no semestre. O que dá para testar agora é se as personas **entendem e valorizam** o mecanismo, com o roteiro de [validação parcial](../validacao/validacao-parcial.md) (SMAN-187, Sprint 4).

| Critério | Meta |
|---|---|
| Participantes por persona | Pelo menos 1, idealmente 3 |
| Tarefas concluídas sem ajuda | Pelo menos 2 de 3 tarefas por participante |
| Nota de clareza do propósito (1 a 5) | Média de pelo menos 4 |
| AI Credits e níveis de autonomia "fazem sentido" (pergunta 3) | Pelo menos 2 de 3 participantes da persona Colaborador respondem que sim |

Se esses critérios forem atendidos, a hipótese segue para o piloto. Se não, o mecanismo de AI Credits é revisto antes do piloto (SMAN-352). **Nenhuma sessão foi feita até 10/11/2026**, então ainda não há resultado.

## 6. Escopo

| Dentro do produto | Situação |
|---|---|
| Colaboradores, equipes, perfis e permissões | Implementado |
| Indicadores, metas, evolução e dashboards | Implementado |
| Pontuação, ranking por tarefas entregues, desafios, reconhecimentos e conquistas | Implementado |
| AI Credits: atividades, elegibilidade, saldo, histórico e níveis de autonomia | Implementado |
| Análises de gestão (alertas) e auditoria | Implementado |
| AI Maturity Score e painel de maturidade | Sprint 4 |
| AI Literacy Score individual (eficiência no uso de tokens) | Sprint 4 (SMAN-343). Hoje o ranking já desconta o uso excessivo de tokens |
| Login real e isolamento entre empresas | Não implementado; isolamento na Sprint 4 (SMAN-344) |

| Fora do escopo deste semestre | Motivo |
|---|---|
| Integração automática com o Jira e com as ferramentas de IA | Os dados entram por cadastro e por carga de CSV |
| Bloqueio real de acesso nas ferramentas de IA | O DAMA define o nível de autonomia; quem aplica é a TI da empresa |
| Uso em produção com dados reais | Exige login real, isolamento entre empresas e testes de segurança |

### Limites da demonstração

A demonstração usa identificação por cabeçalho (`X-Colaborador-Id`, [ADR-0006](../adr/0006-autenticacao-por-cabecalho.md)), sem senha, e não isola os dados entre empresas. Ela demonstra as regras de negócio e as permissões por perfil, **não a segurança de uso real**. Antes de qualquer piloto com dados reais, é preciso concluir o RF39 (autenticação) e o RNF09 (isolamento), e testar falsificação de identidade e acesso cruzado.

## 7. Roadmap

| Release | Conteúdo | Situação |
|---|---|---|
| MVP 1 (D1) | Cadastro, indicadores e dashboard inicial; front de protótipo | Entregue em 04/09 |
| v0.2.0 (D2, Sprints 2 a 4) | Recorte do MVP em [priorizacao-mvp.md](../requisitos/priorizacao-mvp.md): ciclo atividade → AI Credits → autonomia, dashboards, ranking, AI Maturity Score | Em andamento. Fecha na Sprint 4, até 13/11 |
| Piloto (D3) | Login real, isolamento, AI Literacy Score e medição da hipótese em uma empresa | Planejado |
