package com.orangehearth.OrangeHearth.dto.request;

import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;

public record FiltrosTutoresAdmin(
	String texto,
	EstadoCuenta estado,
	String documento,
	Integer minimoMascotas,
	String especie
) { }

