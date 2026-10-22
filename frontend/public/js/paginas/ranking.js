import { api } from '../api.js';
import { sessao } from '../sessao.js';
import { nomes, pessoasAtivas } from '../dados.js';
import { grafico } from '../graficos.js';
import { h, fmt, painel, tabela, badge, botao, formulario, opcoes, toast, stat } from '../ui.js';
import { periodoRanking, seletorPeriodo } from './comum.js';
import { navegar } from '../app.js';

export default async function (el, params, recarregar) {
    const e = sessao.empresaId;
    const periodo = periodoRanking(params);
    const escreve = sessao.pode('PONTOS_LANCAR');   // só o Administrador lança pontos e mantém regras
    const [ranking, minhaPontuacao, regras, n] = await Promise.all([
        api.get(`/empresas/${e}/analytics/ranking-colaboradores?${periodo.consulta}`),
        sessao.pode('GAMIFICACAO_LER') ? api.get(`/colaboradores/${sessao.id}/pontuacao`) : Promise.resolve(null),
        api.get(`/empresas/${e}/regras-pontuacao`).catch(() => []),
        nomes()
    ]);
    const eu = ranking.find(r => r.colaboradorId === sessao.id);

    el.append(h('div', { class: 'grade cards' },
        stat('Minha posição', eu ? `${eu.posicao}º` : '—', `de ${ranking.length} colaboradores`),
        stat('Minha pontuação', fmt.num(eu?.pontuacao), eu ? `nota ${fmt.num(eu.nota)} de 10 · ${periodo.rotulo.toLowerCase()}` : periodo.rotulo),
        stat('Líder', ranking[0]?.nome || '—', ranking[0] ? `${fmt.num(ranking[0].pontuacao)} de pontuação` : null)));

    const areaGrafico = h('div', { class: 'grafico' });
    el.append(h('div', { class: 'grade duas' },
        painel('Ranking de colaboradores', tabela([
            { titulo: '#', valor: r => r.posicao <= 3 ? badge(`${r.posicao}º`, 'acento') : `${r.posicao}º` },
            { titulo: 'Colaborador', valor: r => h('span', { class: r.colaboradorId === sessao.id ? 'forte' : '' }, r.nome) },
            { titulo: 'Equipe', valor: r => n.departamento(r.departamentoId) },
            { titulo: 'Tarefas', classe: 'num', valor: r => fmt.num(r.tarefasEntregues) },
            { titulo: 'Pontos', classe: 'num', valor: r => fmt.num(r.pontosTarefas) },
            { titulo: 'Uso de IA', classe: 'num', valor: r => Number(r.penalidadeIa) > 0 ? badge(`-${fmt.num(r.penalidadeIa)}`, 'perigo') : '—' },
            { titulo: 'Bônus gestor', classe: 'num', valor: r => Number(r.bonusGestor) > 0 ? badge(`+${fmt.num(r.bonusGestor)}`, 'sucesso') : '—' },
            { titulo: 'Pontuação', classe: 'num', valor: r => h('span', { class: 'forte' }, fmt.num(r.pontuacao)) },
            { titulo: 'Nota', classe: 'num', valor: r => fmt.num(r.nota) }
        ], ranking, { destacar: r => r.colaboradorId === sessao.id }), {
            subtitulo: 'Pontuação = pontos das tarefas entregues (story points × urgência) − uso de IA acima da franquia grátis + bônus do gestor (até 15%). Nota de 0 a 10: 10 é a maior pontuação da empresa.',
            acoes: h('div', { class: 'linha' },
                seletorPeriodo(periodo.dias, v => navegar('ranking', { dias: v })),
                escreve ? botao('Lançar pontos', () => lancar(regras, recarregar), { tipo: 'primario' }) : null)
        }),
        painel('Top 10', areaGrafico)));

    const top = ranking.slice(0, 10);
    grafico(areaGrafico, 'bar', {
        labels: top.map(r => r.nome.split(' ')[0]),
        datasets: [{
            label: 'Pontuação', data: top.map(r => Number(r.pontuacao)),
            backgroundColor: top.map(r => r.colaboradorId === sessao.id ? '#ffa726' : '#ff3b00cc'), borderRadius: 6
        }]
    }, { indexAxis: 'y', plugins: { legend: { display: false } } });

    if (minhaPontuacao) {
        el.append(painel('Meu histórico de pontos', tabela([
            { titulo: 'Data', valor: p => fmt.dataHora(p.registradoEm) },
            { titulo: 'Descrição', valor: p => p.descricao },
            { titulo: 'Pontos', classe: 'num', valor: p => '+' + fmt.num(p.pontos) }
        ], minhaPontuacao.historico, { vazioTexto: 'Nenhum ponto lançado ainda.' })));
    }

    el.append(painel('Regras de pontuação', tabela([
        { titulo: 'Regra', valor: r => r.nome },
        { titulo: 'Pontos', classe: 'num', valor: r => fmt.num(r.pontos) },
        { titulo: 'Situação', valor: r => badge(r.ativa ? 'Ativa' : 'Inativa', r.ativa ? 'sucesso' : '') },
        escreve && {
            titulo: '', valor: r => botao(r.ativa ? 'Desativar' : 'Ativar', async () => {
                try {
                    await api.patch(`/regras-pontuacao/${r.regraId}/ativa`, { ativa: !r.ativa });
                    recarregar();
                } catch (err) {
                    toast(err.message, 'erro');
                }
            }, { pequeno: true })
        }
    ].filter(Boolean), regras, { vazioTexto: 'Nenhuma regra cadastrada.' }), {
        subtitulo: 'Pontos lançados por regra alimentam níveis e conquistas; o ranking usa as tarefas entregues. Só podem ser lançados a partir de uma regra ativa',
        acoes: escreve ? botao('Nova regra', () => novaRegra(recarregar)) : null
    }));
}

async function lancar(regras, recarregar) {
    const ativas = regras.filter(r => r.ativa);
    formulario('Lançar pontos', [
        { nome: 'colaboradorId', rotulo: 'Colaborador', tipo: 'select', obrigatorio: true, opcoes: opcoes(await pessoasAtivas(), p => p.colaboradorId, p => p.nome) },
        { nome: 'regraId', rotulo: 'Regra', tipo: 'select', obrigatorio: true, opcoes: opcoes(ativas, r => r.regraId, r => `${r.nome} (+${fmt.num(r.pontos)})`) },
        { nome: 'descricao', rotulo: 'Descrição', inteiro: true, ajuda: 'Opcional; se vazio, usa o nome da regra.' }
    ], async v => {
        await api.post(`/colaboradores/${v.colaboradorId}/pontuacao`, { regraId: Number(v.regraId), descricao: v.descricao });
        toast('Pontos lançados.');
        recarregar();
    }, { textoBotao: 'Lançar' });
}

function novaRegra(recarregar) {
    formulario('Nova regra de pontuação', [
        { nome: 'nome', rotulo: 'Nome', obrigatorio: true },
        { nome: 'pontos', rotulo: 'Pontos', tipo: 'number', obrigatorio: true }
    ], async v => {
        await api.post(`/empresas/${sessao.empresaId}/regras-pontuacao`, v);
        toast('Regra criada.');
        recarregar();
    });
}
