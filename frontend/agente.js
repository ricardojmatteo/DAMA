// Assistente DAMA: agente de IA que roda no BFF e consulta a API do DAMA em nome do usuário logado.
// A chave da Anthropic fica só no servidor (variável de ambiente), nunca vai para o navegador.
// O agente só faz GET, com o token do próprio usuário: enxerga exatamente o que o perfil dele já pode ver
// (permissões e isolamento por empresa continuam sendo aplicados pelo backend, ADR-0010/0011).

// =====================================================================================
//  CHAVE DA API: defina ANTHROPIC_API_KEY no arquivo .env da raiz do projeto
//  (ou no ambiente, ex.: ANTHROPIC_API_KEY=sk-ant-... npm start).
//  NÃO cole a chave aqui no código: o repositório é público.
// =====================================================================================
const ANTHROPIC_API_KEY = process.env.ANTHROPIC_API_KEY || '';
const MODELO = process.env.ANTHROPIC_MODELO || 'claude-sonnet-5-5';
const MAX_PASSOS = 8;          // limite de idas e voltas agente -> ferramenta por pergunta
const MAX_HISTORICO = 30;      // mensagens da sessão enviadas ao modelo
const MAX_RESULTADO = 15000;   // caracteres de cada resposta da API repassados ao modelo

const FERRAMENTAS = [
  {
    name: 'consultar_dama',
    description:
      'Faz um GET na API REST do DAMA Intelligence com as permissões do usuário logado e devolve o JSON. ' +
      'Use para buscar dados reais antes de responder. Caminho relativo a /api, ex.: "/me", ' +
      '"/colaboradores/12/ai-credits/saldo", "/empresas/1/metas?colaboradorId=12". ' +
      'Respostas 403 significam que o perfil do usuário não pode ver aquele recurso.',
    input_schema: {
      type: 'object',
      properties: {
        caminho: { type: 'string', description: 'Caminho começando com "/", com query string opcional.' }
      },
      required: ['caminho']
    }
  }
];

const ENDPOINTS = `
Endpoints de leitura (GET) mais úteis — {empresaId} e {id} vêm do contexto do usuário:
- /me — usuário logado, perfis e permissões
- /colaboradores/{id}/dashboard — pontos, AI Credits, nível, conquistas e metas do colaborador
- /colaboradores/{id}/ai-credits/saldo e /colaboradores/{id}/ai-credits/historico
- /colaboradores/{id}/atividades, /colaboradores/{id}/evolucao, /colaboradores/{id}/conquistas
- /colaboradores/{id}/pontuacao, /colaboradores/{id}/tarefas, /colaboradores/{id}/reconhecimentos, /colaboradores/{id}/capacitacoes
- /ai-credits/niveis — faixas de autonomia; /niveis-evolucao — níveis de gamificação
- /empresas/{empresaId}/metas?colaboradorId&departamentoId&status
- /empresas/{empresaId}/indicadores; /indicadores/{id}/valores?colaboradorId&departamentoId&inicio&fim
- /empresas/{empresaId}/atividades-produtivas; /empresas/{empresaId}/ai-credits/regras
- /empresas/{empresaId}/desafios; /empresas/{empresaId}/reconhecimentos; /empresas/{empresaId}/conquistas
- /empresas/{empresaId}/analytics/ranking-colaboradores?inicio&fim (datas AAAA-MM-DD)
Só para gestão (ANALYTICS_LER / COLABORADOR_LER):
- /empresas/{empresaId}/colaboradores; /empresas/{empresaId}/departamentos; /departamentos/{id}/dashboard
- /empresas/{empresaId}/analytics/visao-consolidada, /alertas, /ranking-departamentos, /indicadores-departamentos
- /empresas/{empresaId}/orcamentos-ia?mes=AAAA-MM; /empresas/{empresaId}/ams
`;

function promptSistema(me) {
  const c = me.colaborador || {};
  return `Você é o Assistente DAMA, agente de IA do DAMA Intelligence, plataforma que acompanha desempenho e uso de IA
e trata o acesso à IA como algo conquistado (AI Credits definem a autonomia: Básico ≥ 0, Ampliado ≥ 100,
Avançado ≥ 250, Total ≥ 500; gastar pode rebaixar o nível).

Usuário desta sessão:
- Nome: ${c.nome} (colaboradorId ${c.colaboradorId}), departamento ${c.departamentoNome || '—'}, empresaId ${c.empresaId}
- Perfis: ${(me.perfis || []).join(', ') || 'nenhum'}
- Permissões: ${(me.permissoes || []).join(', ')}
- Data de hoje: ${new Date().toISOString().slice(0, 10)}

Como trabalhar:
- Responda em português do Brasil, de forma curta e direta.
- Antes de afirmar qualquer número, consulte a API com a ferramenta consultar_dama. Nunca invente dados.
- Você só lê dados; não consegue registrar, editar nem apagar nada. Se pedirem uma ação, explique em que tela da
  plataforma o usuário faz isso.
- Se a API devolver 403, diga que o perfil do usuário não tem acesso àquela informação.
- Ao falar de pessoas, use o nome (não só o id). Formate números no padrão brasileiro.
${ENDPOINTS}`;
}

