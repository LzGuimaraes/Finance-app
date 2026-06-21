package Luiz.Finance.Luiz.CategoriaInvestimento.controller;

import Luiz.Finance.Luiz.CategoriaInvestimento.CategoriaInvestimentoService;
import Luiz.Finance.Luiz.CategoriaInvestimento.dto.CategoriaInvestimentoDTOs.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaInvestimentoController {

    private final CategoriaInvestimentoService categoriaService;

    // ── GET /api/categorias ─────────────────────────────────────────
    // Catálogo global: qualquer usuário autenticado pode listar.
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        return ResponseEntity.ok(categoriaService.listar());
    }

    // ── GET /api/categorias/{id} ─────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    // ── POST /api/categorias (somente ADMIN) ──────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.criar(request));
    }

    // ── PUT /api/categorias/{id} (somente ADMIN) ───────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponse> atualizar(
            @PathVariable Integer id, @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.atualizar(id, request));
    }

    // ── DELETE /api/categorias/{id} (somente ADMIN) ─────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}