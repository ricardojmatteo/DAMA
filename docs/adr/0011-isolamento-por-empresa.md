# ADR-0011: Isolamento entre empresas-cliente no service (`EscopoEmpresa`)

- **Status:** Aceita
- **Data:** 2026-11-12

## Contexto

O `@PreAuthorize` responde "este perfil pode fazer esta operação?", mas não "neste recurso?". Antes desta decisão, um gestor da empresa A lia metas, saldo de AI Credits e auditoria da empresa B só trocando o id na URL. Para um SaaS multiempresa é o risco mais grave do produto.

## Decisão

- Todo recurso pertence a uma empresa. Os services descobrem a empresa do recurso (pelo `empresaId` da URL ou pela entidade carregada: colaborador, meta, desafio, indicador, regra...) e chamam `EscopoEmpresa.exigir(empresaId)` **antes** de ler ou alterar.
- O usuário só acessa a própria empresa. A exceção é a nova permissão `PLATAFORMA_ADMINISTRAR`, dada só ao perfil Administrador: é o operador da plataforma, que cadastra empresas-cliente e enxerga todas.
- Violação vira **403** com a mensagem "O recurso pertence a outra empresa." (não 404: o recurso existe).
- Listagens sem empresa na URL (`GET /api/empresas`) são filtradas para a empresa do usuário.
- Leituras internas entre módulos (`ColaboradorFacade.buscarPorId`, `PerfilFacade.perfisDoColaborador`) não aplicam escopo, porque quem chama já validou o contexto. As rotas da API usam as variantes com escopo (`buscarNaEmpresaDoUsuario`, `perfisDoColaboradorDaEmpresa`).
- A autoria (`registrado_por`) vem sempre do usuário autenticado; campos de autoria enviados no corpo são ignorados.

## Alternativas consideradas

- **Row Level Security no PostgreSQL.** Isolaria no banco, mas exige passar a empresa do usuário em cada conexão (`SET app.empresa_id`) e muda todas as consultas de analytics que cruzam tabelas. Fica como reforço futuro.
- **Expressões SpEL no `@PreAuthorize` (`@escopo.permite(#empresaId)`).** Funciona para rotas com `empresaId` na URL, mas a maioria das rotas recebe o id de outro recurso (`/metas/{id}`), e a empresa só é conhecida depois de carregar a entidade.
- **Schema por empresa.** Isolamento forte, mas multiplica migrações e não combina com o tamanho do projeto.

## Consequências

- A regra fica em um lugar (`EscopoEmpresa`) e é chamada em todos os services que recebem ids pela API.
- Um service novo pode esquecer a chamada. Mitigação: o teste de integração deve ter um caso de acesso cruzado por módulo, e a revisão de código confere a chamada.
- Perfis (`Administrador`, `Gestor`, `Colaborador`), níveis de evolução e níveis de autonomia continuam globais; personalização por empresa fica para depois.
