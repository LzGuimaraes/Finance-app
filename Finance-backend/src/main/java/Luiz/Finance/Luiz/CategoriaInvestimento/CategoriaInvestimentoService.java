package Luiz.Finance.Luiz.CategoriaInvestimento;

import Luiz.Finance.Luiz.CategoriaInvestimento.dto.CategoriaInvestimentoDTOs.*;
import Luiz.Finance.Luiz.CategoriaInvestimento.dto.CategoriaInvestimentoMapper;
import Luiz.Finance.Luiz.CategoriaInvestimento.exception.CategoriaJaExisteException;
import Luiz.Finance.Luiz.CategoriaInvestimento.exception.CategoriaNaoEncontradaException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaInvestimentoService {

    private final CategoriaInvestimentoRepository categoriaRepository;
    private final CategoriaInvestimentoMapper categoriaMapper;

    // ── Listagem ───────────────────────────────────────────────────
    // Catálogo global: qualquer usuário autenticado vê todas as categorias.
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    // ── Busca por id ──────────────────────────────────────────────────
    public CategoriaResponse buscarPorId(Integer id) {
        return categoriaMapper.toResponse(buscarOuFalhar(id));
    }

    // ── Criação (somente ADMIN — ver @PreAuthorize no controller) ──────
    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        if (categoriaRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new CategoriaJaExisteException(request.nome());
        }

        CategoriaInvestimentoModel categoria = CategoriaInvestimentoModel.builder()
                .nome(request.nome())
                .build();

        try {
            categoriaRepository.save(categoria);
        } catch (DataIntegrityViolationException ex) {
            // Corrida entre o existsBy() acima e o save(): duas requisições
            // simultâneas com o mesmo nome podem colidir só na constraint
            // UNIQUE(nome) do banco.
            throw new CategoriaJaExisteException(request.nome());
        }

        return categoriaMapper.toResponse(categoria);
    }

    // ── Edição (somente ADMIN) ──────────────────────────────────────────
    @Transactional
    public CategoriaResponse atualizar(Integer id, CategoriaRequest request) {
        CategoriaInvestimentoModel categoria = buscarOuFalhar(id);

        boolean nomeMudou = !categoria.getNome().equalsIgnoreCase(request.nome());
        if (nomeMudou && categoriaRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new CategoriaJaExisteException(request.nome());
        }

        categoria.setNome(request.nome());
        categoriaRepository.save(categoria);
        return categoriaMapper.toResponse(categoria);
    }

    // ── Remoção (somente ADMIN; cascade apaga subcategorias) ─────────────
    @Transactional
    public void deletar(Integer id) {
        CategoriaInvestimentoModel categoria = buscarOuFalhar(id);
        categoriaRepository.delete(categoria);
    }

    // ── Helper interno ──────────────────────────────────────────────────
    // Visibilidade public: reaproveitado pelo SubcategoriaInvestimentoService
    // para validar que a categoria pai existe antes de operar nas subcategorias.
    public CategoriaInvestimentoModel buscarOuFalhar(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(CategoriaNaoEncontradaException::new);
    }
}