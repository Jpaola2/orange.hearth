package com.orangehearth.OrangeHearth.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

public record SolicitudActualizacionTutor(
	String nombreCompleto,
	@Pattern(regexp = "^\\d{10}$", message = "El teléfono debe tener 10 dígitos")
	String telefono,
	@Valid
	SolicitudDireccion direccion,
	@JsonAlias({ "estado", "status" })
	EstadoCuenta estado
) { }
