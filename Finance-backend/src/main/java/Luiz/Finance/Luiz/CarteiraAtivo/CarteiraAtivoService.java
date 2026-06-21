package Luiz.Finance.Luiz.CarteiraAtivo;

import Luiz.Finance.Luiz.Ativo.AtivoModel;
import Luiz.Finance.Luiz.Ativo.AtivoRepository;
import Luiz.Finance.Luiz.Ativo.exception.AtivoNaoEncontradoException;
import Luiz.Finance.Luiz.CarteiraAtivo.dto.CarteiraAtivoDTOs.*;
import Luiz.Finance.Luiz.CarteiraAtivo.dto.CarteiraAtivoMapper;
import Luiz.Finance.Luiz.CarteiraAtivo.exception.CarteiraAtivoJaExisteException;
import Luiz.Finance.Luiz.CarteiraAtivo.exception.CarteiraAtivoNaoEncontradoException;
import Luiz.Finance.Luiz.Usuarios.UsuarioModel;
import Luiz.Finance.Luiz.Usuarios.UsuarioRepository;
import Luiz.Finance.Luiz.Usuarios.exception.UsuarioNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarteiraAtivoService {

    private final CarteiraAtivoRepository carteiraAtivoRepository;
    private final AtivoRepository ativoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarteiraAtivoMapper carteiraAtivoMapper;

    // ── Listagem (usuário comum — sua própria carteira) ──────────────
    public List<CarteiraAtivoResponse> listarMinhaCarteira(UUID usuarioId) {
        return carteiraAtivoRepository.findAllByUsuarioId(usuarioId).stream()
                .map(carteiraAtivoMapper::toResponse)
                .toList();
    }

    // ── Busca por id (usuário comum — só a própria posição) ──────────
    public CarteiraAtivoResponse buscarMinhaPosicao(UUID usuarioId, UUID id) {
        return carteiraAtivoMapper.toResponse(buscarDoUsuarioOuFalhar(usuarioId, id));
    }

    // ── Criação (usuário comum) ───────────────────────────────────────
    // O ativo precisa já existir no catálogo global (somente ADMIN cria
    // ativos novos — ver AtivoService/AtivoController).
    @Transactional
    public CarteiraAtivoResponse criar(UUID usuarioId, CarteiraAtivoRequest request) {
        UsuarioModel usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(UsuarioNaoEncontradoException::new);

        AtivoModel ativo = ativoRepository.findById(request.ativoId())
                .orElseThrow(AtivoNaoEncontradoException::new);

        if (carteiraAtivoRepository.existsByUsuarioIdAndAtivoId(usuarioId, request.ativoId())) {
            throw new CarteiraAtivoJaExisteException();
        }

        CarteiraAtivoModel carteiraAtivo = CarteiraAtivoModel.builder()
                .usuario(usuario)
                .ativo(ativo)
                .quantidade(request.quantidade())
                .precoMedio(request.precoMedio())
                .notaQualidade(request.notaQualidade())
                .build();

        try {
            carteiraAtivoRepository.save(carteiraAtivo);
        } catch (DataIntegrityViolationException ex) {
            // Corrida na constraint UNIQUE(usuario_id, ativo_id).
            throw new CarteiraAtivoJaExisteException();
        }

        return carteiraAtivoMapper.toResponse(carteiraAtivo);
    }

    // ── Edição (usuário comum — só a própria posição) ─────────────────
    // Não permite troca de ativo: alterar o ativo de uma posição é
    // conceitualmente "vender um e comprar outro", então força criar uma
    // nova posição (deletar + criar) em vez de editar o ativoId aqui.
    @Transactional
    public CarteiraAtivoResponse atualizar(UUID usuarioId, UUID id, CarteiraAtivoRequest request) {
        CarteiraAtivoModel carteiraAtivo = buscarDoUsuarioOuFalhar(usuarioId, id);

        if (!carteiraAtivo.getAtivo().getId().equals(request.ativoId())) {
            throw new IllegalArgumentException(
                    "Não é possível alterar o ativo de uma posição existente. Remova esta posição e crie uma nova.");
        }

        carteiraAtivo.setQuantidade(request.quantidade());
        carteiraAtivo.setPrecoMedio(request.precoMedio());
        carteiraAtivo.setNotaQualidade(request.notaQualidade());

        carteiraAtivoRepository.save(carteiraAtivo);
        return carteiraAtivoMapper.toResponse(carteiraAtivo);
    }

    // ── Remoção (usuário comum — só a própria posição) ────────────────
    @Transactional
    public void deletar(UUID usuarioId, UUID id) {
        CarteiraAtivoModel carteiraAtivo = buscarDoUsuarioOuFalhar(usuarioId, id);
        carteiraAtivoRepository.delete(carteiraAtivo);
    }

    // ── Visão do ADMIN (somente leitura, carteira de qualquer usuário) ──
    public List<CarteiraAtivoResponse> listarCarteiraDoUsuario(UUID usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new UsuarioNaoEncontradoException();
        }
        return carteiraAtivoRepository.findAllByUsuarioId(usuarioId).stream()
                .map(carteiraAtivoMapper::toResponse)
                .toList();
    }

    // ── Helper interno ───────────────────────────────────────────────
    // findByIdAndUsuarioId garante que a posição pertence ao usuário
    // autenticado — sem isso, o id da posição sozinho poderia "escapar"
    // e permitir acesso/edição da carteira de outra pessoa.
    private CarteiraAtivoModel buscarDoUsuarioOuFalhar(UUID usuarioId, UUID id) {
        return carteiraAtivoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(CarteiraAtivoNaoEncontradoException::new);
    }
}