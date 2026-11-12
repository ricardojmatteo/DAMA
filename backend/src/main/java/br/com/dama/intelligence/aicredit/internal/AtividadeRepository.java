package br.com.dama.intelligence.aicredit.internal;

import br.com.dama.intelligence.aicredit.api.AtividadeProdutivaView;
import br.com.dama.intelligence.aicredit.api.AtividadeRegistroView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** dama.atividade_produtiva e dama.atividade_registro. */
@Repository
class AtividadeRepository {

    private static final String ATIVIDADE_COLS = "atividade_id, empresa_id, nome, descricao, ativa";
    private static final String REGISTRO_COLS = "registro_id, atividade_id, colaborador_id, quantidade, registrado_em";

    private final JdbcTemplate jdbc;

    AtividadeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    AtividadeProdutivaView inserirAtividade(Long empresaId, String nome, String descricao) {
        return jdbc.queryForObject(
                "insert into dama.atividade_produtiva (empresa_id, nome, descricao) values (?, ?, ?) returning " + ATIVIDADE_COLS,
                AtividadeRepository::mapearAtividade, empresaId, nome, descricao);
    }

    List<AtividadeProdutivaView> listarAtividades(Long empresaId) {
        return jdbc.query("select " + ATIVIDADE_COLS + " from dama.atividade_produtiva where empresa_id = ? order by nome",
                AtividadeRepository::mapearAtividade, empresaId);
    }

    Optional<AtividadeProdutivaView> buscarAtividade(Long atividadeId) {
        return jdbc.query("select " + ATIVIDADE_COLS + " from dama.atividade_produtiva where atividade_id = ?",
                AtividadeRepository::mapearAtividade, atividadeId).stream().findFirst();
    }

    AtividadeRegistroView inserirRegistro(Long atividadeId, Long colaboradorId, BigDecimal quantidade) {
        return jdbc.queryForObject(
                "insert into dama.atividade_registro (atividade_id, colaborador_id, quantidade) values (?, ?, ?) returning " + REGISTRO_COLS,
                AtividadeRepository::mapearRegistro, atividadeId, colaboradorId, quantidade);
    }

    List<AtividadeRegistroView> listarRegistros(Long colaboradorId) {
        return jdbc.query("select " + REGISTRO_COLS + " from dama.atividade_registro where colaborador_id = ? " +
                        "order by registrado_em desc, registro_id desc",
                AtividadeRepository::mapearRegistro, colaboradorId);
    }

    private static AtividadeProdutivaView mapearAtividade(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new AtividadeProdutivaView(rs.getLong("atividade_id"), rs.getLong("empresa_id"),
                rs.getString("nome"), rs.getString("descricao"), rs.getBoolean("ativa"));
    }

    private static AtividadeRegistroView mapearRegistro(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new AtividadeRegistroView(rs.getLong("registro_id"), rs.getLong("atividade_id"), rs.getLong("colaborador_id"),
                rs.getBigDecimal("quantidade"), rs.getTimestamp("registrado_em").toLocalDateTime());
    }
}
