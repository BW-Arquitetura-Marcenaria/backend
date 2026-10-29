package br.com.bw.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String remetente;

    @Value("${app.mail.nome-remetente:BW Arquitetura}")
    private String nomeRemetente;

    @Value("${app.convite-admin.base-url}")
    private String baseUrl;

    @Value("${app.convite-admin.expiracao-horas}")
    private int expiracaoHoras;

    public void sendAdminInvite(String destinatario, String nomeCompleto, String nomeEmpresa, String token) {
        String link = baseUrl + "/convite-admin?token=" + token;

        String nome = HtmlUtils.htmlEscape(nomeCompleto);
        String empresa = HtmlUtils.htmlEscape(nomeEmpresa);

        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 520px; margin: 0 auto; color: #222;">
                  <h2 style="color: #5a3e2b;">Convite para administrar a %s</h2>
                  <p>Olá, %s.</p>
                  <p>Você foi convidado(a) para fazer parte da equipe de administradores da <strong>%s</strong>.
                  Ao aceitar, você define a sua própria senha e passa a ter acesso ao painel administrativo.</p>
                  <p style="margin: 28px 0;">
                    <a href="%s"
                       style="background: #5a3e2b; color: #fff; padding: 12px 24px;
                              text-decoration: none; border-radius: 6px; display: inline-block;">
                      Aceitar convite
                    </a>
                    &nbsp;
                    <a href="%s&acao=recusar"
                       style="color: #5a3e2b; padding: 12px 24px; text-decoration: none;
                              border: 1px solid #5a3e2b; border-radius: 6px; display: inline-block;">
                      Recusar
                    </a>
                  </p>
                  <p style="font-size: 13px; color: #666;">
                    O link é pessoal e vale por %d horas. A resposta é confirmada na própria página.
                    Se não esperava este convite, basta ignorar este e-mail.
                  </p>
                </div>
                """.formatted(empresa, nome, empresa, link, link, expiracaoHoras);

        send(destinatario, nomeEmpresa, "Convite para administrar a " + nomeEmpresa, html);
    }

    public void sendNewMessageAlertToTeam(String destinatario, String nomeCliente) {
        String cliente = HtmlUtils.htmlEscape(nomeCliente);
        String link = baseUrl + "/admin/chat";

        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 520px; margin: 0 auto; color: #222;">
                  <h2 style="color: #5a3e2b;">Nova mensagem de cliente</h2>
                  <p><strong>%s</strong> enviou uma mensagem no chat e está aguardando resposta.</p>
                  <p style="margin: 28px 0;">
                    <a href="%s"
                       style="background: #5a3e2b; color: #fff; padding: 12px 24px;
                              text-decoration: none; border-radius: 6px; display: inline-block;">
                      Abrir conversas
                    </a>
                  </p>
                  <p style="font-size: 13px; color: #666;">
                    Você recebe um aviso por sequência de mensagens, não uma para cada mensagem.
                  </p>
                </div>
                """.formatted(cliente, link);

        send(destinatario, nomeRemetente, "Nova mensagem de " + nomeCliente, html);
    }

    public void sendReplyAlertToClient(String destinatario, String nomeCliente) {
        String cliente = HtmlUtils.htmlEscape(nomeCliente);
        String empresa = HtmlUtils.htmlEscape(nomeRemetente);
        String link = baseUrl + "/chat";

        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 520px; margin: 0 auto; color: #222;">
                  <h2 style="color: #5a3e2b;">Você tem uma resposta</h2>
                  <p>Olá, %s.</p>
                  <p>A equipe da <strong>%s</strong> respondeu a sua mensagem no chat.</p>
                  <p style="margin: 28px 0;">
                    <a href="%s"
                       style="background: #5a3e2b; color: #fff; padding: 12px 24px;
                              text-decoration: none; border-radius: 6px; display: inline-block;">
                      Ver resposta
                    </a>
                  </p>
                </div>
                """.formatted(cliente, empresa, link);

        send(destinatario, nomeRemetente, "Você tem uma resposta da " + nomeRemetente, html);
    }

    private void send(String destinatario, String nomeExibido, String assunto, String html) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, "UTF-8");
            helper.setFrom(remetente, nomeExibido);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(html, true);

            mailSender.send(mensagem);
        } catch (MessagingException | UnsupportedEncodingException | MailException e) {
            log.error("Falha ao enviar e-mail '{}' para {}", assunto, destinatario, e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Não foi possível enviar o e-mail. Tente novamente.");
        }
    }
}