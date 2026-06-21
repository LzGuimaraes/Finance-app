package Luiz.Finance.Luiz.Ativo.controller;

import Luiz.Finance.Luiz.Ativo.AtivoService;
import Luiz.Finance.Luiz.Ativo.dto.AtivoDTOs.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ativos")
@RequiredArgsConstructor
public class AtivoController {

    private final AtivoService ativoService;

    // ── GET /api/ativos ─────────────────────────────────────────────
    // Catálogo global: qualquer usuário autenticado pode listar.
    @GetMapping
    public ResponseEntity<List<AtivoResponse>> listar() {
        return ResponseEntity.ok(ativoService.listar());
    }

    // ── GET /api/ativos/{id} ────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<AtivoResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ativoService.buscarPorId(id));
    }

    // ── GET /api/ativos/buscar?ticker=... (issue #40) ───────────────
    @GetMapping("/buscar")
    public ResponseEntity<AtivoResponse> buscarPorTicker(@RequestParam String ticker) {
        return ResponseEntity.ok(ativoService.buscarPorTicker(ticker));
    }

    // ── POST /api/ativos (somente ADMIN) ─────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AtivoResponse> criar(@Valid @RequestBody AtivoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ativoService.criar(request));
    }

    // ── PUT /api/ativos/{id} (somente ADMIN) ─────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AtivoResponse> atualizar(
            @PathVariable Integer id, @Valid @RequestBody AtivoRequest request) {
        return ResponseEntity.ok(ativoService.atualizar(id, request));
    }

    // ── PATCH /api/ativos/{id}/cotacao (somente ADMIN) ───────────────
    // Rota dedicada para atualização de preço (RF010), separada da edição
    // completa — facilita a futura integração automática (issue #44).
    @PatchMapping("/{id}/cotacao")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AtivoResponse> atualizarCotacao(
            @PathVariable Integer id, @Valid @RequestBody AtivoCotacaoRequest request) {
        return ResponseEntity.ok(ativoService.atualizarCotacao(id, request));
    }

    // ── DELETE /api/ativos/{id} (somente ADMIN) ──────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        ativoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}