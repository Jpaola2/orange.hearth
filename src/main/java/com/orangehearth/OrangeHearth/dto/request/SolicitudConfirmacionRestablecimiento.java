package com.orangehearth.OrangeHearth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SolicitudConfirmacionRestablecimiento(
	@NotBlank(message = "El token es obligatorio")
	String token,
	@NotBlank(message = "La nueva contraseña es obligatoria")
	String nuevaPassword
) { }
