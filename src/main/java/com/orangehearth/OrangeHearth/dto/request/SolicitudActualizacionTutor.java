package com.orangehearth.OrangeHearth.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

public record SolicitudActualizacionTutor(
	@Pattern(regexp = "^\\d{10}$", message = "El teléfono debe tener 10 dígitos")
	String telefono,
	@Valid
	SolicitudDireccion direccion
) { }
