package br.com.dama.intelligence.orcamento.internal;

import br.com.dama.intelligence.orcamento.api.OrcamentoIaView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/** dama.orcamento_ia. */
@Repository
class OrcamentoRepository {

    private static final String SELECT =
            "select o.orcamento_id, o.empresa_id, o.departamento_id, d.nome as departamento_nome, o.mes_referencia, o.valor_orcado, " +
                    "o.gasto_atual, o.consumo_previsto, o.previsto_inferior, o.previsto_superior, o.origem_previsao, o.atualizado_em " +
                    "from dama.orcamento_ia o join dama.departamento d on d.departamento_id = o.departamento_id ";

    private final JdbcTemplate jdbc;

    OrcamentoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    List<OrcamentoIaView> listar(Long empresaId, LocalDate mes) {
        return jdbc.query(SELECT + "where o.empresa_id = ? and o.mes_referencia = ? order by d.nome",
                OrcamentoRepository::mapear, empresaId, Date.valueOf(mes));
    }

    OrcamentoIaView salvar(Long empresaId, Long departamentoId, LocalDate mes, BigDecimal orcado, BigDecimal gasto,
                           BigDecimal previsto, BigDecimal inferior, BigDecimal superior, String origem) {
        Long id = jdbc.queryForObject(
                "insert into dama.orcamento_ia (empresa_id, departamento_id, mes_referencia, valor_orcado, gasto_atual, " +
                        "consumo_previsto, previsto_inferior, previsto_superior, origem_previsao) values (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "on conflict (departamento_id, mes_referencia) do update set valor_orcado = excluded.valor_orcado, " +
                        "gasto_atual = excluded.gasto_atual, consumo_previsto = excluded.consumo_previsto, " +
                        "previsto_inferior = excluded.previsto_inferior, previsto_superior = excluded.previsto_superior, " +
                        "origem_previsao = excluded.origem_previsao, atualizado_em = now() returning orcamento_id",
                Long.class, empresaId, departamentoId, Date.valueOf(mes), orcado, gasto, previsto, inferior, superior, origem);
        return jdbc.queryForObject(SELECT + "where o.orcamento_id = ?", OrcamentoRepository::mapear, id);
    }

    /** situacao e projeção calculadas na leitura: nunca armazenadas (ADR-0005). */
    private static OrcamentoIaView mapear(java.sql.ResultSet rs, int i) throws java.sql.SQLException {
        BigDecimal orcado = rs.getBigDecimal("valor_orcado");
        BigDecimal gasto = rs.getBigDecimal("gasto_atual");
        BigDecimal previsto = rs.getBigDecimal("consumo_previsto");
        BigDecimal superior = rs.getBigDecimal("previsto_superior");
        BigDecimal projetado = gasto.add(previsto == null ? BigDecimal.ZERO : previsto);
        BigDecimal percentual = orcado.signum() == 0 ? null
                : projetado.multiply(BigDecimal.valueOf(100)).divide(orcado, 1, RoundingMode.HALF_UP);
        String situacao;
        if (projetado.compareTo(orcado) > 0) {
            situacao = "ACIMA";
        } else if (superior != null && gasto.add(superior).compareTo(orcado) > 0) {
            situacao = "RISCO";
        } else {
            situacao = "DENTRO";
        }
        return new OrcamentoIaView(rs.getLong("orcamento_id"), rs.getLong("empresa_id"), rs.getLong("departamento_id"),
                rs.getString("departamento_nome"), rs.getDate("mes_referencia").toLocalDate(), orcado, gasto, previsto,
                rs.getBigDecimal("previsto_inferior"), superior, projetado, percentual, situacao,
                rs.getString("origem_previsao"), rs.getTimestamp("atualizado_em").toLocalDateTime());
    }
}
