package com.orangehearth.OrangeHearth.dto.response;

import com.orangehearth.OrangeHearth.model.enums.Rol;

public record RespuestaAutenticacion(
	String mensaje,
	Rol rol,
	String nombre,
	String token
) {
	public static RespuestaAutenticacion of(String mensaje, Rol rol, String nombre) {
		return new RespuestaAutenticacion(mensaje, rol, nombre, null);
	}

	public RespuestaAutenticacion withToken(String sessionToken) {
		return new RespuestaAutenticacion(mensaje, rol, nombre, sessionToken);
	}
}
