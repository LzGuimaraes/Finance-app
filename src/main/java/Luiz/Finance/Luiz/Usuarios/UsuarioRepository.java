package Luiz.Finance.Luiz.Usuarios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, UUID> {

    Optional<UsuarioModel> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<UsuarioModel> findByTokenConfirmacaoEmail(String token);

    Optional<UsuarioModel> findByTokenResetSenha(String token);
}