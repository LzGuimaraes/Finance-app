package Luiz.Finance.Luiz.Usuarios.config;

import Luiz.Finance.Luiz.Usuarios.exception.NegocioException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── Regras de negócio (exceções de domínio) ───────────────────
    // Cada subclasse de NegocioException já carrega seu HttpStatus,
    // então um único handler cobre EmailJaCadastrado, TokenInvalido,
    // RefreshTokenExpirado, UsuarioNaoEncontrado etc.
    @ExceptionHandler(NegocioException.class)
    public ProblemDetail handleNegocio(NegocioException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(ex.getStatus());
        pd.setTitle(ex.getStatus().getReasonPhrase());
        pd.setDetail(ex.getMessage());
        return pd;
    }

    // ── Validação de DTOs (@Valid) ─────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            erros.put(campo, error.getDefaultMessage());
        });
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Erro de validação");
        pd.setProperty("erros", erros);
        return pd;
    }

    // ── IllegalArgumentException remanescente ──────────────────────
    // Mantido como rede de segurança para qualquer ponto do código
    // que ainda lance IllegalArgumentException diretamente.
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Requisição inválida");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    // ── Autenticação ────────────────────────────────────────────────
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ProblemDetail handleCredenciais(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Credenciais inválidas");
        pd.setDetail("E-mail ou senha incorretos.");
        return pd;
    }

    @ExceptionHandler(DisabledException.class)
    public ProblemDetail handleDisabled(DisabledException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setTitle("Conta não ativada");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    // ── Autorização ─────────────────────────────────────────────────
    // Lançada quando um usuário autenticado tenta acessar algo que
    // exige uma role que ele não tem (ex: /api/admin/** sem ROLE_ADMIN).
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAcessoNegado(AccessDeniedException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setTitle("Acesso negado");
        pd.setDetail("Você não tem permissão para acessar este recurso.");
        return pd;
    }

    // ── JWT malformado/expirado/inválido fora do filtro ─────────────
    // O JwtAuthenticationFilter já trata isso silenciosamente para rotas
    // protegidas (resultando em 401 pela ausência de autenticação), mas
    // este handler cobre qualquer chamada direta a JwtService que escape
    // do filtro (ex: em testes ou endpoints futuros).
    @ExceptionHandler(JwtException.class)
    public ProblemDetail handleJwt(JwtException ex) {
        log.debug("Falha ao processar JWT: {}", ex.getMessage());
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Token inválido");
        pd.setDetail("O token de autenticação é inválido ou expirou.");
        return pd;
    }

    // ── Corpo da requisição malformado ───────────────────────────────
    // JSON inválido, campo com tipo errado no body, body vazio onde
    // era esperado um JSON, etc.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleJsonMalformado(HttpMessageNotReadableException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Corpo da requisição inválido");
        pd.setDetail("O JSON enviado está malformado ou possui um formato inesperado.");
        return pd;
    }

    // ── Parâmetro de tipo incompatível ────────────────────────────────
    // Ex: ?token=abc onde se esperava um UUID, ou um path variable
    // que não converte para o tipo do método do controller.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTipoIncompativel(MethodArgumentTypeMismatchException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Parâmetro inválido");
        pd.setDetail("O parâmetro '" + ex.getName() + "' possui um valor em formato inválido.");
        return pd;
    }

    // ── Parâmetro obrigatório ausente ──────────────────────────────────
    // Ex: GET /api/auth/confirmar-email sem ?token=...
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleParametroAusente(MissingServletRequestParameterException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Parâmetro obrigatório ausente");
        pd.setDetail("O parâmetro obrigatório '" + ex.getParameterName() + "' não foi informado.");
        return pd;
    }

    // ── Método HTTP não suportado na rota ────────────────────────────────
    // Ex: GET em /api/auth/login (que só aceita POST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMetodoNaoSuportado(HttpRequestMethodNotSupportedException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.METHOD_NOT_ALLOWED);
        pd.setTitle("Método não permitido");
        pd.setDetail("O método " + ex.getMethod() + " não é suportado para este recurso.");
        return pd;
    }

    // ── Rota inexistente ──────────────────────────────────────────────────
    // Requer spring.mvc.throw-exception-if-no-handler-found=true e
    // spring.web.resources.add-mappings=false no application.yml para
    // ser disparado em vez de cair no handler padrão do Tomcat.
    @ExceptionHandler(NoHandlerFoundException.class)
    public ProblemDetail handleRotaNaoEncontrada(NoHandlerFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Recurso não encontrado");
        pd.setDetail("A rota " + ex.getHttpMethod() + " " + ex.getRequestURL() + " não existe.");
        return pd;
    }

    // ── Violação de integridade no banco ────────────────────────────────────
    // Rede de segurança para colisões de constraint (unique, foreign key)
    // que não foram capturadas antes e convertidas em NegocioException.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleIntegridadeDados(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Violação de integridade de dados em {} {}: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflito de dados");
        pd.setDetail("A operação viola uma restrição de integridade dos dados.");
        return pd;
    }

    // ── Rede de segurança final ───────────────────────────────────────────────
    // Qualquer exceção não mapeada acima cai aqui. Sem isso, o Spring
    // retorna 500 com stacktrace ou corpo padrão não estruturado.
    // Importante: sempre logar com stacktrace completo, pois é um caso
    // não previsto e precisa de investigação.
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenerico(Exception ex, HttpServletRequest request) {
        log.error("Erro não tratado em {} {}", request.getMethod(), request.getRequestURI(), ex);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Erro interno");
        pd.setDetail("Ocorreu um erro inesperado. Tente novamente mais tarde.");
        return pd;
    }
}