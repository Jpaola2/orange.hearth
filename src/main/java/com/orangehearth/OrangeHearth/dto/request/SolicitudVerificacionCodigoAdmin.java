package com.orangehearth.OrangeHearth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitudVerificacionCodigoAdmin(
	@Email(message = "Correo inválido")
	@NotBlank
	String correo,
	@NotBlank(message = "El código es obligatorio")
	String codigo
) { }
