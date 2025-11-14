package com.orangehearth.OrangeHearth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SolicitudDireccion(
	String via,
	String numeroPrincipal,
	String letraUno,
	String bis,
	String letraDos,
	String cardinal,
	String numeroSecundario,
	String numeroPlaca,
	String adicional,
	@NotBlank(message = "La dirección completa es obligatoria")
	String completo
) { }
