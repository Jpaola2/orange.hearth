package com.orangehearth.OrangeHearth.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orangehearth.OrangeHearth.dto.request.SolicitudCreacionCita;
import com.orangehearth.OrangeHearth.dto.request.SolicitudActualizacionCita;
import com.orangehearth.OrangeHearth.dto.response.RespuestaCita;
import com.orangehearth.OrangeHearth.service.ServicioCitas;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/citas")
@Validated
@RequiredArgsConstructor
public class ControladorCitas {

	private final ServicioCitas servicioCitas;

	@PostMapping
	public RespuestaCita crear(@Valid @RequestBody SolicitudCreacionCita request) {
		return servicioCitas.crear(request);
	}

	@PatchMapping("/{id}")
	public RespuestaCita actualizar(
		@PathVariable Long id,
		@Valid @RequestBody SolicitudActualizacionCita request
	) {
		return servicioCitas.actualizar(id, request);
	}

	@GetMapping
	public List<RespuestaCita> listar(
		@RequestParam(name = "tutorId", required = false) Long tutorId,
		@RequestParam(name = "veterinarioId", required = false) Long veterinarioId
	) {
		if (tutorId != null) {
			return servicioCitas.listarPorTutor(tutorId);
		}
		if (veterinarioId != null) {
			return servicioCitas.listarPorVeterinario(veterinarioId);
		}
		return servicioCitas.listarTodas();
	}
}
