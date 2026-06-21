# Patrimônio — Frontend de Autenticação

Frontend básico em **React + Vite (JavaScript)** para autenticação e cadastro,
construído a partir do backend Spring Boot (`AuthController`, `AuthDTOs`, etc).

## Telas

- `/cadastro` — criação de conta (`POST /api/auth/cadastro`)
- `/login` — login (`POST /api/auth/login`)
- `/confirmar-email?token=...` — confirmação de e-mail (`GET /api/auth/confirmar-email`)
- `/solicitar-reset-senha` — solicitar link de redefinição (`POST /api/auth/solicitar-reset-senha`)
- `/reset-senha?token=...` — definir nova senha (`POST /api/auth/reset-senha`)
- `/perfil` — rota protegida, exibe dados do usuário logado (`GET /api/auth/perfil`) e botão de sair

O fluxo de **refresh token** está implementado em `src/services/authService.js`
(`refreshToken`), pronto para ser plugado em um interceptor do axios quando o
access token expirar — não foi conectado automaticamente para manter o escopo
simples (apenas autenticação e cadastro).

## Como rodar

1. Configure a URL da API no arquivo `.env` (já criado, padrão `http://localhost:8080`):
   ```
   VITE_API_URL=http://localhost:8080
   ```
2. Instale as dependências:
   ```
   npm install
   ```
3. Suba o ambiente de desenvolvimento:
   ```
   npm run dev
   ```
4. Acesse `http://localhost:5173`.

Certifique-se de que o backend Spring Boot esteja rodando e com **CORS liberado**
para a origem do Vite (`http://localhost:5173`), já que o `SecurityConfig`
enviado não configura CORS explicitamente.

## Estrutura

```
src/
  components/      AuthLayout (split-screen), RotaProtegida, estilos de form
  context/          AuthContext (estado do usuário logado) + hook useAuth
  pages/            Login, Cadastro, ConfirmarEmail, SolicitarResetSenha, ResetarSenha, Perfil
  services/         api.js (axios), authService.js (chamadas aos endpoints), erros.js
```

## Observações sobre o backend

- Os tokens (`accessToken`, `refreshToken`) são guardados em `localStorage`.
- Erros de validação (`@Valid`) e de negócio são extraídos do `ProblemDetail`
  retornado pelo `GlobalExceptionHandler` (campos `detail` / `erros`).
- O login bloqueia com mensagem amigável quando a conta ainda não confirmou
  o e-mail (`DisabledException` → 403, tratado em `GlobalExceptionHandler`).
