package br.com.dama.intelligence.pontuacao.internal;

import br.com.dama.intelligence.pontuacao.api.BonusGestorView;
import br.com.dama.intelligence.pontuacao.api.PontuacaoView;
import br.com.dama.intelligence.pontuacao.api.RegraPontuacaoView;
import br.com.dama.intelligence.pontuacao.api.TarefaEntregueView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
class PontuacaoRepository {

    private static final String REGRA_COLS = "regra_id, empresa_id, atividade_id, nome, pontos, ativa";
    private static final String PONTUACAO_COLS = "pontuacao_id, colaborador_id, regra_id, pontos, descricao, registrado_em";

    private final JdbcTemplate jdbc;

    PontuacaoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    RegraPontuacaoView inserirRegra(Long empresaId, Long atividadeId, String nome, BigDecimal pontos) {
        return jdbc.queryForObject(
                "insert into dama.regra_pontuacao (empresa_id, atividade_id, nome, pontos) values (?, ?, ?, ?) returning " + REGRA_COLS,
                PontuacaoRepository::mapearRegra, empresaId, atividadeId, nome, pontos);
    }

    List<RegraPontuacaoView> listarRegras(Long empresaId) {
        return jdbc.query("select " + REGRA_COLS + " from dama.regra_pontuacao where empresa_id = ? order by nome",
                PontuacaoRepository::mapearRegra, empresaId);
    }

    Optional<RegraPontuacaoView> buscarRegra(Long regraId) {
        return jdbc.query("select " + REGRA_COLS + " from dama.regra_pontuacao where regra_id = ?",
                PontuacaoRepository::mapearRegra, regraId).stream().findFirst();
    }

    void atualizarRegraAtiva(Long regraId, boolean ativa) {
        jdbc.update("update dama.regra_pontuacao set ativa = ? where regra_id = ?", ativa, regraId);
    }

    PontuacaoView inserirPontuacao(Long colaboradorId, Long regraId, BigDecimal pontos, String descricao) {
        return jdbc.queryForObject(
                "insert into dama.pontuacao (colaborador_id, regra_id, pontos, descricao) values (?, ?, ?, ?) returning " + PONTUACAO_COLS,
                PontuacaoRepository::mapearPontuacao, colaboradorId, regraId, pontos, descricao);
    }

    List<PontuacaoView> historico(Long colaboradorId) {
        return jdbc.query("select " + PONTUACAO_COLS + " from dama.pontuacao where colaborador_id = ? " +
                        "order by registrado_em desc, pontuacao_id desc",
                PontuacaoRepository::mapearPontuacao, colaboradorId);
    }

    private static final String TAREFA_COLS =
            "tarefa_id, colaborador_id, chave, descricao, story_points, urgencia, tokens_ia, entregue_em";
    private static final String BONUS_COLS = "bonus_id, colaborador_id, concedido_por, pontos, motivo, registrado_em";

    TarefaEntregueView inserirTarefa(Long colaboradorId, String chave, String descricao, BigDecimal storyPoints,
                                     String urgencia, long tokensIa, LocalDateTime entregueEm) {
        return jdbc.queryForObject(
                "insert into dama.tarefa_entregue (colaborador_id, chave, descricao, story_points, urgencia, tokens_ia, entregue_em) " +
                        "values (?, ?, ?, ?, ?, ?, coalesce(?, now())) returning " + TAREFA_COLS,
                (rs, i) -> new TarefaEntregueView(rs.getLong("tarefa_id"), rs.getLong("colaborador_id"), rs.getString("chave"),
                        rs.getString("descricao"), rs.getBigDecimal("story_points"), rs.getString("urgencia"),
                        rs.getLong("tokens_ia"), rs.getTimestamp("entregue_em").toLocalDateTime()),
                colaboradorId, chave, descricao, storyPoints, urgencia, tokensIa,
                entregueEm == null ? null : Timestamp.valueOf(entregueEm));
    }

    List<TarefaEntregueView> listarTarefas(Long colaboradorId) {
        return jdbc.query("select " + TAREFA_COLS + " from dama.tarefa_entregue where colaborador_id = ? " +
                        "order by entregue_em desc, tarefa_id desc",
                (rs, i) -> new TarefaEntregueView(rs.getLong("tarefa_id"), rs.getLong("colaborador_id"), rs.getString("chave"),
                        rs.getString("descricao"), rs.getBigDecimal("story_points"), rs.getString("urgencia"),
                        rs.getLong("tokens_ia"), rs.getTimestamp("entregue_em").toLocalDateTime()),
                colaboradorId);
    }

    BonusGestorView inserirBonus(Long colaboradorId, Long concedidoPor, BigDecimal pontos, String motivo) {
        return jdbc.queryForObject(
                "insert into dama.bonus_gestor (colaborador_id, concedido_por, pontos, motivo) values (?, ?, ?, ?) returning " + BONUS_COLS,
                PontuacaoRepository::mapearBonus, colaboradorId, concedidoPor, pontos, motivo);
    }

    List<BonusGestorView> listarBonus(Long colaboradorId) {
        return jdbc.query("select " + BONUS_COLS + " from dama.bonus_gestor where colaborador_id = ? " +
                        "order by registrado_em desc, bonus_id desc",
                PontuacaoRepository::mapearBonus, colaboradorId);
    }

    private static BonusGestorView mapearBonus(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new BonusGestorView(rs.getLong("bonus_id"), rs.getLong("colaborador_id"), rs.getLong("concedido_por"),
                rs.getBigDecimal("pontos"), rs.getString("motivo"), rs.getTimestamp("registrado_em").toLocalDateTime());
    }

    BigDecimal total(Long colaboradorId) {
        BigDecimal total = jdbc.queryForObject(
                "select coalesce(sum(pontos), 0) from dama.pontuacao where colaborador_id = ?", BigDecimal.class, colaboradorId);
        return total == null ? BigDecimal.ZERO : total;
    }

    private static RegraPontuacaoView mapearRegra(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new RegraPontuacaoView(rs.getLong("regra_id"), rs.getLong("empresa_id"), rs.getObject("atividade_id", Long.class),
                rs.getString("nome"), rs.getBigDecimal("pontos"), rs.getBoolean("ativa"));
    }

    private static PontuacaoView mapearPontuacao(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new PontuacaoView(rs.getLong("pontuacao_id"), rs.getLong("colaborador_id"), rs.getObject("regra_id", Long.class),
                rs.getBigDecimal("pontos"), rs.getString("descricao"), rs.getTimestamp("registrado_em").toLocalDateTime());
    }
}
