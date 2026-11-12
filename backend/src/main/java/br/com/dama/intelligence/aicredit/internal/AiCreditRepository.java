package br.com.dama.intelligence.aicredit.internal;

import br.com.dama.intelligence.aicredit.api.NivelAutonomiaView;
import br.com.dama.intelligence.aicredit.api.TransacaoCreditoView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** dama.ai_credit_transacao e dama.ai_nivel_autonomia. */
@Repository
class AiCreditRepository {

    private static final String TRANSACAO_COLS =
            "transacao_id, colaborador_id, regra_id, registro_id, tipo, quantidade, motivo, registrado_em";
    private static final String NIVEL_COLS = "nivel_id, nome, creditos_minimos, ordem, descricao";

    private final JdbcTemplate jdbc;

    AiCreditRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Serializa operações de saldo de um mesmo colaborador até o fim da transação corrente,
     * evitando que dois débitos concorrentes ultrapassem o saldo disponível.
     */
    void bloquearSaldo(Long colaboradorId) {
        jdbc.query("select pg_advisory_xact_lock(?)", (RowCallbackHandler) rs -> { }, colaboradorId);
    }

    TransacaoCreditoView inserirTransacao(Long colaboradorId, Long regraId, Long registroId, String tipo,
                                          BigDecimal quantidade, String motivo) {
        return jdbc.queryForObject(
                "insert into dama.ai_credit_transacao (colaborador_id, regra_id, registro_id, tipo, quantidade, motivo) " +
                        "values (?, ?, ?, ?, ?, ?) returning " + TRANSACAO_COLS,
                AiCreditRepository::mapearTransacao, colaboradorId, regraId, registroId, tipo, quantidade, motivo);
    }

    List<TransacaoCreditoView> historico(Long colaboradorId) {
        return jdbc.query("select " + TRANSACAO_COLS + " from dama.ai_credit_transacao where colaborador_id = ? " +
                        "order by registrado_em desc, transacao_id desc",
                AiCreditRepository::mapearTransacao, colaboradorId);
    }

    BigDecimal saldo(Long colaboradorId) {
        BigDecimal saldo = jdbc.queryForObject(
                "select coalesce(sum(case when tipo = 'CREDITO' then quantidade else -quantidade end), 0) " +
                        "from dama.ai_credit_transacao where colaborador_id = ?",
                BigDecimal.class, colaboradorId);
        return saldo == null ? BigDecimal.ZERO : saldo;
    }

    NivelAutonomiaView inserirNivel(String nome, BigDecimal creditosMinimos, int ordem, String descricao) {
        return jdbc.queryForObject(
                "insert into dama.ai_nivel_autonomia (nome, creditos_minimos, ordem, descricao) values (?, ?, ?, ?) returning " + NIVEL_COLS,
                AiCreditRepository::mapearNivel, nome, creditosMinimos, ordem, descricao);
    }

    List<NivelAutonomiaView> listarNiveis() {
        return jdbc.query("select " + NIVEL_COLS + " from dama.ai_nivel_autonomia order by ordem", AiCreditRepository::mapearNivel);
    }

    /** Maior nível cujo saldo mínimo já foi atingido. */
    Optional<NivelAutonomiaView> nivelParaSaldo(BigDecimal saldo) {
        return jdbc.query("select " + NIVEL_COLS + " from dama.ai_nivel_autonomia where creditos_minimos <= ? order by ordem desc limit 1",
                AiCreditRepository::mapearNivel, saldo).stream().findFirst();
    }

    private static TransacaoCreditoView mapearTransacao(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new TransacaoCreditoView(rs.getLong("transacao_id"), rs.getLong("colaborador_id"),
                rs.getObject("regra_id", Long.class), rs.getObject("registro_id", Long.class),
                rs.getString("tipo"), rs.getBigDecimal("quantidade"), rs.getString("motivo"),
                rs.getTimestamp("registrado_em").toLocalDateTime());
    }

    private static NivelAutonomiaView mapearNivel(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new NivelAutonomiaView(rs.getLong("nivel_id"), rs.getString("nome"), rs.getBigDecimal("creditos_minimos"),
                rs.getInt("ordem"), rs.getString("descricao"));
    }
}
