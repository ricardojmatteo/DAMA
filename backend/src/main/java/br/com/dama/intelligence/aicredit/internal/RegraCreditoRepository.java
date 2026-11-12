package br.com.dama.intelligence.aicredit.internal;

import br.com.dama.intelligence.aicredit.api.RegraCreditoView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/** dama.ai_credit_regra. */
@Repository
class RegraCreditoRepository {

    private static final String REGRA_COLS =
            "regra_id, empresa_id, atividade_id, nome, criterio_elegibilidade, creditos_concedidos, ativa";

    private final JdbcTemplate jdbc;

    RegraCreditoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    RegraCreditoView inserirRegra(Long empresaId, Long atividadeId, String nome, String criterio, BigDecimal creditos) {
        return jdbc.queryForObject(
                "insert into dama.ai_credit_regra (empresa_id, atividade_id, nome, criterio_elegibilidade, creditos_concedidos) " +
                        "values (?, ?, ?, ?, ?) returning " + REGRA_COLS,
                RegraCreditoRepository::mapearRegra, empresaId, atividadeId, nome, criterio, creditos);
    }

    List<RegraCreditoView> listarRegras(Long empresaId) {
        return jdbc.query("select " + REGRA_COLS + " from dama.ai_credit_regra where empresa_id = ? order by nome",
                RegraCreditoRepository::mapearRegra, empresaId);
    }

    List<RegraCreditoView> regrasAtivasDaAtividade(Long atividadeId) {
        return jdbc.query("select " + REGRA_COLS + " from dama.ai_credit_regra where atividade_id = ? and ativa order by regra_id",
                RegraCreditoRepository::mapearRegra, atividadeId);
    }

    private static RegraCreditoView mapearRegra(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new RegraCreditoView(rs.getLong("regra_id"), rs.getLong("empresa_id"), rs.getObject("atividade_id", Long.class),
                rs.getString("nome"), rs.getString("criterio_elegibilidade"), rs.getBigDecimal("creditos_concedidos"),
                rs.getBoolean("ativa"));
    }
}
