// Assistente IA: chat com o agente que roda no BFF (frontend/agente.js).
// A conversa (sessão) fica guardada nesta aba do navegador até clicar em "Nova conversa" ou sair.

import { sessao } from '../sessao.js';
import { h, painel, botao } from '../ui.js';

const SUGESTOES = [
    'Qual é o meu saldo de AI Credits e quanto falta para o próximo nível de autonomia?',
    'Como estão minhas metas?',
    'Quais alertas de gestão merecem atenção agora?',
    'Quem está no topo do ranking dos últimos 30 dias?'
];

function chaveSessao() {
    return `dama.assistente.${sessao.id}`;
}

function carregarHistorico() {
    try {
        return JSON.parse(window.sessionStorage.getItem(chaveSessao())) || [];
    } catch {
        return [];
    }
}

function salvarHistorico(historico) {
    try {
        window.sessionStorage.setItem(chaveSessao(), JSON.stringify(historico));
    } catch {
        // sem storage: a conversa vale até recarregar a página
    }
}

async function postar(historico) {
    const resposta = await fetch('/agente/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', Accept: 'application/json', Authorization: 'Bearer ' + sessao.token },
        body: JSON.stringify({ historico })
    });
    const dados = await resposta.json().catch(() => ({}));
    if (!resposta.ok) throw new Error(dados.message || `Erro ${resposta.status}`);
    return dados;
}

export default async function (el) {
    let historico = carregarHistorico();
    let ocupado = false;

    const status = await fetch('/agente/status').then(r => r.json()).catch(() => ({ configurado: false }));

    const mensagens = h('div', { class: 'chat-mensagens', 'aria-live': 'polite' });
    const campo = h('textarea', { class: 'chat-campo', rows: '2', placeholder: 'Pergunte sobre suas metas, AI Credits, ranking…', 'aria-label': 'Mensagem' });
    const enviar = h('button', { type: 'submit', class: 'btn primario' }, 'Enviar');

    function bolha(m) {
        return h('div', { class: `chat-msg ${m.papel === 'user' ? 'eu' : 'agente'}${m.erro ? ' erro' : ''}` },
            h('div', { class: 'chat-texto' }, m.texto),
            m.consultas && m.consultas.length > 0 && h('details', { class: 'chat-consultas' },
                h('summary', {}, `${m.consultas.length} consulta(s) à plataforma`),
                h('ul', {}, m.consultas.map(c => h('li', {}, h('code', {}, c.caminho), ` → ${c.status}`)))));
    }

    function desenhar() {
        if (historico.length === 0) {
            mensagens.replaceChildren(h('div', { class: 'chat-vazio' },
                h('p', { class: 'muted' }, 'Olá! Sou o Assistente DAMA. Consulto seus dados na plataforma (só leitura, com as permissões do seu perfil) para responder.'),
                h('div', { class: 'chat-sugestoes' }, SUGESTOES.map(s =>
                    h('button', { type: 'button', class: 'btn pequeno', onclick: () => perguntar(s) }, s)))));
        } else {
            mensagens.replaceChildren(...historico.map(bolha));
        }
        if (ocupado) mensagens.append(h('div', { class: 'chat-msg agente digitando' }, h('div', { class: 'chat-texto' }, 'Consultando a plataforma…')));
        mensagens.scrollTop = mensagens.scrollHeight;
    }

    async function perguntar(texto) {
        texto = texto.trim();
        if (!texto || ocupado) return;
        historico.push({ papel: 'user', texto });
        campo.value = '';
        ocupado = true;
        enviar.disabled = true;
        desenhar();
        try {
            const enviados = historico.filter(m => !m.erro).map(({ papel, texto: t }) => ({ papel, texto: t }));
            const r = await postar(enviados);
            historico.push({ papel: 'assistant', texto: r.texto, consultas: r.consultas });
        } catch (e) {
            historico.push({ papel: 'assistant', texto: e.message, erro: true });
        } finally {
            ocupado = false;
            enviar.disabled = false;
            salvarHistorico(historico);
            desenhar();
            campo.focus();
        }
    }

    campo.addEventListener('keydown', e => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            perguntar(campo.value);
        }
    });

    const form = h('form', { class: 'chat-form', onsubmit: e => { e.preventDefault(); perguntar(campo.value); } }, campo, enviar);

    el.append(painel('Assistente DAMA', [
        !status.configurado && h('div', { class: 'chat-aviso' },
            'O assistente ainda não tem chave de API. Defina ANTHROPIC_API_KEY no arquivo .env e reinicie o front.'),
        mensagens,
        form
    ], {
        subtitulo: 'Agente de IA que lê seus dados na plataforma. Não registra nem altera nada.',
        acoes: botao('Nova conversa', () => {
            historico = [];
            salvarHistorico(historico);
            desenhar();
        })
    }));
    desenhar();
    campo.focus();
}
