package com.orangehearth.OrangeHearth.dto.response;

public record RespuestaMascota(
	Long id,
	String nombre,
	String especie,
	String raza,
	Integer edadValor,
	String edadUnidad
) { }
