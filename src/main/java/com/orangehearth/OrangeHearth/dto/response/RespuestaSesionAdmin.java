package com.orangehearth.OrangeHearth.dto.response;

public record RespuestaSesionAdmin(
	String mensaje,
	String token,
	long expiresInMinutes
) { }
