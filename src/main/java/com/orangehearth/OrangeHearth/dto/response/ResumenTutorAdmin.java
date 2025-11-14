package com.orangehearth.OrangeHearth.dto.response;

public record ResumenTutorAdmin(
	Long idTutor,
	String nombreCompleto,
	String email,
	String telefono,
	String direccionCompleta,
	Integer numeroMascotas,
	String nombresMascotas,
	String estado
) { }

