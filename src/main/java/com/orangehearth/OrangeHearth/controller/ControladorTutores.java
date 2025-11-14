package com.orangehearth.OrangeHearth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.orangehearth.OrangeHearth.dto.request.SolicitudRegistroTutor;
import com.orangehearth.OrangeHearth.dto.request.SolicitudActualizacionTutor;
import com.orangehearth.OrangeHearth.dto.response.RespuestaTutor;
import com.orangehearth.OrangeHearth.service.ServicioTutores;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tutores")
@Validated
@RequiredArgsConstructor
public class ControladorTutores {

	private final ServicioTutores tutorService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RespuestaTutor register(@Valid @RequestBody SolicitudRegistroTutor request) {
		return tutorService.register(request);
	}

	@GetMapping
	public List<RespuestaTutor> list() {
		return tutorService.findAll();
	}

	@GetMapping("/{id}")
	public RespuestaTutor get(@PathVariable Long id) {
		return tutorService.findById(id);
	}

	@PutMapping("/{id}")
	public RespuestaTutor update(@PathVariable Long id, @Valid @RequestBody SolicitudActualizacionTutor request) {
		return tutorService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		tutorService.delete(id);
	}
}
