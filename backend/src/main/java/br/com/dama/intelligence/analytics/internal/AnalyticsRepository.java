package br.com.dama.intelligence.analytics.internal;

import br.com.dama.intelligence.analytics.api.IndicadorDepartamentoView;
import br.com.dama.intelligence.analytics.api.VisaoConsolidadaView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Lê exclusivamente das views de database/02_views.sql — nenhuma tabela é acessada diretamente. */
@Repository
class AnalyticsRepository {

    private final JdbcTemplate jdbc;

    AnalyticsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    boolean existeEmpresa(Long empresaId) {
        Integer n = jdbc.queryForObject("select count(*) from dama.empresa where empresa_id = ?", Integer.class, empresaId);
        return n != null && n > 0;
    }

    Optional<VisaoConsolidadaView> visaoConsolidada(Long empresaId) {
        return jdbc.query(
                "select empresa_id, colaboradores_ativos, total_departamentos, metas_em_andamento, " +
                        "metas_concluidas, pontos_totais_periodo from dama.vw_visao_consolidada_empresa where empresa_id = ?",
                (rs, i) -> new VisaoConsolidadaView(
                        rs.getLong("empresa_id"), rs.getLong("colaboradores_ativos"), rs.getLong("total_departamentos"),
                        rs.getLong("metas_em_andamento"), rs.getLong("metas_concluidas"), rs.getBigDecimal("pontos_totais_periodo")),
                empresaId).stream().findFirst();
    }

    List<RankingRegras.Departamento> departamentos(Long empresaId) {
        return jdbc.query("select departamento_id, nome from dama.departamento where empresa_id = ? order by nome",
                (rs, i) -> new RankingRegras.Departamento(rs.getLong("departamento_id"), rs.getString("nome")),
                empresaId);
    }

    List<RankingRegras.Colaborador> colaboradoresAtivos(Long empresaId) {
        return jdbc.query("select colaborador_id, nome, departamento_id from dama.colaborador where empresa_id = ? and ativo " +
                        "order by colaborador_id",
                (rs, i) -> new RankingRegras.Colaborador(rs.getLong("colaborador_id"), rs.getString("nome"),
                        rs.getLong("departamento_id")),
                empresaId);
    }

    /** Tarefas entregues pelos colaboradores ativos da empresa entre o início do dia inicio e o fim do dia fim. */
    List<RankingRegras.Tarefa> tarefasEntregues(Long empresaId, LocalDate inicio, LocalDate fim) {
        return jdbc.query(
                "select t.colaborador_id, t.story_points, t.urgencia, t.tokens_ia from dama.tarefa_entregue t " +
                        "join dama.colaborador c on c.colaborador_id = t.colaborador_id " +
                        "where c.empresa_id = ? and c.ativo and t.entregue_em >= ? and t.entregue_em < ?",
                (rs, i) -> new RankingRegras.Tarefa(rs.getLong("colaborador_id"), rs.getBigDecimal("story_points"),
                        rs.getString("urgencia"), rs.getLong("tokens_ia")),
                empresaId, Timestamp.valueOf(inicio.atStartOfDay()), Timestamp.valueOf(fim.plusDays(1).atStartOfDay()));
    }

    /** Bônus do gestor da janela, somados por colaborador. */
    List<RankingRegras.Bonus> bonusDoGestor(Long empresaId, LocalDate inicio, LocalDate fim) {
        return jdbc.query(
                "select b.colaborador_id, sum(b.pontos) as pontos from dama.bonus_gestor b " +
                        "join dama.colaborador c on c.colaborador_id = b.colaborador_id " +
                        "where c.empresa_id = ? and c.ativo and b.registrado_em >= ? and b.registrado_em < ? " +
                        "group by b.colaborador_id",
                (rs, i) -> new RankingRegras.Bonus(rs.getLong("colaborador_id"), rs.getBigDecimal("pontos")),
                empresaId, Timestamp.valueOf(inicio.atStartOfDay()), Timestamp.valueOf(fim.plusDays(1).atStartOfDay()));
    }

