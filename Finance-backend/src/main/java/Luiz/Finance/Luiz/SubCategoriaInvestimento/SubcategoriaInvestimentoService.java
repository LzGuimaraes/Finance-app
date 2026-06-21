package Luiz.Finance.Luiz.SubCategoriaInvestimento;

import Luiz.Finance.Luiz.CategoriaInvestimento.CategoriaInvestimentoModel;
import Luiz.Finance.Luiz.CategoriaInvestimento.CategoriaInvestimentoService;
import Luiz.Finance.Luiz.SubCategoriaInvestimento.dto.SubcategoriaInvestimentoDTOs.*;
import Luiz.Finance.Luiz.SubCategoriaInvestimento.dto.SubcategoriaInvestimentoMapper;
import Luiz.Finance.Luiz.SubCategoriaInvestimento.exception.SubcategoriaJaExisteException;
import Luiz.Finance.Luiz.SubCategoriaInvestimento.exception.SubcategoriaNaoEncontradaException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubcategoriaInvestimentoService {

    private final SubcategoriaInvestimentoRepository subcategoriaRepository;
    private final SubcategoriaInvestimentoMapper subcategoriaMapper;

    // Reaproveita a verificação de existência da categoria pai.
    private final CategoriaInvestimentoService categoriaService;

    // ── Listagem ───────────────────────────────────────────────────
    public List<SubcategoriaResponse> listar(Integer categoriaId) {
        categoriaService.buscarOuFalhar(categoriaId); // 404 se a categoria não existir

        return subcategoriaRepository.findAllByCategoriaId(categoriaId).stream()
                .map(subcategoriaMapper::toResponse)
                .toList();
    }

    // ── Busca por id ──────────────────────────────────────────────────
    public SubcategoriaResponse buscarPorId(Integer categoriaId, Integer id) {
        categoriaService.buscarOuFalhar(categoriaId);
        return subcategoriaMapper.toResponse(buscarNaCategoria(categoriaId, id));
    }

    // ── Criação (somente ADMIN) ───────────────────────────────────────
    @Transactional
    public SubcategoriaResponse criar(Integer categoriaId, SubcategoriaRequest request) {
        CategoriaInvestimentoModel categoria = categoriaService.buscarOuFalhar(categoriaId);

        if (subcategoriaRepository.existsByCategoriaIdAndNomeIgnoreCase(categoriaId, request.nome())) {
            throw new SubcategoriaJaExisteException(request.nome());
        }

        SubcategoriaInvestimentoModel subcategoria = SubcategoriaInvestimentoModel.builder()
                .nome(request.nome())
                .categoria(categoria)
                .build();

        try {
            subcategoriaRepository.save(subcategoria);
        } catch (DataIntegrityViolationException ex) {
            // Corrida na constraint UNIQUE(categoria_id, nome).
            throw new SubcategoriaJaExisteException(request.nome());
        }

        return subcategoriaMapper.toResponse(subcategoria);
    }

    // ── Edição (somente ADMIN) ────────────────────────────────────────────
    @Transactional
    public SubcategoriaResponse atualizar(Integer categoriaId, Integer id, SubcategoriaRequest request) {
        categoriaService.buscarOuFalhar(categoriaId);
        SubcategoriaInvestimentoModel subcategoria = buscarNaCategoria(categoriaId, id);

        boolean nomeMudou = !subcategoria.getNome().equalsIgnoreCase(request.nome());
        if (nomeMudou && subcategoriaRepository.existsByCategoriaIdAndNomeIgnoreCase(categoriaId, request.nome())) {
            throw new SubcategoriaJaExisteException(request.nome());
        }

        subcategoria.setNome(request.nome());
        subcategoriaRepository.save(subcategoria);
        return subcategoriaMapper.toResponse(subcategoria);
    }

    // ── Remoção (somente ADMIN) ─────────────────────────────────────────────
    @Transactional
    public void deletar(Integer categoriaId, Integer id) {
        categoriaService.buscarOuFalhar(categoriaId);
        SubcategoriaInvestimentoModel subcategoria = buscarNaCategoria(categoriaId, id);
        subcategoriaRepository.delete(subcategoria);
    }

    // ── Helper interno ───────────────────────────────────────────────────────
    // findByIdAndCategoriaId garante que a subcategoria realmente pertence
    // à categoria informada na URL — sem isso, o id da subcategoria sozinho
    // poderia "escapar" para fora do contexto nested.
    private SubcategoriaInvestimentoModel buscarNaCategoria(Integer categoriaId, Integer id) {
        return subcategoriaRepository.findByIdAndCategoriaId(id, categoriaId)
                .orElseThrow(SubcategoriaNaoEncontradaException::new);
    }
}