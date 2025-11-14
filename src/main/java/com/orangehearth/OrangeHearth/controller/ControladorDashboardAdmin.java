package com.orangehearth.OrangeHearth.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.orangehearth.OrangeHearth.dto.request.FiltrosTutoresAdmin;
import com.orangehearth.OrangeHearth.dto.response.ResumenTutorAdmin;
import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;
import com.orangehearth.OrangeHearth.service.ServicioTutoresAdmin;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ControladorDashboardAdmin {

	private final ServicioTutoresAdmin servicioTutoresAdmin;

	@GetMapping({"/dashboardAdmin", "/dashboardAdmin.html"})
	public String verDashboardAdmin(
		@RequestParam(name = "q", required = false) String texto,
		@RequestParam(name = "estado", required = false) String estado,
		@RequestParam(name = "documento", required = false) String documento,
		@RequestParam(name = "minMascotas", required = false) Integer minimoMascotas,
		@RequestParam(name = "especie", required = false) String especie,
		Model model
	) {
		EstadoCuenta estadoFiltro = null;
		if (estado != null && !estado.isBlank()) {
			try {
				estadoFiltro = EstadoCuenta.valueOf(estado.toUpperCase());
			} catch (IllegalArgumentException ignored) {
				estadoFiltro = null;
			}
		}

		FiltrosTutoresAdmin filtros = new FiltrosTutoresAdmin(
			texto,
			estadoFiltro,
			documento,
			minimoMascotas,
			especie
		);

		List<ResumenTutorAdmin> tutores = servicioTutoresAdmin.listarTutoresConFiltros(filtros);
		model.addAttribute("tutores", tutores);
		model.addAttribute("filtros", filtros);

		return "dashboardAdmin";
	}
}

