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
import com.orangehearth.OrangeHearth.model.entity.Veterinarian;
import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;
import com.orangehearth.OrangeHearth.model.enums.Rol;
import com.orangehearth.OrangeHearth.repository.RepositorioTokensRestablecimiento;
import com.orangehearth.OrangeHearth.repository.RepositorioCuentasUsuario;
import com.orangehearth.OrangeHearth.repository.RepositorioVeterinarios;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicioRestablecimientoContrasena {

	private final RepositorioCuentasUsuario userAccountRepository;
	private final RepositorioTokensRestablecimiento passwordResetTokenRepository;
	private final PasswordEncoder passwordEncoder;
    private final ServicioPoliticaContrasena passwordPolicyService;
    private final ServicioCorreo emailService;
    private final RepositorioVeterinarios veterinarianRepository;

	@Value("${app.veterinarian.reset.expiration-minutes:60}")
	private long expirationMinutes;

    @Value("${app.veterinarian.reset.base-url:http://localhost:8080/login-rol.html?resetVet=1&token=}")
    private String resetBaseUrl;

    @Transactional
    public void requestVeterinarianReset(SolicitudRestablecimientoContrasena request) {
        throw new ExcepcionValidacion("El restablecimiento por correo ha sido desactivado para veterinarios. Usa la opción de pregunta de seguridad.");
	}

	@Transactional
	public void confirmVeterinarianReset(SolicitudConfirmacionRestablecimiento request) {
        throw new ExcepcionValidacion("El flujo de token por correo ha sido desactivado para veterinarios. Usa la opción de pregunta de seguridad.");
	}

    @Transactional(readOnly = true)
    public String getVeterinarianSecurityQuestion(SolicitudRestablecimientoContrasena request) {
        CuentaUsuario account = userAccountRepository.findByEmailIgnoreCase(request.correo())
            .filter(user -> user.getRol() == Rol.VETERINARIO)
            .filter(user -> user.getStatus() == EstadoCuenta.ACTIVE)
            .orElseThrow(() -> new ExcepcionValidacion("No fue posible completar el proceso. Por favor contacta a la administración."));

        veterinarianRepository.findByUserAccountId(account.getId())
            .filter(v -> v.getStatus() == EstadoCuenta.ACTIVE)
            .orElseThrow(() -> new ExcepcionValidacion("No fue posible completar el proceso. Por favor contacta a la administración."));

        if (account.getSecurityQuestion() == null || account.getSecurityQuestion().isBlank()
            || account.getSecurityAnswerHash() == null || account.getSecurityAnswerHash().isBlank()) {
            throw new ExcepcionValidacion("No fue posible completar el proceso. Por favor contacta a la administración.");
        }

        return account.getSecurityQuestion();
    }

    @Transactional
    public void confirmVeterinarianResetBySecurityAnswer(
        com.orangehearth.OrangeHearth.dto.request.SolicitudConfirmacionSeguridadVeterinario request
    ) {
        CuentaUsuario account = userAccountRepository.findByEmailIgnoreCase(request.correo())
            .orElseThrow(() -> new ExcepcionValidacion("Credenciales inválidas."));

        if (account.getRol() != Rol.VETERINARIO) {
            throw new ExcepcionValidacion("No autorizado para este proceso.");
        }
        if (account.getStatus() != EstadoCuenta.ACTIVE) {
            throw new ExcepcionValidacion("No fue posible completar el proceso. Por favor contacta a la administración.");
        }

        veterinarianRepository.findByUserAccountId(account.getId())
            .filter(v -> v.getStatus() == EstadoCuenta.ACTIVE)
            .orElseThrow(() -> new ExcepcionValidacion("No fue posible completar el proceso. Por favor contacta a la administración."));

        if (account.getSecurityAnswerHash() == null || account.getSecurityAnswerHash().isBlank()) {
            throw new ExcepcionValidacion("No fue posible completar el proceso. Por favor contacta a la administración.");
        }

        if (!passwordEncoder.matches(request.respuestaSeguridad(), account.getSecurityAnswerHash())) {
            throw new ExcepcionValidacion("La respuesta de seguridad es incorrecta.");
        }

        passwordPolicyService.validateOrThrow(request.nuevaPassword());
        account.setPassword(passwordEncoder.encode(request.nuevaPassword()));
        account.setPasswordUpdatedAt(LocalDateTime.now());
    }
}
