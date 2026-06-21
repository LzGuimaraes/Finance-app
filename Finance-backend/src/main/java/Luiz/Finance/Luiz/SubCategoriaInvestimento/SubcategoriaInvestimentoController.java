package Luiz.Finance.Luiz.SubCategoriaInvestimento.controller;

import Luiz.Finance.Luiz.SubCategoriaInvestimento.SubcategoriaInvestimentoService;
import Luiz.Finance.Luiz.SubCategoriaInvestimento.dto.SubcategoriaInvestimentoDTOs.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias/{categoriaId}/subcategorias")
@RequiredArgsConstructor
public class SubcategoriaInvestimentoController {

    private final SubcategoriaInvestimentoService subcategoriaService;

    // ── GET /api/categorias/{categoriaId}/subcategorias ───────────────
    @GetMapping
    public ResponseEntity<List<SubcategoriaResponse>> listar(@PathVariable Integer categoriaId) {
        return ResponseEntity.ok(subcategoriaService.listar(categoriaId));
    }

    // ── GET /api/categorias/{categoriaId}/subcategorias/{id} ──────────
    @GetMapping("/{id}")
    public ResponseEntity<SubcategoriaResponse> buscarPorId(
            @PathVariable Integer categoriaId, @PathVariable Integer id) {
        return ResponseEntity.ok(subcategoriaService.buscarPorId(categoriaId, id));
    }

    // ── POST /api/categorias/{categoriaId}/subcategorias (somente ADMIN) ──
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubcategoriaResponse> criar(
            @PathVariable Integer categoriaId, @Valid @RequestBody SubcategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subcategoriaService.criar(categoriaId, request));
    }

    // ── PUT /api/categorias/{categoriaId}/subcategorias/{id} (somente ADMIN) ──
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubcategoriaResponse> atualizar(
            @PathVariable Integer categoriaId,
            @PathVariable Integer id,
            @Valid @RequestBody SubcategoriaRequest request) {
        return ResponseEntity.ok(subcategoriaService.atualizar(categoriaId, id, request));
    }

    // ── DELETE /api/categorias/{categoriaId}/subcategorias/{id} (somente ADMIN) ──
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Integer categoriaId, @PathVariable Integer id) {
        subcategoriaService.deletar(categoriaId, id);
        return ResponseEntity.noContent().build();
    }
}