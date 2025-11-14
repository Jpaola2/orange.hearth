package com.orangehearth.OrangeHearth.dto.response;

import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;

public record RespuestaVeterinario(
	Long id,
	String nombreCompleto,
	String correo,
	String telefono,
	String tarjetaProfesional,
	String especialidad,
	Integer aniosExperiencia,
	EstadoCuenta estado
) { }
