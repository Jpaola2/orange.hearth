package com.orangehearth.OrangeHearth.dto.response;

import java.time.LocalDateTime;

import com.orangehearth.OrangeHearth.model.enums.EstadoCita;

public record RespuestaCita(
	Long id,
	Long tutorId,
	String tutorNombre,
	String tutorCorreo,
	Long mascotaId,
	String mascotaNombre,
	String mascotaEspecie,
	Long veterinarioId,
	String veterinarioNombre,
	String veterinarioEspecialidad,
	LocalDateTime fechaHora,
	EstadoCita estado,
	String motivo
) { }

