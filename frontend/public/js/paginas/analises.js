import { api } from '../api.js';
import { sessao } from '../sessao.js';
import { h, fmt, painel, stat, badge, select, vazio, tabela, botao, formulario, opcoes, toast } from '../ui.js';
import { departamentos } from '../dados.js';
import { navegar } from '../app.js';

const TIPOS = {
    META_VENCIDA: 'Meta vencida',
    META_EM_RISCO: 'Meta em risco',
    INDICADOR_EM_QUEDA: 'Indicador em queda',
    EQUIPE_ABAIXO_DA_MEDIA: 'Equipe abaixo da média',
    SEM_ATIVIDADE_RECENTE: 'Sem atividade recente',
    ORCAMENTO_IA_EM_RISCO: 'Orçamento de IA em risco'
};
const SITUACAO = { DENTRO: ['Dentro do orçamento', 'sucesso'], RISCO: ['Pode estourar', 'alerta'], ACIMA: ['Acima do orçamento', 'perigo'] };
const SEVERIDADE = { ALTA: 'Alta', MEDIA: 'Média', BAIXA: 'Baixa' };

export default async function (el, params, recarregar) {
    const e = sessao.empresaId;
    const [visao, analise, orcamentos] = await Promise.all([
        api.get(`/empresas/${e}/analytics/visao-consolidada`),
        api.get(`/empresas/${e}/analytics/alertas`),
        api.get(`/empresas/${e}/orcamentos-ia`).catch(() => [])
    ]);
    const filtroTipo = params.get('tipo') || '';
    const filtroSeveridade = params.get('severidade') || '';
    const alertas = analise.alertas.filter(a => (!filtroTipo || a.tipo === filtroTipo) && (!filtroSeveridade || a.severidade === filtroSeveridade));

    el.append(h('div', { class: 'grade cards' },
        stat('Colaboradores ativos', fmt.num(visao.colaboradoresAtivos), `${fmt.num(visao.totalDepartamentos)} equipes`),
        stat('Metas em andamento', fmt.num(visao.metasEmAndamento), `${fmt.num(visao.metasConcluidas)} concluídas`),
        stat('Pontos na empresa', fmt.num(visao.pontosTotaisPeriodo)),
        stat('Alertas de prioridade alta', fmt.num(analise.totalPorSeveridade.ALTA),
            `${fmt.num(analise.totalPorSeveridade.MEDIA)} média · ${fmt.num(analise.totalPorSeveridade.BAIXA)} baixa`)));

    const filtrar = (chave, valor) => {
        const novo = Object.fromEntries(params);
        if (valor) novo[chave] = valor; else delete novo[chave];
        navegar('analises', novo);
    };

    el.append(painel('Onde agir', alertas.length === 0 ? vazio('Nenhum ponto de atenção no momento.') :
        h('div', { class: 'lista', style: 'gap:10px' }, alertas.map(a => h('div', { class: 'alerta-item ' + a.severidade },
            h('div', { class: 'linha', style: 'justify-content:space-between' },
                h('div', { class: 'titulo' }, a.titulo),
                h('div', { class: 'linha' }, badge(TIPOS[a.tipo] || a.tipo), badge(SEVERIDADE[a.severidade], a.severidade === 'ALTA' ? 'perigo' : a.severidade === 'MEDIA' ? 'alerta' : 'info'))),
            h('div', { class: 'detalhe' }, a.detalhe),
            h('div', { class: 'recomendacao' }, a.recomendacao),
            h('div', { class: 'linha', style: 'margin-top:8px' }, links(a))))), {
        subtitulo: `Gerado em ${fmt.data(analise.dataReferencia)} a partir dos dados registrados na plataforma`,
        acoes: [
            select([{ valor: '', rotulo: 'Todas as severidades' }, ...Object.entries(SEVERIDADE).map(([valor, rotulo]) => ({ valor, rotulo }))],
                { valor: filtroSeveridade, aoMudar: v => filtrar('severidade', v) }),
            select([{ valor: '', rotulo: 'Todos os tipos' }, ...Object.entries(TIPOS).map(([valor, rotulo]) => ({ valor, rotulo }))],
                { valor: filtroTipo, aoMudar: v => filtrar('tipo', v) })
        ]
    }));

    const escreve = sessao.pode('DESEMPENHO_ESCREVER');
    el.append(painel('Orçamento de IA do mês por centro de custo', tabela([
        { titulo: 'Departamento', valor: o => o.departamentoNome },
        { titulo: 'Orçado', classe: 'num', valor: o => fmt.moeda(o.valorOrcado) },
        { titulo: 'Gasto até agora', classe: 'num', valor: o => fmt.moeda(o.gastoAtual) },
        { titulo: 'Consumo previsto', classe: 'num', valor: o => o.consumoPrevisto == null ? '—' : fmt.moeda(o.consumoPrevisto) },
        { titulo: 'Faixa de 80%', classe: 'num', valor: o => o.previstoInferior == null ? '—' : `${fmt.moeda(o.previstoInferior)} a ${fmt.moeda(o.previstoSuperior)}` },
        { titulo: 'Projeção', classe: 'num', valor: o => `${fmt.moeda(o.projetado)}${o.percentualProjetado != null ? ` (${fmt.num(o.percentualProjetado)}%)` : ''}` },
        { titulo: 'Situação', valor: o => badge(SITUACAO[o.situacao][0], SITUACAO[o.situacao][1]) }
    ], orcamentos, { vazioTexto: 'Nenhum orçamento de IA registrado para este mês.' }), {
        subtitulo: 'Gasto é retrato do mês (assentos e consumo faturados). Consumo previsto e faixa de 80% vêm do modelo de demanda por centro de custo; a projeção soma gasto e consumo previsto.',
        acoes: escreve ? botao('Registrar orçamento', () => registrarOrcamento(recarregar)) : null
    }));
}

