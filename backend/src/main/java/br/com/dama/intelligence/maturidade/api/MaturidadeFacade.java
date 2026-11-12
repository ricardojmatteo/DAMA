package br.com.dama.intelligence.maturidade.api;

import java.util.List;

/** AI Maturity Score (RF40/RF41, docs/produto/ams.md) e capacitações em IA que alimentam uma de suas dimensões. */
public interface MaturidadeFacade {

    /** AMS da empresa e de cada departamento, calculado na leitura a partir dos dados do produto. */
    AmsEmpresaView ams(Long empresaId);

    List<CapacitacaoView> capacitacoesDoColaborador(Long colaboradorId);

    CapacitacaoView registrarCapacitacao(Long colaboradorId, RegistrarCapacitacaoCommand comando);
}
