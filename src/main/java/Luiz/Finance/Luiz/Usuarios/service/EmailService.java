package Luiz.Finance.Luiz.Usuarios.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    // URL do backend — usada para o link de confirmação de e-mail,
    // que é um GET tratado diretamente pelo AuthController.
    @Value("${app.url:http://localhost:8080}")
    private String appUrl;

    // URL do frontend — usada para o link de reset de senha, que precisa
    // abrir uma TELA (formulário de nova senha) e não chamar a API direto,
    // já que /api/auth/reset-senha é um POST.
    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String remetente;

    @Async
    public void enviarConfirmacaoEmail(String destinatario, String token) {
        String link = appUrl + "/api/auth/confirmar-email?token=" + token;
        String corpo = """
                Olá! Bem-vindo ao Luiz Finance.
                
                Confirme seu e-mail clicando no link abaixo:
                %s
                
                O link expira em 24 horas.
                
                Se você não criou uma conta, ignore este e-mail.
                """.formatted(link);

        enviar(destinatario, "Confirme seu e-mail - Luiz Finance", corpo);
    }

    @Async
    public void enviarResetSenha(String destinatario, String token) {
        String link = frontendUrl + "/reset-senha?token=" + token;
        String corpo = """
                Recebemos uma solicitação de redefinição de senha para sua conta.
                
                Clique no link abaixo para criar uma nova senha:
                %s
                
                O link expira em 1 hora.
                
                Se você não solicitou a redefinição, ignore este e-mail.
                """.formatted(link);

        enviar(destinatario, "Redefinição de senha - Luiz Finance", corpo);
    }

    private void enviar(String destinatario, String assunto, String corpo) {
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(remetente);
            mensagem.setTo(destinatario);
            mensagem.setSubject(assunto);
            mensagem.setText(corpo);
            mailSender.send(mensagem);
            log.info("E-mail enviado para {}", destinatario);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail para {}: {}", destinatario, e.getMessage());
        }
    }
}