async function registrarOrcamento(recarregar) {
    const hoje = new Date();
    const mes = `${hoje.getFullYear()}-${String(hoje.getMonth() + 1).padStart(2, '0')}-01`;
    formulario('Orçamento de IA do departamento', [
        { nome: 'departamentoId', rotulo: 'Departamento', tipo: 'select', obrigatorio: true, opcoes: opcoes(await departamentos(), d => d.departamentoId, d => d.nome) },
        { nome: 'mesReferencia', rotulo: 'Mês de referência', tipo: 'date', obrigatorio: true, valor: mes, ajuda: 'Qualquer dia do mês; o registro vale para o mês inteiro.' },
        { nome: 'valorOrcado', rotulo: 'Valor orçado (R$)', tipo: 'number', obrigatorio: true },
        { nome: 'gastoAtual', rotulo: 'Gasto até agora (R$)', tipo: 'number' },
        { nome: 'consumoPrevisto', rotulo: 'Consumo previsto até o fim do mês (R$)', tipo: 'number', ajuda: 'Saída do modelo de demanda. Opcional.' },
        { nome: 'previstoInferior', rotulo: 'Faixa de 80%: limite inferior (R$)', tipo: 'number' },
        { nome: 'previstoSuperior', rotulo: 'Faixa de 80%: limite superior (R$)', tipo: 'number' },
        { nome: 'origemPrevisao', rotulo: 'Origem da previsão', inteiro: true, ajuda: 'Ex.: previsao_demanda_custo.ipynb 2026-11-10' }
    ], async v => {
        await api.put(`/empresas/${sessao.empresaId}/orcamentos-ia`, { ...v, departamentoId: Number(v.departamentoId) });
        toast('Orçamento registrado.');
        recarregar();
    });
}

function links(alerta) {
    const itens = [];
    if (alerta.tipo.startsWith('META')) {
        const responsavel = alerta.colaboradorId ? `c:${alerta.colaboradorId}` : `d:${alerta.departamentoId}`;
        itens.push(h('a', { href: `#/metas?responsavel=${responsavel}` }, 'Ver metas'));
    }
    if (alerta.tipo === 'INDICADOR_EM_QUEDA') {
        const serie = alerta.colaboradorId ? `c:${alerta.colaboradorId}` : `d:${alerta.departamentoId}`;
        itens.push(h('a', { href: `#/indicadores?ind=${alerta.referenciaId}&serie=${serie}` }, 'Ver evolução do indicador'));
    }
    if (alerta.departamentoId) {
        itens.push(h('a', { href: `#/equipes?dep=${alerta.departamentoId}` }, 'Ver equipe'));
    }
    if (alerta.colaboradorId) {
        itens.push(h('a', { href: `#/inicio?colaborador=${alerta.colaboradorId}` }, 'Ver painel do colaborador'));
    }
    return itens.flatMap((link, i) => i === 0 ? [link] : [h('span', { class: 'muted' }, '·'), link]);
}
