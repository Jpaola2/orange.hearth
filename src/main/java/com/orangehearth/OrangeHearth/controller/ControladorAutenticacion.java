package com.orangehearth.OrangeHearth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.orangehearth.OrangeHearth.dto.request.SolicitudConfirmacionRestablecimiento;
import com.orangehearth.OrangeHearth.dto.request.SolicitudRestablecimientoContrasena;
import com.orangehearth.OrangeHearth.dto.request.SolicitudInicioSesionTutor;
import com.orangehearth.OrangeHearth.dto.request.SolicitudInicioSesionVeterinario;
import com.orangehearth.OrangeHearth.dto.response.RespuestaAutenticacion;
import com.orangehearth.OrangeHearth.service.ServicioAutenticacion;
import com.orangehearth.OrangeHearth.service.ServicioRestablecimientoContrasena;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@Validated
@RequiredArgsConstructor
public class ControladorAutenticacion {

	private final ServicioAutenticacion authService;
	private final ServicioRestablecimientoContrasena passwordResetService;

	@PostMapping("/tutores/login")
	public RespuestaAutenticacion loginTutor(@Valid @RequestBody SolicitudInicioSesionTutor request) {
		return authService.loginTutor(request);
	}

	@PostMapping("/veterinarios/login")
	public RespuestaAutenticacion loginVeterinarian(@Valid @RequestBody SolicitudInicioSesionVeterinario request) {
		return authService.loginVeterinarian(request);
	}

	@PostMapping("/veterinarios/password-reset/request")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public String requestVeterinarianPasswordReset(@Valid @RequestBody SolicitudRestablecimientoContrasena request) {
		passwordResetService.requestVeterinarianReset(request);
		return "Si el correo pertenece a un veterinario activo, se envió un enlace de recuperación.";
	}

	@PostMapping("/veterinarios/password-reset/confirm")
	public String confirmVeterinarianPasswordReset(
		@Valid @RequestBody SolicitudConfirmacionRestablecimiento request
	) {
		passwordResetService.confirmVeterinarianReset(request);
		return "Contraseña actualizada con éxito.";
	}
}
