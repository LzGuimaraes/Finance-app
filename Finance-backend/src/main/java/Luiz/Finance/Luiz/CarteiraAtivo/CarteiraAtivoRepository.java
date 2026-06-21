package Luiz.Finance.Luiz.CarteiraAtivo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarteiraAtivoRepository extends JpaRepository<CarteiraAtivoModel, UUID> {

    // ── Escopo do usuário comum (dono da carteira) ───────────────────
    List<CarteiraAtivoModel> findAllByUsuarioId(UUID usuarioId);

    // Garante que a posição realmente pertence ao usuário autenticado —
    // essencial para impedir que um usuário acesse/edite/delete a posição
    // de outro só trocando o id na URL.
    Optional<CarteiraAtivoModel> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    boolean existsByUsuarioIdAndAtivoId(UUID usuarioId, Integer ativoId);

    // ── Visão do ADMIN (qualquer usuário, somente leitura) ───────────
    // Reaproveita findAllByUsuarioId — mantido como método próprio para
    // deixar explícito, no service, qual chamada é "visão de admin" vs
    // "minha carteira".
}