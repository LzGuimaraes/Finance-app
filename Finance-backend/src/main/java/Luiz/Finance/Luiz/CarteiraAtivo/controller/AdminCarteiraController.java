package Luiz.Finance.Luiz.CarteiraAtivo.controller;

import Luiz.Finance.Luiz.CarteiraAtivo.CarteiraAtivoService;
import Luiz.Finance.Luiz.CarteiraAtivo.dto.CarteiraAtivoDTOs.CarteiraAtivoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// Rotas administrativas: já cobertas por .requestMatchers("/api/admin/**").hasRole("ADMIN")
// no SecurityConfig, mas o @PreAuthorize é mantido aqui como segunda camada
// explícita de defesa, igual ao padrão usado nos demais controllers de escrita.
@RestController
@RequestMapping("/api/admin/usuarios/{usuarioId}/carteira")
@RequiredArgsConstructor
public class AdminCarteiraController {

    private final CarteiraAtivoService carteiraAtivoService;

    // ── GET /api/admin/usuarios/{usuarioId}/carteira (somente ADMIN, read-only) ──
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CarteiraAtivoResponse>> listarCarteiraDoUsuario(
            @PathVariable UUID usuarioId) {
        return ResponseEntity.ok(carteiraAtivoService.listarCarteiraDoUsuario(usuarioId));
    }
}