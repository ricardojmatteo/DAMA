-- =====================================================================
-- Views de apoio ao módulo analytics (RF13, RF14, RF15, RF36, RF37).
-- Diferença essencial em relação à versão anterior: tudo aqui é
-- calculado DENTRO de uma empresa (por departamento/colaborador), nunca
-- comparando uma empresa-cliente com outra.
-- =====================================================================
SET search_path = dama, public;

-- RF33/RF36 — indicadores da equipe: valor mais recente por indicador/departamento
-- (DROP antes do CREATE porque a lista de colunas mudou; mantém o script reexecutável)
DROP VIEW IF EXISTS vw_indicador_departamento_atual;
CREATE VIEW vw_indicador_departamento_atual AS
SELECT iv.departamento_id,
       d.nome AS departamento_nome,
       i.empresa_id,
       i.indicador_id,
       i.nome AS indicador_nome,
       i.unidade,
       iv.periodo_referencia,
       iv.valor
FROM indicador_valor iv
JOIN indicador i ON i.indicador_id = iv.indicador_id
JOIN departamento d ON d.departamento_id = iv.departamento_id
JOIN (
    SELECT departamento_id, indicador_id, MAX(periodo_referencia) AS ultimo_periodo
    FROM indicador_valor
    WHERE departamento_id IS NOT NULL
    GROUP BY departamento_id, indicador_id
) ult ON ult.departamento_id = iv.departamento_id
     AND ult.indicador_id = iv.indicador_id
     AND ult.ultimo_periodo = iv.periodo_referencia
WHERE iv.departamento_id IS NOT NULL;

-- RF35 — evolução de um indicador ao longo do tempo, por colaborador ou equipe
CREATE OR REPLACE VIEW vw_indicador_evolucao AS
SELECT iv.indicador_id,
       i.empresa_id,
       iv.colaborador_id,
       iv.departamento_id,
       iv.periodo_referencia,
       iv.valor
FROM indicador_valor iv
JOIN indicador i ON i.indicador_id = iv.indicador_id
ORDER BY iv.periodo_referencia;

-- RF08/RF28/RF14/RF36 — os rankings de colaborador e de equipe deixaram de ser views: dependem de uma janela
-- de tempo e do uso de IA, então são calculados em AnalyticsService/RankingRegras (docs/requisitos/ranking.md).
-- Os DROPs mantêm o script reexecutável em bancos criados com a versão anterior.
DROP VIEW IF EXISTS vw_ranking_colaborador;
DROP VIEW IF EXISTS vw_ranking_departamento;

-- RF13 — visão consolidada de desempenho de uma empresa.
-- Cada total vem de uma subconsulta própria: juntar colaborador, departamento,
-- meta e pontuacao no mesmo FROM multiplicava a soma de pontos (POAN-118).
DROP VIEW IF EXISTS vw_visao_consolidada_empresa;
CREATE VIEW vw_visao_consolidada_empresa AS
SELECT e.empresa_id,
       (SELECT COUNT(*) FROM colaborador c
         WHERE c.empresa_id = e.empresa_id AND c.ativo)                          AS colaboradores_ativos,
       (SELECT COUNT(*) FROM departamento d
         WHERE d.empresa_id = e.empresa_id)                                      AS total_departamentos,
       (SELECT COUNT(*) FROM meta m
         WHERE m.empresa_id = e.empresa_id AND m.status = 'Em andamento')        AS metas_em_andamento,
       (SELECT COUNT(*) FROM meta m
         WHERE m.empresa_id = e.empresa_id AND m.status = 'Concluida')           AS metas_concluidas,
       (SELECT COALESCE(SUM(p.pontos), 0) FROM pontuacao p
          JOIN colaborador c ON c.colaborador_id = p.colaborador_id
         WHERE c.empresa_id = e.empresa_id)                                      AS pontos_totais_periodo
FROM empresa e;

-- RF15 — saldo de AI Credits por colaborador (nunca armazenado, sempre derivado)
CREATE OR REPLACE VIEW vw_ai_credits_saldo AS
SELECT colaborador_id,
       SUM(CASE WHEN tipo = 'CREDITO' THEN quantidade ELSE -quantidade END) AS saldo_atual
FROM ai_credit_transacao
GROUP BY colaborador_id;