async function chamarDama(apiBase, autorizacao, caminho) {
  if (typeof caminho !== 'string' || !caminho.startsWith('/') || caminho.startsWith('//') || caminho.includes('..')) {
    return { erro: 'Caminho inválido: use um caminho relativo começando com "/".' };
  }
  const resposta = await fetch(`${apiBase}/api${caminho}`, {
    headers: { Accept: 'application/json', Authorization: autorizacao }
  });
  const texto = await resposta.text();
  return { status: resposta.status, corpo: texto.length > MAX_RESULTADO ? texto.slice(0, MAX_RESULTADO) + '…[cortado]' : texto };
}

async function chamarClaude(sistema, mensagens) {
  const resposta = await fetch('https://api.anthropic.com/v1/messages', {
    method: 'POST',
    headers: {
      'content-type': 'application/json',
      'x-api-key': ANTHROPIC_API_KEY,
      'anthropic-version': '2023-06-01'
    },
    body: JSON.stringify({ model: MODELO, max_tokens: 2048, system: sistema, tools: FERRAMENTAS, messages: mensagens })
  });
  const dados = await resposta.json().catch(() => ({}));
  if (!resposta.ok) {
    const erro = new Error(dados?.error?.message || `Anthropic respondeu ${resposta.status}`);
    erro.status = resposta.status;
    throw erro;
  }
  return dados;
}

function limparHistorico(historico) {
  if (!Array.isArray(historico)) return [];
  const limpas = historico
    .filter(m => m && ['user', 'assistant'].includes(m.papel) && typeof m.texto === 'string' && m.texto.trim())
    .slice(-MAX_HISTORICO)
    .map(m => ({ role: m.papel, content: m.texto.slice(0, 8000) }));
  while (limpas.length && limpas[0].role !== 'user') limpas.shift();
  return limpas;
}

function registrarRotas(app, apiBase) {
  app.get('/agente/status', (_req, res) => {
    res.json({ configurado: Boolean(ANTHROPIC_API_KEY), modelo: MODELO });
  });

  // Corpo: { historico: [{ papel: 'user'|'assistant', texto }] } — a última mensagem é a pergunta do usuário.
  app.post('/agente/chat', async (req, res) => {
    if (!ANTHROPIC_API_KEY) {
      return res.status(503).json({ message: 'Assistente sem chave configurada. Defina ANTHROPIC_API_KEY no .env e reinicie o front.' });
    }
    const autorizacao = req.get('Authorization');
    if (!autorizacao) return res.status(401).json({ message: 'Sessão inválida. Entre novamente.' });

    const mensagens = limparHistorico(req.body?.historico);
    if (!mensagens.length || mensagens[mensagens.length - 1].role !== 'user') {
      return res.status(400).json({ message: 'Envie uma pergunta.' });
    }

    try {
      const me = await chamarDama(apiBase, autorizacao, '/me');
      if (me.status !== 200) return res.status(me.status === 403 ? 403 : 401).json({ message: 'Sessão inválida. Entre novamente.' });
      const sistema = promptSistema(JSON.parse(me.corpo));
      const consultas = [];

      for (let passo = 0; passo < MAX_PASSOS; passo++) {
        const resposta = await chamarClaude(sistema, mensagens);
        mensagens.push({ role: 'assistant', content: resposta.content });

        const usos = resposta.content.filter(b => b.type === 'tool_use');
        if (resposta.stop_reason !== 'tool_use' || !usos.length) {
          const texto = resposta.content.filter(b => b.type === 'text').map(b => b.text).join('\n').trim();
          return res.json({ texto: texto || '(sem resposta)', consultas });
        }

        const resultados = [];
        for (const uso of usos) {
          let resultado;
          try {
            resultado = await chamarDama(apiBase, autorizacao, uso.input?.caminho);
          } catch (err) {
            resultado = { erro: `Backend indisponível: ${err.message}` };
          }
          consultas.push({ caminho: uso.input?.caminho, status: resultado.status ?? 0 });
          resultados.push({ type: 'tool_result', tool_use_id: uso.id, content: JSON.stringify(resultado) });
        }
        mensagens.push({ role: 'user', content: resultados });
      }
      res.json({ texto: 'Precisei de consultas demais para responder. Tente uma pergunta mais específica.', consultas });
    } catch (err) {
      console.error(`[agente] ${err.message}`);
      res.status(502).json({
        message: err.status === 401 ? 'Chave da Anthropic inválida. Confira ANTHROPIC_API_KEY.' : `Falha no assistente: ${err.message}`
      });
    }
  });
}

module.exports = { registrarRotas };
