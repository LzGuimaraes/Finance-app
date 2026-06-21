package Luiz.Finance.Luiz.Ativo;

import Luiz.Finance.Luiz.Ativo.dto.AtivoDTOs.*;
import Luiz.Finance.Luiz.Ativo.dto.AtivoMapper;
import Luiz.Finance.Luiz.Ativo.exception.AtivoJaExisteException;
import Luiz.Finance.Luiz.Ativo.exception.AtivoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AtivoService {

    private final AtivoRepository ativoRepository;
    private final AtivoMapper ativoMapper;

    // ── Listagem ───────────────────────────────────────────────────
    // Catálogo global: qualquer usuário autenticado vê todos os ativos.
    public List<AtivoResponse> listar() {
        return ativoRepository.findAll().stream()
                .map(ativoMapper::toResponse)
                .toList();
    }

    // ── Busca por id ──────────────────────────────────────────────
    public AtivoResponse buscarPorId(Integer id) {
        return ativoMapper.toResponse(buscarOuFalhar(id));
    }

    // ── Busca por ticker (issue #40) ───────────────────────────────
    public AtivoResponse buscarPorTicker(String ticker) {
        AtivoModel ativo = ativoRepository.findByTickerIgnoreCase(ticker)
                .orElseThrow(AtivoNaoEncontradoException::new);
        return ativoMapper.toResponse(ativo);
    }

    // ── Criação (somente ADMIN — ver @PreAuthorize no controller) ──
    @Transactional
    public AtivoResponse criar(AtivoRequest request) {
        if (ativoRepository.existsByTickerIgnoreCase(request.ticker())) {
            throw new AtivoJaExisteException(request.ticker());
        }

        AtivoModel ativo = AtivoModel.builder()
                .ticker(request.ticker())
                .nome(request.nome())
                .tipo(request.tipo())
                .cotacaoAtual(request.cotacaoAtual() != null ? request.cotacaoAtual() : BigDecimal.ZERO)
                .dataAtualizacao(OffsetDateTime.now())
                .build();

        try {
            ativoRepository.save(ativo);
        } catch (DataIntegrityViolationException ex) {
            // Corrida entre o existsBy() acima e o save(): duas requisições
            // simultâneas com o mesmo ticker podem colidir só na constraint
            // UNIQUE(ticker) do banco.
            throw new AtivoJaExisteException(request.ticker());
        }

        return ativoMapper.toResponse(ativo);
    }

    // ── Edição (somente ADMIN) ──────────────────────────────────────
    @Transactional
    public AtivoResponse atualizar(Integer id, AtivoRequest request) {
        AtivoModel ativo = buscarOuFalhar(id);

        boolean tickerMudou = !ativo.getTicker().equalsIgnoreCase(request.ticker());
        if (tickerMudou && ativoRepository.existsByTickerIgnoreCase(request.ticker())) {
            throw new AtivoJaExisteException(request.ticker());
        }

        ativo.setTicker(request.ticker());
        ativo.setNome(request.nome());
        ativo.setTipo(request.tipo());

        if (request.cotacaoAtual() != null) {
            ativo.setCotacaoAtual(request.cotacaoAtual());
            ativo.setDataAtualizacao(OffsetDateTime.now());
        }

        ativoRepository.save(ativo);
        return ativoMapper.toResponse(ativo);
    }

    // ── Atualização de cotação (RF010 / issue #44) ──────────────────
    // Endpoint dedicado, separado da edição completa, pensando em uma
    // futura atualização automática (scheduler) que só altera o preço.
    @Transactional
    public AtivoResponse atualizarCotacao(Integer id, AtivoCotacaoRequest request) {
        AtivoModel ativo = buscarOuFalhar(id);
        ativo.setCotacaoAtual(request.cotacaoAtual());
        ativo.setDataAtualizacao(OffsetDateTime.now());
        ativoRepository.save(ativo);
        return ativoMapper.toResponse(ativo);
    }

    // ── Remoção (somente ADMIN) ──────────────────────────────────────
    // Nota: ativos têm FK RESTRICT em carteira_ativos — a exclusão falhará
    // (DataIntegrityViolationException, tratada pelo GlobalExceptionHandler)
    // se o ativo estiver presente em alguma carteira.
    @Transactional
    public void deletar(Integer id) {
        AtivoModel ativo = buscarOuFalhar(id);
        ativoRepository.delete(ativo);
    }

    // ── Helper interno ───────────────────────────────────────────────
    private AtivoModel buscarOuFalhar(Integer id) {
        return ativoRepository.findById(id)
                .orElseThrow(AtivoNaoEncontradoException::new);
    }
}