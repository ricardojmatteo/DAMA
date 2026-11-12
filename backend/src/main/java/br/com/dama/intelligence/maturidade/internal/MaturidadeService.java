package br.com.dama.intelligence.maturidade.internal;

import br.com.dama.intelligence.auditoria.api.AuditoriaFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorFacade;
import br.com.dama.intelligence.colaborador.api.ColaboradorView;
import br.com.dama.intelligence.maturidade.api.*;
import br.com.dama.intelligence.organizacao.api.OrganizacaoFacade;
import br.com.dama.intelligence.shared.error.NotFoundException;
import br.com.dama.intelligence.shared.error.ValidationException;
import br.com.dama.intelligence.shared.security.EscopoEmpresa;
import br.com.dama.intelligence.shared.security.UsuarioAtual;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
class MaturidadeService implements MaturidadeFacade {

    private final MaturidadeRepository repo;
    private final MaturidadeConfig config;
    private final ColaboradorFacade colaboradores;
    private final OrganizacaoFacade organizacao;
    private final AuditoriaFacade auditoria;
    private final EscopoEmpresa escopo;
    private final Clock clock;

    MaturidadeService(MaturidadeRepository repo, MaturidadeConfig config, ColaboradorFacade colaboradores,
                      OrganizacaoFacade organizacao, AuditoriaFacade auditoria, EscopoEmpresa escopo, Clock clock) {
        this.repo = repo;
        this.config = config;
        this.colaboradores = colaboradores;
        this.organizacao = organizacao;
        this.auditoria = auditoria;
        this.escopo = escopo;
        this.clock = clock;
    }

    @Override
    public AmsEmpresaView ams(Long empresaId) {
        if (!organizacao.existeEmpresa(empresaId)) {
            throw new NotFoundException("Empresa inexistente: " + empresaId);
        }
        escopo.exigir(empresaId);
        LocalDate fim = LocalDate.now(clock);
        LocalDate inicio = fim.minusDays(config.janelaDias() - 1L);
        LocalDate capacitadosDesde = fim.minusMonths(config.capacitacaoValidadeMeses());

        List<AmsDepartamentoView> departamentos = MaturidadeRegras.calcular(
                repo.dadosPorDepartamento(empresaId, inicio, fim, capacitadosDesde, config.tokensPorPonto(), config.franquiaGratis()),
                config.pesos());
        double ams = MaturidadeRegras.consolidar(departamentos);

        MaturidadeRegras.Pesos p = config.pesos();
        Map<String, BigDecimal> pesos = new LinkedHashMap<>();
        pesos.put("adocao", BigDecimal.valueOf(p.adocao()));
        pesos.put("capacitacao", BigDecimal.valueOf(p.capacitacao()));
        pesos.put("governanca", BigDecimal.valueOf(p.governanca()));
        pesos.put("produtividade", BigDecimal.valueOf(p.produtividade()));
        pesos.put("eficiencia", BigDecimal.valueOf(p.eficiencia()));

        return new AmsEmpresaView(empresaId, inicio, fim, pesos, MaturidadeRegras.arredondar(ams), MaturidadeRegras.faixa(ams), departamentos);
    }

    @Override
    public List<CapacitacaoView> capacitacoesDoColaborador(Long colaboradorId) {
        escopo.exigir(colaboradores.buscarPorId(colaboradorId).empresaId()); // 404 se não existir, 403 se de outra empresa
        return repo.capacitacoesDoColaborador(colaboradorId);
    }

    @Override
    @Transactional
    public CapacitacaoView registrarCapacitacao(Long colaboradorId, RegistrarCapacitacaoCommand comando) {
        ColaboradorView colaborador = colaboradores.buscarPorId(colaboradorId);
        escopo.exigir(colaborador.empresaId());
        if (!colaborador.ativo()) {
            throw new ValidationException("O colaborador está inativo.");
        }
        CapacitacaoView criada = repo.inserirCapacitacao(colaboradorId, comando.nome().trim(), comando.cargaHoraria(),
                comando.concluidaEm(), UsuarioAtual.id());
        auditoria.registrar(colaborador.empresaId(), UsuarioAtual.id(), "capacitacao", criada.capacitacaoId().toString(), "CRIACAO", null);
        return criada;
    }
}
