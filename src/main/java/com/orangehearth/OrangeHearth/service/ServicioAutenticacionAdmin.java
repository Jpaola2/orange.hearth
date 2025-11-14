package com.orangehearth.OrangeHearth.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.orangehearth.OrangeHearth.dto.response.RespuestaSesionAdmin;
import com.orangehearth.OrangeHearth.exception.ExcepcionRecursoNoEncontrado;
import com.orangehearth.OrangeHearth.exception.ExcepcionAccionNoAutorizada;
import com.orangehearth.OrangeHearth.exception.ExcepcionValidacion;
import com.orangehearth.OrangeHearth.model.entity.CodigoOtpAdmin;
import com.orangehearth.OrangeHearth.model.entity.SesionAdmin;
import com.orangehearth.OrangeHearth.model.entity.CuentaUsuario;
import com.orangehearth.OrangeHearth.repository.RepositorioCodigoOtpAdmin;
import com.orangehearth.OrangeHearth.repository.RepositorioSesionesAdmin;
import com.orangehearth.OrangeHearth.repository.RepositorioCuentasUsuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicioAutenticacionAdmin {

	private final RepositorioCuentasUsuario userAccountRepository;
	private final RepositorioCodigoOtpAdmin adminOtpCodeRepository;
	private final RepositorioSesionesAdmin adminSessionRepository;
	private final PasswordEncoder passwordEncoder;
	private final ServicioCorreo emailService;

	@Value("${app.admin.email}")
	private String adminEmail;

	@Value("${app.admin.otp.expiration-minutes:10}")
	private long otpExpirationMinutes;

	@Value("${app.admin.session.ttl-minutes:30}")
	private long sessionTtlMinutes;

    @Transactional
    public void requestAccessCode(String email, String requestIp) {
        validateAdminEmail(email);

		CuentaUsuario admin = userAccountRepository.findByEmailIgnoreCase(adminEmail)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("Cuenta de administrador no encontrada."));

		String code = generateCode();
		CodigoOtpAdmin otp = CodigoOtpAdmin.builder()
			.adminAccount(admin)
			.codeHash(passwordEncoder.encode(code))
			.expiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
			.consumed(false)
			.build();
    adminOtpCodeRepository.save(otp);

    // Enviar el correo DESPUÉS de confirmar la transacción para evitar rollback
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                // Enviar con plantilla HTML bonita
                emailService.sendAdminOtpHtml(adminEmail, admin.getFullName(), code, requestIp != null ? requestIp : "localhost");
                } catch (Exception ex) {
                    // Sólo registrar el error; el OTP ya quedó persistido
                    // El usuario puede solicitar un nuevo código o reintentar
                }
            }
        });
    }

	@Transactional
	public RespuestaSesionAdmin verifyCode(String email, String code) {
		validateAdminEmail(email);

		CuentaUsuario admin = userAccountRepository.findByEmailIgnoreCase(adminEmail)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("Cuenta de administrador no encontrada."));

		CodigoOtpAdmin otp = adminOtpCodeRepository
			.findTopByAdminAccountIdAndConsumedFalseOrderByCreatedAtDesc(admin.getId())
			.orElseThrow(() -> new ExcepcionValidacion("No hay un código activo. Solicita uno nuevo."));

		if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new ExcepcionValidacion("El código expiró, solicita uno nuevo.");
		}

		if (!passwordEncoder.matches(code, otp.getCodeHash())) {
			throw new ExcepcionValidacion("Código incorrecto.");
		}

		otp.setConsumed(true);

		SesionAdmin session = SesionAdmin.builder()
			.adminAccount(admin)
			.token(UUID.randomUUID().toString())
			.expiresAt(LocalDateTime.now().plusMinutes(sessionTtlMinutes))
			.revoked(false)
			.build();

		adminSessionRepository.save(session);

		return new RespuestaSesionAdmin(
			"Autenticación exitosa.",
			session.getToken(),
			sessionTtlMinutes
		);
	}

	@Transactional(readOnly = true)
	public CuentaUsuario validateSessionToken(String token) {
		SesionAdmin session = adminSessionRepository.findByTokenAndRevokedFalse(token)
			.orElseThrow(() -> new ExcepcionAccionNoAutorizada("Sesión de administrador inválida."));

		if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new ExcepcionAccionNoAutorizada("La sesión de administrador expiró.");
		}

		return session.getAdminAccount();
	}

	private void validateAdminEmail(String email) {
		if (email == null || !email.equalsIgnoreCase(adminEmail)) {
			throw new ExcepcionValidacion("Solo el correo de gerencia autorizado puede solicitar códigos.");
		}
	}

	private String generateCode() {
		int value = (int) (Math.random() * 1_000_000);
		return String.format("%06d", value);
	}
}
