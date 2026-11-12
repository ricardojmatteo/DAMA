# ADR-0010: Autenticação por e-mail e senha com token JWT

- **Status:** Aceita (substitui a [ADR-0006](0006-autenticacao-por-cabecalho.md))
- **Data:** 2026-11-12

## Contexto

A ADR-0006 identificava o usuário pelo cabeçalho `X-Colaborador-Id`. O cabeçalho não prova identidade: qualquer cliente que mandasse `1` virava Administrador. A avaliação de Modelagem e Construção de Software marcou isso como prioridade crítica, e o produto não pode ser demonstrado como SaaS assim.

## Decisão

- `POST /api/auth/login` recebe e-mail e senha, confere o hash BCrypt guardado em `colaborador.senha_hash` e devolve um JWT HS256 com validade de 8 horas.
- As demais rotas exigem `Authorization: Bearer <token>`. O `JwtAuthenticationFilter` valida assinatura e validade e carrega do banco o colaborador e suas permissões **a cada requisição**. O token carrega só a identidade (`sub`), então desligar alguém ou trocar seu perfil vale na hora.
- Colaborador desligado (`ativo = false`) não autentica, nem com token válido. Sem token válido a API responde 401; autenticado sem permissão, 403.
- O e-mail passa a ser único na plataforma (índice `ux_colaborador_email`), porque o login não pede a empresa.
- Senha: definida no cadastro (opcional), trocada pelo próprio usuário em `POST /api/auth/senha` ou redefinida por quem tem `COLABORADOR_ESCREVER` em `PUT /api/colaboradores/{id}/senha`.
- O segredo vem de `DAMA_JWT_SEGREDO` (mínimo de 32 bytes). Sem ele, a API sobe com um segredo de desenvolvimento e registra um aviso.

## Alternativas consideradas

- **SSO/OAuth2 com provedor externo (Keycloak, Entra ID).** É o destino em produção, mas exige um provedor rodando no compose e configuração por cliente. O formato de token é o mesmo (JWT), então a troca futura fica no filtro.
- **Sessão com cookie no BFF.** Tiraria o token do `localStorage`, mas o BFF hoje é só um repasse e passaria a guardar estado. Fica como evolução.
- **Manter o cabeçalho e confiar no BFF.** A porta 8080 da API fica exposta no compose, então o BFF é contornável.

## Consequências

- Identidade agora é provada; o `@PreAuthorize` por permissão continua igual, sem mudar nenhum controller.
- O token fica no `localStorage` do navegador: sujeito a XSS. Mitigação atual: o front não injeta HTML de dados do usuário (usa `textContent` via `h()`).
- Não há revogação de token antes da expiração, mas como as permissões são lidas do banco a cada requisição, desligar o colaborador corta o acesso imediatamente.
- Usuários de demonstração entram com a senha `dama123` (gerada por `pgcrypto` nos scripts de banco).
