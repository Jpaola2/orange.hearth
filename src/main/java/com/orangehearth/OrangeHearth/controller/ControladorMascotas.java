package com.orangehearth.OrangeHearth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.orangehearth.OrangeHearth.dto.request.SolicitudMascota;
import com.orangehearth.OrangeHearth.dto.response.RespuestaMascota;
import com.orangehearth.OrangeHearth.service.ServicioMascotas;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mascotas")
@Validated
@RequiredArgsConstructor
public class ControladorMascotas {

	private final ServicioMascotas mascotaService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RespuestaMascota addMascota(
		@RequestParam("tutorId") Long tutorId,
		@Valid @RequestBody SolicitudMascota request
	) {
		return mascotaService.addMascota(tutorId, request);
	}

	@GetMapping
	public List<RespuestaMascota> list(@RequestParam("tutorId") Long tutorId) {
		return mascotaService.findByTutor(tutorId);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		mascotaService.delete(id);
	}
}
