import { api } from '../api.js';
import { sessao } from '../sessao.js';
import { pessoasAtivas } from '../dados.js';
import { grafico, serie } from '../graficos.js';
import { h, fmt, painel, stat, badge, tabela, botao, formulario, opcoes, toast, hojeIso } from '../ui.js';

const FAIXA = {
    ADOCAO_BAIXA: ['Adoção baixa', 'perigo'],
    EM_DESENVOLVIMENTO: ['Em desenvolvimento', 'alerta'],
    AVANCADO: ['Avançado', 'sucesso']
};
const DIMENSOES = [
    ['adocao', 'Adoção', 'Colaboradores ativos com uso de IA registrado na janela'],
    ['capacitacao', 'Capacitação', 'Colaboradores com treinamento em IA nos últimos 12 meses'],
    ['governanca', 'Governança', 'Tarefas dentro do limite de tokens e sem retrabalho'],
    ['produtividade', 'Produtividade', 'Story points por colaborador, relativo ao melhor departamento'],
    ['eficiencia', 'Eficiência', 'Quanto do uso de IA ficou abaixo do limite que gera penalidade']
];

export default async function (el, params, recarregar) {
    const ams = await api.get(`/empresas/${sessao.empresaId}/ams`);
    const deps = ams.departamentos;
    const melhor = [...deps].sort((a, b) => b.ams - a.ams)[0];
    const pior = [...deps].filter(d => d.colaboradoresAtivos > 0).sort((a, b) => a.ams - b.ams)[0];

    el.append(h('div', { class: 'grade cards' },
        stat('AI Maturity Score da empresa', fmt.num(ams.ams), FAIXA[ams.faixa][0]),
        stat('Departamento mais maduro', melhor?.departamentoNome || '—', melhor ? `AMS ${fmt.num(melhor.ams)}` : null),
        stat('Onde começar', pior?.departamentoNome || '—', pior ? `AMS ${fmt.num(pior.ams)} · ${FAIXA[pior.faixa][0].toLowerCase()}` : null),
        stat('Janela analisada', `${fmt.data(ams.janelaInicio)} a ${fmt.data(ams.janelaFim)}`, 'tarefas e atividades')));

    const area = h('div', { class: 'grafico' });
    el.append(painel('Dimensões por departamento', area, {
        subtitulo: 'Cada dimensão vai de 0 a 100. O AMS é a média ponderada: ' +
            DIMENSOES.map(([chave, rotulo]) => `${rotulo} ${fmt.num(ams.pesos[chave])}%`).join(', ') + '.'
    }));
    grafico(area, 'bar', {
        labels: deps.map(d => d.departamentoNome),
        datasets: DIMENSOES.map(([chave, rotulo], i) => serie(rotulo, deps.map(d => Number(d[chave])), i))
    }, { scales: { y: { min: 0, max: 100 } } });

    el.append(painel('AI Maturity Score por departamento', tabela([
        { titulo: 'Departamento', valor: d => d.departamentoNome },
        { titulo: 'Ativos', classe: 'num', valor: d => fmt.num(d.colaboradoresAtivos) },
        ...DIMENSOES.map(([chave, rotulo]) => ({ titulo: rotulo, classe: 'num', valor: d => fmt.num(d[chave]) })),
        { titulo: 'AMS', classe: 'num', valor: d => h('span', { class: 'forte' }, fmt.num(d.ams)) },
        { titulo: 'Faixa', valor: d => badge(FAIXA[d.faixa][0], FAIXA[d.faixa][1]) },
        { titulo: 'O que fazer', valor: d => h('span', { class: 'muted' }, d.leitura) }
    ], deps, { vazioTexto: 'Nenhum departamento cadastrado.' }), {
        subtitulo: 'Faixas: 0 a 39 adoção baixa, 40 a 69 em desenvolvimento, 70 a 100 avançado (proposta do PO, a calibrar com dados reais).'
    }));

    el.append(painel('De onde vêm os números', tabela([
        { titulo: 'Departamento', valor: d => d.departamentoNome },
        { titulo: 'Com uso de IA', classe: 'num', valor: d => `${fmt.num(d.evidencias.comUsoDeIa)} de ${fmt.num(d.colaboradoresAtivos)}` },
        { titulo: 'Capacitados', classe: 'num', valor: d => `${fmt.num(d.evidencias.capacitados)} de ${fmt.num(d.colaboradoresAtivos)}` },
        { titulo: 'Tarefas conformes', classe: 'num', valor: d => `${fmt.num(d.evidencias.tarefasConformes)} de ${fmt.num(d.evidencias.tarefasEntregues)}` },
        { titulo: 'Story points por pessoa', classe: 'num', valor: d => fmt.num(d.evidencias.storyPointsPorColaborador) },
        { titulo: 'Penalidade média', classe: 'num', valor: d => d.evidencias.fracaoMediaPenalizada == null ? '—' : `${fmt.num(d.evidencias.fracaoMediaPenalizada * 100)}%` }
    ], deps), {
        subtitulo: DIMENSOES.map(([, rotulo, descricao]) => `${rotulo}: ${descricao.toLowerCase()}`).join('. ') + '.',
        acoes: sessao.pode('DESEMPENHO_ESCREVER') ? botao('Registrar capacitação', () => registrarCapacitacao(recarregar)) : null
    }));
}

async function registrarCapacitacao(recarregar) {
    formulario('Registrar capacitação em IA', [
        { nome: 'colaboradorId', rotulo: 'Colaborador', tipo: 'select', obrigatorio: true, opcoes: opcoes(await pessoasAtivas(), p => p.colaboradorId, p => p.nome) },
        { nome: 'nome', rotulo: 'Treinamento', obrigatorio: true, ajuda: 'Ex.: Política de uso responsável de IA' },
        { nome: 'cargaHoraria', rotulo: 'Carga horária (h)', tipo: 'number' },
        { nome: 'concluidaEm', rotulo: 'Concluído em', tipo: 'date', obrigatorio: true, valor: hojeIso() }
    ], async v => {
        await api.post(`/colaboradores/${v.colaboradorId}/capacitacoes`, { nome: v.nome, cargaHoraria: v.cargaHoraria, concluidaEm: v.concluidaEm });
        toast('Capacitação registrada.');
        recarregar();
    }, { textoBotao: 'Registrar' });
}
