package com.orangehearth.OrangeHearth.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SolicitudMascota(
	@NotBlank(message = "El nombre de la mascota es obligatorio")
	String nombre,
	@NotBlank(message = "La especie es obligatoria")
	String especie,
	@NotBlank(message = "La raza es obligatoria")
	String raza,
	@Min(value = 1, message = "La edad debe ser mayor o igual a 1")
	Integer edadValor,
	@NotBlank(message = "La unidad de edad es obligatoria")
	String edadUnidad
) { }
