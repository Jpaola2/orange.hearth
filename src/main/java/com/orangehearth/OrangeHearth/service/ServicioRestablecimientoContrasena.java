package com.orangehearth.OrangeHearth.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orangehearth.OrangeHearth.dto.request.SolicitudConfirmacionRestablecimiento;
import com.orangehearth.OrangeHearth.dto.request.SolicitudRestablecimientoContrasena;
import com.orangehearth.OrangeHearth.exception.ExcepcionValidacion;
import com.orangehearth.OrangeHearth.model.entity.TokenRestablecimientoContrasena;
import com.orangehearth.OrangeHearth.model.entity.CuentaUsuario;
import com.orangehearth.OrangeHearth.model.enums.Rol;
import com.orangehearth.OrangeHearth.repository.RepositorioTokensRestablecimiento;
import com.orangehearth.OrangeHearth.repository.RepositorioCuentasUsuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicioRestablecimientoContrasena {

	private final RepositorioCuentasUsuario userAccountRepository;
	private final RepositorioTokensRestablecimiento passwordResetTokenRepository;
	private final PasswordEncoder passwordEncoder;
    private final ServicioPoliticaContrasena passwordPolicyService;
    private final ServicioCorreo emailService;

	@Value("${app.veterinarian.reset.expiration-minutes:60}")
	private long expirationMinutes;

    @Value("${app.veterinarian.reset.base-url:http://localhost:8080/login-rol.html?resetVet=1&token=}")
    private String resetBaseUrl;

    @Transactional
    public void requestVeterinarianReset(SolicitudRestablecimientoContrasena request) {
		Optional<CuentaUsuario> accountOptional = userAccountRepository.findByEmailIgnoreCase(request.correo())
			.filter(user -> user.getRol() == Rol.VETERINARIO);

		accountOptional.ifPresent(account -> {
			TokenRestablecimientoContrasena token = TokenRestablecimientoContrasena.builder()
				.token(UUID.randomUUID().toString())
				.userAccount(account)
				.expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
				.build();
            passwordResetTokenRepository.save(token);
            String url = resetBaseUrl + token.getToken();
            emailService.sendVeterinarianResetLinkHtml(account.getEmail(), account.getFullName(), url);
        });
	}

	@Transactional
	public void confirmVeterinarianReset(SolicitudConfirmacionRestablecimiento request) {
		TokenRestablecimientoContrasena token = passwordResetTokenRepository.findByTokenAndUsedAtIsNull(request.token())
			.orElseThrow(() -> new ExcepcionValidacion("Token inválido o ya utilizado."));

		if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new ExcepcionValidacion("El token expiró, solicita uno nuevo.");
		}

		CuentaUsuario account = token.getCuentaUsuario();
		if (account.getRol() != Rol.VETERINARIO) {
			throw new ExcepcionValidacion("El token no corresponde a un veterinario.");
		}

		passwordPolicyService.validateOrThrow(request.nuevaPassword());
		account.setPassword(passwordEncoder.encode(request.nuevaPassword()));
		account.setPasswordUpdatedAt(LocalDateTime.now());
		token.setUsedAt(LocalDateTime.now());
	}
}
