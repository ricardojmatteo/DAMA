# Evidências de responsividade

Capturas da interface em três larguras, com o usuário Lucas Pereira (colaborador).

| Arquivo | Largura | O que mostra |
|---|---|---|
| celular-375px-login.jpg | 375 px | Login com usuários de demonstração em coluna única |
| celular-375px-painel.jpg | 375 px | Meu painel com cartões em duas colunas e tabela rolável |
| celular-375px-menu-aberto.jpg | 375 px | Menu lateral aberto como gaveta |
| tablet-768px-painel.jpg | 768 px | Meu painel com tabela completa visível |
| desktop-painel.jpg | desktop | Menu lateral fixo e grade de três colunas |

As capturas foram feitas com os dados de um servidor de pré-visualização local que simula a API, porque o ambiente de captura não tinha Docker. O layout é o mesmo da aplicação completa; os números exibidos não vêm do banco. Para refazer com dados reais, suba a stack com `docker compose up --build`, entre em http://localhost:3000 e redimensione a janela para as larguras da tabela.
