// Usuário autenticado e suas permissões. O token JWT (ADR-0010) vai em Authorization: Bearer em cada chamada (ver api.js).

const CHAVE_TOKEN = 'dama.token';

function lerSalvo() {
    try {
        return window.localStorage.getItem(CHAVE_TOKEN) || null;
    } catch {
        return null;
    }
}

export const sessao = {
    token: lerSalvo(),
    id: null,
    colaborador: null,
    perfis: [],
    permissoes: new Set(),
    administraPlataforma: false,

    get empresaId() {
        return this.colaborador?.empresaId;
    },

    pode(...permissoes) {
        return permissoes.some(p => this.permissoes.has(p));
    },

    /** Guarda o token recebido no login; os dados do usuário vêm depois, de GET /me (ver definir). */
    autenticar(token) {
        this.token = token;
        try {
            window.localStorage.setItem(CHAVE_TOKEN, token);
        } catch {
            // sem storage (aba anônima etc.): a sessão vale só até recarregar a página
        }
    },

    definir(dados) {
        this.id = dados.colaborador.colaboradorId;
        this.colaborador = dados.colaborador;
        this.perfis = dados.perfis;
        this.permissoes = new Set(dados.permissoes);
        this.administraPlataforma = Boolean(dados.administraPlataforma);
    },

    limpar() {
        this.token = null;
        this.id = null;
        this.colaborador = null;
        this.perfis = [];
        this.permissoes = new Set();
        this.administraPlataforma = false;
        try {
            window.localStorage.removeItem(CHAVE_TOKEN);
            // conversa do Assistente IA não sobrevive à saída
            Object.keys(window.sessionStorage).filter(k => k.startsWith('dama.assistente.')).forEach(k => window.sessionStorage.removeItem(k));
        } catch {
            // ignorado
        }
    }
};
