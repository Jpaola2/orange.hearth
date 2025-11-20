package com.orangehearth.OrangeHearth.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitudConfirmacionSeguridadVeterinario(
	@Email(message = "Correo inválido")
	@NotBlank(message = "El correo es obligatorio")
	String correo,
	@NotBlank(message = "La respuesta de seguridad es obligatoria")
	@JsonAlias({"respuesta", "respuestaSeguridad"})
	String respuestaSeguridad,
	@NotBlank(message = "La nueva contraseña es obligatoria")
	@JsonAlias({"nuevaContrasena", "nuevaPassword"})
	String nuevaPassword
) { }
