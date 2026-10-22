import { h, select, opcoes } from '../ui.js';
import { sessao } from '../sessao.js';
import { pessoasAtivas, departamentos } from '../dados.js';

/** Percentual atingido de uma meta, considerando indicadores em que menor é melhor. */
export function percentualMeta(meta, maiorMelhor = true) {
    if (meta.status === 'Concluida') return 100;
    if (meta.progressoAtual === null || meta.progressoAtual === undefined) return 0;
    const atual = Number(meta.progressoAtual);
    const alvo = Number(meta.valorAlvo);
    if (maiorMelhor) return alvo > 0 ? (atual / alvo) * 100 : 0;
    return atual > 0 ? Math.min(100, (alvo / atual) * 100) : 100;
}

/** Opções "Pessoa — Nome" (c:id) e "Equipe — Nome" (d:id) para campos de responsável. */
export async function opcoesResponsavel({ incluirVazio } = {}) {
    const [pessoas, equipes] = await Promise.all([pessoasAtivas(), departamentos()]);
    const itens = incluirVazio ? [{ valor: '', rotulo: incluirVazio }] : [];
    for (const d of equipes) itens.push({ valor: `d:${d.departamentoId}`, rotulo: `Equipe — ${d.nome}` });
    for (const p of pessoas) itens.push({ valor: `c:${p.colaboradorId}`, rotulo: `Pessoa — ${p.nome}` });
    return itens;
}

export function lerResponsavel(valor) {
    if (!valor) return { colaboradorId: null, departamentoId: null };
    const [tipo, id] = valor.split(':');
    return tipo === 'c'
        ? { colaboradorId: Number(id), departamentoId: null }
        : { colaboradorId: null, departamentoId: Number(id) };
}

export async function opcoesPessoas(vazioRotulo) {
    return opcoes(await pessoasAtivas(), p => p.colaboradorId, p => p.nome, { vazioRotulo });
}

/**
 * Seletor "ver dados de" para quem pode consultar outras pessoas; para os demais, fica fixo no próprio usuário.
 * Retorna { elemento, id } — elemento é null quando não há escolha a fazer.
 */
export async function seletorPessoa(params, aoMudar, permissao = 'COLABORADOR_LER') {
    const id = Number(params.get('colaborador')) || sessao.id;
    if (!sessao.pode(permissao)) return { elemento: null, id: sessao.id };
    const itens = await opcoesPessoas();
    const elemento = h('div', { class: 'linha' },
        h('span', { class: 'muted' }, 'Ver dados de'),
        select(itens, { valor: id, aoMudar }));
    return { elemento, id };
}

// ---------- janela de tempo do ranking ----------

const PERIODOS = [
    { valor: '7', rotulo: 'Últimos 7 dias' },
    { valor: '30', rotulo: 'Últimos 30 dias' },
    { valor: '90', rotulo: 'Últimos 90 dias' },
    { valor: '365', rotulo: 'Último ano' },
    { valor: 'todo', rotulo: 'Todo o período' }
];

function iso(data) {
    const p = n => String(n).padStart(2, '0');
    return `${data.getFullYear()}-${p(data.getMonth() + 1)}-${p(data.getDate())}`;
}

/** Período escolhido na URL (?dias=7|30|90|365|todo; padrão 30) e a query string que o backend espera. */
export function periodoRanking(params) {
    const dias = PERIODOS.some(p => p.valor === params.get('dias')) ? params.get('dias') : '30';
    const hoje = new Date();
    const inicio = new Date(hoje);
    if (dias === 'todo') inicio.setFullYear(2000, 0, 1);
    else inicio.setDate(hoje.getDate() - (Number(dias) - 1));
    return { dias, consulta: `inicio=${iso(inicio)}&fim=${iso(hoje)}`, rotulo: PERIODOS.find(p => p.valor === dias).rotulo };
}

export function seletorPeriodo(dias, aoMudar) {
    return select(PERIODOS, { valor: dias, aoMudar });
}
