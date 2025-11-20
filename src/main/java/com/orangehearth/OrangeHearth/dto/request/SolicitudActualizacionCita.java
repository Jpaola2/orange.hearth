package com.orangehearth.OrangeHearth.dto.request;

import java.time.LocalDateTime;

import com.orangehearth.OrangeHearth.model.enums.EstadoCita;

public record SolicitudActualizacionCita(
	Long tutorId,
	Long veterinarioId,
	Boolean admin,
	LocalDateTime nuevaFechaHora,
	EstadoCita nuevoEstado
) { }

