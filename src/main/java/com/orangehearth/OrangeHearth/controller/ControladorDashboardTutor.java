package com.orangehearth.OrangeHearth.controller;

import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.orangehearth.OrangeHearth.model.entity.Mascota;
import com.orangehearth.OrangeHearth.model.entity.Tutor;
import com.orangehearth.OrangeHearth.repository.RepositorioTutores;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ControladorDashboardTutor {

	private final RepositorioTutores tutorRepository;

	@GetMapping({"/dashboardTutor", "/dashboardTutor.html"})
	@Transactional(readOnly = true)
	public String verDashboardTutor(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
			String email = auth.getName();
			tutorRepository.findByUserAccountEmailIgnoreCase(email).ifPresent(tutor -> {
				List<Mascota> mascotas = tutor.getMascotas();
				model.addAttribute("mascotas", mascotas);
				model.addAttribute("nombreTutor",
					tutor.getCuentaUsuario() != null ? tutor.getCuentaUsuario().getFullName() : "");
			});
		}
		return "dashboardTutor";
	}
}