    List<AnaliseGestaoRegras.MetaAberta> metasAbertas(Long empresaId) {
        return jdbc.query(
                "select m.meta_id, m.descricao, m.colaborador_id, c.nome as colaborador_nome, m.departamento_id, " +
                        "d.nome as departamento_nome, m.valor_alvo, m.data_inicio, m.data_fim, " +
                        "coalesce(i.maior_melhor, true) as maior_melhor, " +
                        "(select p.valor_atual from dama.meta_progresso p where p.meta_id = m.meta_id " +
                        " order by p.registrado_em desc, p.meta_progresso_id desc limit 1) as progresso_atual " +
                        "from dama.meta m " +
                        "left join dama.colaborador c on c.colaborador_id = m.colaborador_id " +
                        "left join dama.departamento d on d.departamento_id = m.departamento_id " +
                        "left join dama.indicador i on i.indicador_id = m.indicador_id " +
                        "where m.empresa_id = ? and m.status in ('Em andamento', 'Atrasada') " +
                        "and (m.colaborador_id is null or c.ativo) " +
                        "order by m.data_fim",
                (rs, i) -> new AnaliseGestaoRegras.MetaAberta(
                        rs.getLong("meta_id"), rs.getString("descricao"),
                        rs.getObject("colaborador_id", Long.class), rs.getString("colaborador_nome"),
                        rs.getObject("departamento_id", Long.class), rs.getString("departamento_nome"),
                        rs.getBigDecimal("valor_alvo"), rs.getBigDecimal("progresso_atual"),
                        rs.getDate("data_inicio").toLocalDate(), rs.getDate("data_fim").toLocalDate(),
                        rs.getBoolean("maior_melhor")),
                empresaId);
    }

    /** Para cada série (indicador + colaborador ativo ou departamento), os dois valores mais recentes, do mais novo ao mais antigo. */
    List<AnaliseGestaoRegras.ValorIndicador> ultimosDoisValoresPorSerie(Long empresaId) {
        return jdbc.query(
                "select * from (" +
                        " select iv.indicador_id, i.nome as indicador_nome, i.unidade, i.maior_melhor, " +
                        "  iv.colaborador_id, c.nome as colaborador_nome, iv.departamento_id, d.nome as departamento_nome, " +
                        "  iv.periodo_referencia, iv.valor, " +
                        "  row_number() over (partition by iv.indicador_id, iv.colaborador_id, iv.departamento_id " +
                        "                     order by iv.periodo_referencia desc, iv.indicador_valor_id desc) as rn " +
                        " from dama.indicador_valor iv " +
                        " join dama.indicador i on i.indicador_id = iv.indicador_id " +
                        " left join dama.colaborador c on c.colaborador_id = iv.colaborador_id " +
                        " left join dama.departamento d on d.departamento_id = iv.departamento_id " +
                        " where i.empresa_id = ? and i.ativo and (iv.colaborador_id is null or c.ativo)" +
                        ") serie where rn <= 2 " +
                        "order by indicador_id, colaborador_id nulls last, departamento_id nulls last, rn",
                (rs, i) -> new AnaliseGestaoRegras.ValorIndicador(
                        rs.getLong("indicador_id"), rs.getString("indicador_nome"), rs.getString("unidade"),
                        rs.getBoolean("maior_melhor"),
                        rs.getObject("colaborador_id", Long.class), rs.getString("colaborador_nome"),
                        rs.getObject("departamento_id", Long.class), rs.getString("departamento_nome"),
                        rs.getDate("periodo_referencia").toLocalDate(), rs.getBigDecimal("valor")),
                empresaId);
    }

    /** Ativos admitidos até a data desde, sem pontos nem atividades registradas a partir dela. */
    List<AnaliseGestaoRegras.ColaboradorSemAtividade> colaboradoresSemAtividade(Long empresaId, LocalDate desde) {
        return jdbc.query(
                "select c.colaborador_id, c.nome, c.departamento_id from dama.colaborador c " +
                        "where c.empresa_id = ? and c.ativo and c.data_admissao <= ? " +
                        "and not exists (select 1 from dama.pontuacao p " +
                        "  where p.colaborador_id = c.colaborador_id and p.registrado_em >= ?) " +
                        "and not exists (select 1 from dama.atividade_registro a " +
                        "  where a.colaborador_id = c.colaborador_id and a.registrado_em >= ?) " +
                        "order by c.nome",
                (rs, i) -> new AnaliseGestaoRegras.ColaboradorSemAtividade(rs.getLong("colaborador_id"),
                        rs.getString("nome"), rs.getLong("departamento_id")),
                empresaId, desde, desde, desde);
    }

    List<IndicadorDepartamentoView> indicadoresPorDepartamento(Long empresaId, Long indicadorId) {
        String sql = "select departamento_id, departamento_nome, indicador_id, indicador_nome, unidade, " +
                "periodo_referencia, valor from dama.vw_indicador_departamento_atual where empresa_id = ?";
        org.springframework.jdbc.core.RowMapper<IndicadorDepartamentoView> mapper = (rs, i) -> new IndicadorDepartamentoView(
                rs.getLong("departamento_id"), rs.getString("departamento_nome"), rs.getLong("indicador_id"),
                rs.getString("indicador_nome"), rs.getString("unidade"),
                rs.getDate("periodo_referencia").toLocalDate(), rs.getBigDecimal("valor"));
        if (indicadorId == null) {
            return jdbc.query(sql + " order by indicador_nome, valor desc", mapper, empresaId);
        }
        return jdbc.query(sql + " and indicador_id = ? order by valor desc", mapper, empresaId, indicadorId);
    }
}
