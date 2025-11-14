package com.orangehearth.OrangeHearth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicioCorreo {

	private static final Logger log = LoggerFactory.getLogger(ServicioCorreo.class);

	private final JavaMailSender mailSender;

	@Value("${app.mail.from}")
	private String from;

	public void sendAdminOtp(String to, String code) {
		String subject = "Código de acceso OrangeHearth";
		String text = """
			Hola Jessica,

			Tu código de un solo uso para ingresar a OrangeHearth es: %s
			El código expira en 10 minutos. No lo compartas con nadie.

			Equipo OrangeHearth.
			""".formatted(code);
		sendEmail(to, subject, text);
	}

	public void sendVeterinarianResetLink(String to, String token) {
		String subject = "Recuperación de contraseña - OrangeHearth";
		String resetUrl = "https://orangehearth/reset-password?token=" + token;
		String text = """
			Hola,

			Recibimos una solicitud para restablecer tu contraseña de acceso como veterinario.
			Puedes crear una nueva contraseña usando el siguiente enlace (válido por 60 minutos):

			%s

			Si no fuiste tú, ignora este correo.

			Equipo OrangeHearth.
			""".formatted(resetUrl);
		sendEmail(to, subject, text);
	}

	// HTML con plantilla para restablecimiento del veterinario
	public void sendVeterinarianResetLinkHtml(String to, String nombre, String url) {
		String subject = "🔐 Restablece tu contraseña - OrangeHearth";
		String html = cargarPlantillaReset(nombre, url);
		enviarHtml(to, subject, html);
	}

	private void enviarHtml(String to, String subject, String html) {
		try {
			MimeMessage msg = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
			helper.setFrom(from, "Orange Hearth");
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(html, true);

			try {
				ClassPathResource logo = new ClassPathResource("mail/LogoOrangeHearth.png");
				if (!logo.exists()) logo = new ClassPathResource("static/imagenes/LogoOrangeHearth.png");
				if (logo.exists()) helper.addInline("logo", logo, "image/png");
			} catch (Exception ignore) {}

			mailSender.send(msg);
		} catch (Exception ex) {
			log.error("No se pudo enviar el correo a {}: {}", to, ex.getMessage());
			throw new IllegalStateException("No se pudo enviar el correo electrónico.");
		}
	}

	private String cargarPlantillaReset(String nombre, String url) {
		try {
			var res = new ClassPathResource("templates/mail/reset.html");
			String html = new String(res.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
			html = html.replace("{{NAME}}", escape(nombre))
				   .replace("{{URL}}", escape(url));
			return html;
		} catch (Exception ex) {
			return "<html><body><p>Hola " + escape(nombre) + ",</p>" +
				   "<p>Para restablecer tu contraseña haz clic en: <a href='" + escape(url) + "'>Restablecer</a></p>" +
				   "<p>Si no solicitaste esto, ignora este mensaje.</p></body></html>";
		}
	}

    private String escape(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // OTP HTML para administrador usando plantilla otp.html
    public void sendAdminOtpHtml(String to, String nombre, String codigo, String ip) {
        try {
            var res = new ClassPathResource("templates/mail/otp.html");
            String html = new String(res.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            html = html.replace("{{NAME}}", escape(nombre != null ? nombre : "Gerencia"))
                       .replace("{{CODE}}", escape(codigo))
                       .replace("{{IP}}", escape(ip != null ? ip : "localhost"))
                       .replace("{{DATETIME}}", java.time.ZonedDateTime.now().toString());
            enviarHtml(to, "🔑 Tu Código de Acceso para OrangeHearth", html);
        } catch (Exception e) {
            // Si falla plantilla, cae al texto plano
            sendAdminOtp(to, codigo);
        }
    }

	private void sendEmail(String to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(from);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			mailSender.send(message);
		} catch (MailException ex) {
			log.error("No se pudo enviar el correo a {}: {}", to, ex.getMessage());
			throw new IllegalStateException("No se pudo enviar el correo electrónico.");
		}
	}
}
