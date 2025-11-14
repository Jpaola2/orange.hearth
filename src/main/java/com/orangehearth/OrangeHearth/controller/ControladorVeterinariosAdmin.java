package com.orangehearth.OrangeHearth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.orangehearth.OrangeHearth.dto.request.SolicitudCreacionVeterinario;
import com.orangehearth.OrangeHearth.dto.request.SolicitudActualizacionVeterinario;
import com.orangehearth.OrangeHearth.dto.request.SolicitudEstadoVeterinario;
import com.orangehearth.OrangeHearth.dto.response.RespuestaVeterinario;
import com.orangehearth.OrangeHearth.service.ServicioAutenticacionAdmin;
import com.orangehearth.OrangeHearth.service.ServicioVeterinarios;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/veterinarios")
@Validated
@RequiredArgsConstructor
public class ControladorVeterinariosAdmin {

	private final ServicioAutenticacionAdmin adminServicioAutenticacion;
	private final ServicioVeterinarios veterinarianService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RespuestaVeterinario createVeterinarian(
		@RequestHeader("X-ADMIN-TOKEN") String adminToken,
		@Valid @RequestBody SolicitudCreacionVeterinario request
	) {
		adminServicioAutenticacion.validateSessionToken(adminToken);
		return veterinarianService.create(request);
	}

	@GetMapping
	public List<RespuestaVeterinario> listVeterinarians(
		@RequestHeader("X-ADMIN-TOKEN") String adminToken
	) {
		adminServicioAutenticacion.validateSessionToken(adminToken);
		return veterinarianService.findAll();
	}

	@GetMapping("/{id}")
	public RespuestaVeterinario getVeterinarian(
		@RequestHeader("X-ADMIN-TOKEN") String adminToken,
		@PathVariable Long id
	) {
		adminServicioAutenticacion.validateSessionToken(adminToken);
		return veterinarianService.getById(id);
	}

	@PutMapping("/{id}")
	public RespuestaVeterinario updateVeterinarian(
		@RequestHeader("X-ADMIN-TOKEN") String adminToken,
		@PathVariable Long id,
		@Valid @RequestBody SolicitudActualizacionVeterinario request
	) {
		adminServicioAutenticacion.validateSessionToken(adminToken);
		return veterinarianService.update(id, request);
	}

	@PatchMapping("/{id}/status")
	public RespuestaVeterinario updateStatus(
		@RequestHeader("X-ADMIN-TOKEN") String adminToken,
		@PathVariable Long id,
		@Valid @RequestBody SolicitudEstadoVeterinario request
	) {
		adminServicioAutenticacion.validateSessionToken(adminToken);
		return veterinarianService.updateStatus(id, request.status());
	}
}
