package br.com.dama.intelligence.maturidade.internal;

import br.com.dama.intelligence.maturidade.api.CapacitacaoView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

/**
 * Leituras agregadas para o AMS (sobre colaborador, atividade_registro, tarefa_entregue e capacitacao) e escrita em
 * dama.capacitacao. Como o módulo analytics, é um módulo de leitura que agrega dados de outros módulos (ADR-0012).
 */
@Repository
class MaturidadeRepository {

    private final JdbcTemplate jdbc;

    MaturidadeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Uma linha por departamento da empresa. A fração penalizada de cada tarefa segue a mesma fórmula do ranking:
     * clamp((tokens ÷ (story points × tokensPorPonto) − franquia) ÷ (1 − franquia), 0, 1).
     */
    List<MaturidadeRegras.Dados> dadosPorDepartamento(Long empresaId, LocalDate inicio, LocalDate fim,
                                                      LocalDate capacitadosDesde, double tokensPorPonto, double franquia) {
        Timestamp de = Timestamp.valueOf(inicio.atStartOfDay());
        Timestamp ate = Timestamp.valueOf(fim.plusDays(1).atStartOfDay());
        String sql =
                "with ativos as (" +
                "  select c.colaborador_id, c.departamento_id from dama.colaborador c where c.empresa_id = ? and c.ativo), " +
                "uso as (" +
                "  select a.colaborador_id from ativos a where exists (" +
                "      select 1 from dama.atividade_registro r where r.colaborador_id = a.colaborador_id and r.registrado_em >= ? and r.registrado_em < ?)" +
                "    or exists (" +
                "      select 1 from dama.tarefa_entregue t where t.colaborador_id = a.colaborador_id and t.tokens_ia > 0 and t.entregue_em >= ? and t.entregue_em < ?)), " +
                "capacitados as (" +
                "  select distinct k.colaborador_id from dama.capacitacao k join ativos a on a.colaborador_id = k.colaborador_id " +
                "  where k.concluida_em >= ?), " +
                "tarefas as (" +
                "  select a.departamento_id, t.story_points, t.retrabalho, " +
                "         greatest(0, least(1, (t.tokens_ia / (t.story_points * ?) - ?) / (1 - ?))) as fracao, " +
                "         (t.tokens_ia <= t.story_points * ?) as dentro_do_limite " +
                "  from dama.tarefa_entregue t join ativos a on a.colaborador_id = t.colaborador_id " +
                "  where t.entregue_em >= ? and t.entregue_em < ?) " +
                "select d.departamento_id, d.nome, " +
                "  (select count(*) from ativos a where a.departamento_id = d.departamento_id) as ativos, " +
                "  (select count(*) from ativos a join uso u on u.colaborador_id = a.colaborador_id where a.departamento_id = d.departamento_id) as com_uso, " +
                "  (select count(*) from ativos a join capacitados k on k.colaborador_id = a.colaborador_id where a.departamento_id = d.departamento_id) as capacitados, " +
                "  (select count(*) from tarefas t where t.departamento_id = d.departamento_id) as tarefas, " +
                "  (select count(*) from tarefas t where t.departamento_id = d.departamento_id and t.dentro_do_limite and not t.retrabalho) as conformes, " +
                "  (select coalesce(sum(t.story_points), 0) from tarefas t where t.departamento_id = d.departamento_id) as story_points, " +
                "  (select coalesce(sum(t.fracao), 0) from tarefas t where t.departamento_id = d.departamento_id) as soma_fracao " +
                "from dama.departamento d where d.empresa_id = ? order by d.nome";
        return jdbc.query(sql,
                (rs, i) -> new MaturidadeRegras.Dados(rs.getLong("departamento_id"), rs.getString("nome"), rs.getLong("ativos"),
                        rs.getLong("com_uso"), rs.getLong("capacitados"), rs.getLong("tarefas"), rs.getLong("conformes"),
                        rs.getDouble("story_points"), rs.getDouble("soma_fracao")),
                empresaId, de, ate, de, ate, Date.valueOf(capacitadosDesde),
                tokensPorPonto, franquia, franquia, tokensPorPonto, de, ate, empresaId);
    }

    List<CapacitacaoView> capacitacoesDoColaborador(Long colaboradorId) {
        return jdbc.query("select capacitacao_id, colaborador_id, nome, carga_horaria, concluida_em, registrado_por " +
                        "from dama.capacitacao where colaborador_id = ? order by concluida_em desc, capacitacao_id desc",
                MaturidadeRepository::mapear, colaboradorId);
    }

    CapacitacaoView inserirCapacitacao(Long colaboradorId, String nome, BigDecimal cargaHoraria, LocalDate concluidaEm, Long registradoPor) {
        return jdbc.queryForObject("insert into dama.capacitacao (colaborador_id, nome, carga_horaria, concluida_em, registrado_por) " +
                        "values (?, ?, ?, ?, ?) returning capacitacao_id, colaborador_id, nome, carga_horaria, concluida_em, registrado_por",
                MaturidadeRepository::mapear, colaboradorId, nome, cargaHoraria, Date.valueOf(concluidaEm), registradoPor);
    }

    private static CapacitacaoView mapear(java.sql.ResultSet rs, int i) throws java.sql.SQLException {
        return new CapacitacaoView(rs.getLong("capacitacao_id"), rs.getLong("colaborador_id"), rs.getString("nome"),
                rs.getBigDecimal("carga_horaria"), rs.getDate("concluida_em").toLocalDate(), rs.getObject("registrado_por", Long.class));
    }
}
