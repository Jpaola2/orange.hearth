package com.orangehearth.OrangeHearth.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orangehearth.OrangeHearth.dto.response.RespuestaVeterinario;
import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;
import com.orangehearth.OrangeHearth.service.ServicioVeterinarios;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/veterinarios")
@Validated
@RequiredArgsConstructor
public class ControladorVeterinariosPublico {

	private final ServicioVeterinarios servicioVeterinarios;

	@GetMapping
	public List<RespuestaVeterinario> listarActivos() {
		return servicioVeterinarios.findAll().stream()
			.filter(v -> v.estado() == EstadoCuenta.ACTIVE)
			.toList();
	}
}

