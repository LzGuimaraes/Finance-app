package Luiz.Finance.Luiz.CarteiraAtivo.controller;

import Luiz.Finance.Luiz.CarteiraAtivo.CarteiraAtivoService;
import Luiz.Finance.Luiz.CarteiraAtivo.dto.CarteiraAtivoDTOs.*;
import Luiz.Finance.Luiz.Usuarios.UsuarioModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/carteira")
@RequiredArgsConstructor
public class CarteiraAtivoController {

    private final CarteiraAtivoService carteiraAtivoService;

    // ── GET /api/carteira ──────────────────────────────────────────
    // Sempre a carteira do usuário autenticado — nunca recebe usuarioId
    // pela URL, evitando que alguém acesse a carteira de outra pessoa
    // só trocando um parâmetro.
    @GetMapping
    public ResponseEntity<List<CarteiraAtivoResponse>> listarMinhaCarteira(
            @AuthenticationPrincipal UsuarioModel usuario) {
        return ResponseEntity.ok(carteiraAtivoService.listarMinhaCarteira(usuario.getId()));
    }

    // ── GET /api/carteira/{id} ─────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<CarteiraAtivoResponse> buscarMinhaPosicao(
            @AuthenticationPrincipal UsuarioModel usuario, @PathVariable UUID id) {
        return ResponseEntity.ok(carteiraAtivoService.buscarMinhaPosicao(usuario.getId(), id));
    }

    // ── POST /api/carteira ──────────────────────────────────────────
    @PostMapping
    public ResponseEntity<CarteiraAtivoResponse> criar(
            @AuthenticationPrincipal UsuarioModel usuario, @Valid @RequestBody CarteiraAtivoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(carteiraAtivoService.criar(usuario.getId(), request));
    }

    // ── PUT /api/carteira/{id} ────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<CarteiraAtivoResponse> atualizar(
            @AuthenticationPrincipal UsuarioModel usuario,
            @PathVariable UUID id,
            @Valid @RequestBody CarteiraAtivoRequest request) {
        return ResponseEntity.ok(carteiraAtivoService.atualizar(usuario.getId(), id, request));
    }

    // ── DELETE /api/carteira/{id} ─────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @AuthenticationPrincipal UsuarioModel usuario, @PathVariable UUID id) {
        carteiraAtivoService.deletar(usuario.getId(), id);
        return ResponseEntity.noContent().build();
    }
}