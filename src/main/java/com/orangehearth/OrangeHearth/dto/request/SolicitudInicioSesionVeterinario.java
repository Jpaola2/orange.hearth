package com.orangehearth.OrangeHearth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitudInicioSesionVeterinario(
	@Email(message = "Correo inválido")
	@NotBlank
	String correo,
	@NotBlank
	String password,
	@NotBlank(message = "La tarjeta profesional es obligatoria")
	String tarjetaProfesional
) { }
