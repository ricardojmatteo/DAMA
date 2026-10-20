# Front-end

SPA em JavaScript puro (módulos ES, sem build) servida por um BFF Node/Express. O BFF entrega os arquivos de `public/` e encaminha `/api/*` para a API, repassando o cabeçalho `X-Colaborador-Id`.

```
frontend/
├── server.js              BFF: estáticos, proxy /api, /health e Chart.js em /vendor
└── public/
    ├── index.html
    ├── css/app.css
    └── js/
        ├── app.js         rotas, menu por permissão e login
        ├── api.js         cliente HTTP
        ├── sessao.js      usuário logado e permissões
        ├── dados.js       listas de apoio com cache (pessoas, equipes, indicadores)
        ├── ui.js          componentes (tabela, painel, formulário, modal, toast)
        ├── graficos.js    Chart.js
        └── paginas/       uma página por item do menu
```

## Rodar

```bash
npm install
API_BASE_URL=http://localhost:8080 npm start     # http://localhost:3000
```

`PORT` muda a porta do front. No PowerShell: `$env:API_BASE_URL="http://localhost:8080"; npm start`.

## Responsividade

| Largura | Comportamento |
|---|---|
| acima de 1200 px | menu lateral fixo de 244 px, grades de 2 a 3 colunas |
| 1025 a 1200 px | menu lateral de 216 px e margens menores |
| 901 a 1024 px | grades de duas colunas viram uma |
| até 900 px | menu vira gaveta aberta pelo botão do topo; fecha ao escolher uma tela, ao tocar fora ou com Esc; tabelas rolam na horizontal |
| até 560 px | painéis compactos, filtros em largura total, modal ocupa a base da tela, avisos em largura total |
| até 340 px | cartões de indicadores em uma coluna |

Em telas de toque os botões têm altura mínima de 40 px e os campos usam fonte de 16 px, o que evita o zoom automático do iOS. Capturas em celular, tablet e desktop estão em [docs/evidencias/responsividade](../docs/evidencias/responsividade/).

## Telas

| Menu | Quem vê | Conteúdo |
|---|---|---|
| Meu painel | todos | Pontos, AI Credits, nível, conquistas, metas e evolução dos indicadores |
| Evolução e conquistas | todos | Níveis, progresso e conquistas; gestores criam e concedem conquistas |
| AI Credits | todos | Saldo, nível de autonomia, histórico; gestores registram atividades e lançam créditos |
| Ranking e pontuação | todos | Ranking da empresa; só o administrador lança pontos e mantém regras |
| Metas | todos | Metas com progresso; gestores criam metas e registram progresso |
| Indicadores | todos | Evolução por pessoa ou equipe com filtro de período |
| Equipes | todos | Dashboard da equipe; gestores comparam equipes |
| Desafios / Reconhecimentos | todos | Listagem; gestores criam, inscrevem e concluem |
| Análises de gestão | gestores | Alertas de onde agir |
| Pessoas e perfis | gestores | Cadastro de colaboradores; administrador gerencia perfis e equipes |
| Auditoria | administrador | Movimentações registradas |
