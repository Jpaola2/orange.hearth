package com.orangehearth.OrangeHearth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitudRestablecimientoContrasena(
	@Email(message = "Correo inválido")
	@NotBlank
	String correo
) { }
