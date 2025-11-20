package com.orangehearth.OrangeHearth.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record SolicitudCreacionCita(
	@NotNull(message = "El tutor es obligatorio")
	Long tutorId,
	@NotNull(message = "La mascota es obligatoria")
	Long mascotaId,
	@NotNull(message = "El veterinario es obligatorio")
	Long veterinarioId,
	@NotNull(message = "La fecha y hora son obligatorias")
	LocalDateTime fechaHora,
	String motivo
) { }

