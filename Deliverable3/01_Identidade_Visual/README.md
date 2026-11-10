# Identidade visual — DAMA

**Slogan:** *Elegância em movimento*

A identidade parte de duas referências: o logotipo "DAMA" em letras maiúsculas e bem espaçadas sobre fundo escuro ([referencia-estilo.png](referencia-estilo.png)) e uma foto de luz laranja em movimento ([referencia-cor.png](referencia-cor.png)). A ideia é transmitir elegância (tipografia limpa e espaçada) e energia (o laranja e a sensação de movimento).

## Logotipo

| Arquivo | Uso |
|---|---|
| [logo-dama.svg](logo-dama.svg) | Versão principal: fundo escuro, "DAMA" com degradê laranja e slogan |
| [logo-dama-claro.svg](logo-dama-claro.svg) | Para fundos claros, como documentos e slides |
| [icone-dama.svg](icone-dama.svg) | Ícone/avatar: "D" sobre quadrado laranja (favicon, redes sociais) |

Regras de uso:
- O "DAMA" é sempre escrito em maiúsculas, em Montserrat Bold, com espaçamento entre letras de cerca de 30%.
- O degradê do logo vai de `#FFE3C2` a `#FFB061`, `#FF7A1A` e `#E0570B`. Quando não for possível usar degradê, use o laranja sólido `#FF8A2A` em fundo escuro ou `#C2470A` em fundo claro.
- O slogan vem abaixo do logo, em maiúsculas, com um traço laranja de cada lado.
- Os SVGs usam as fontes Montserrat e Hind do Google Fonts. Em computadores sem essas fontes instaladas, a visualização usa uma fonte parecida.

## Cores

| Nome | Hex | Uso |
|---|---|---|
| Laranja DAMA | `#FF7A1A` | Cor principal, botões, destaques |
| Laranja claro | `#FF8A2A` | Links, segundo tom do degradê |
| Laranja escuro | `#C2470A` | Laranja sobre fundo claro |
| Pêssego | `#FFD2A3` | Destaques secundários, brilhos |
| Areia | `#F6DCC4` | Slogan sobre fundo escuro |
| Fundo | `#070707` | Fundo do site |
| Creme | `#FBF4EC` | Fundo claro |

## Tipografia

- **Montserrat:** logo e títulos. Geométrica e moderna.
- **Hind:** textos e interface. Muito legível em tamanhos pequenos.

## Acessibilidade

- Os textos têm contraste mínimo de 4.5:1 com o fundo.
- O texto do site passou de 14px para 15px.
- No ranking, o usuário logado é destacado em pêssego claro, que se diferencia das outras barras também pela luminosidade, e não apenas pela cor.

## Aplicação no site

A identidade foi aplicada ao front-end do DAMA Intelligence no [PR #8](https://github.com/ricardojmatteo/DAMA/pull/8):
- `frontend/public/css/app.css`: cores, fontes, logo com degradê e o fundo do login com luz em movimento
- `frontend/public/index.html`: carregamento das fontes Montserrat e Hind, e o favicon
- `frontend/public/js/app.js`: slogan na tela de login
- `frontend/public/js/graficos.js` e `js/paginas/ranking.js`: novas cores dos gráficos

## Figma

Protótipo no Figma Make: [Identidade Visual da Marca DAMA](https://www.figma.com/make/G6hVC4OH8dNyINV1qNGn8k/Identidade-Visual-da-Marca-DAMA). Os prompts usados para gerá-lo estão em [prompts-figma.md](prompts-figma.md).
