package com.orangehearth.OrangeHearth.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orangehearth.OrangeHearth.dto.request.FiltrosTutoresAdmin;
import com.orangehearth.OrangeHearth.dto.response.ResumenTutorAdmin;
import com.orangehearth.OrangeHearth.model.entity.Mascota;
import com.orangehearth.OrangeHearth.model.entity.Tutor;
import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;
import com.orangehearth.OrangeHearth.repository.RepositorioTutores;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicioTutoresAdmin {

	private final RepositorioTutores tutorRepository;

	@Transactional(readOnly = true)
	public List<ResumenTutorAdmin> listarTutoresConFiltros(FiltrosTutoresAdmin filtros) {
		List<Tutor> tutores = tutorRepository.findAll();

		return tutores.stream()
			.filter(tutor -> cumpleFiltros(tutor, filtros))
			.map(this::mapToResumen)
			.toList();
	}

	private boolean cumpleFiltros(Tutor tutor, FiltrosTutoresAdmin filtros) {
		if (filtros == null) {
			return true;
		}

		String texto = normalizar(filtros.texto());
		String documentoFiltro = normalizar(filtros.documento());
		String especieFiltro = normalizar(filtros.especie());
		Integer minimoMascotas = filtros.minimoMascotas();
		EstadoCuenta estadoFiltro = filtros.estado();

		String nombreCompleto = normalizar(tutor.getCuentaUsuario().getFullName());
		String email = normalizar(tutor.getCuentaUsuario().getEmail());
		String documentoTutor = normalizar(tutor.getDocumentNumber());
		EstadoCuenta estadoTutor = tutor.getCuentaUsuario().getStatus();

		List<Mascota> mascotas = tutor.getMascotas();
		int numeroMascotas = mascotas != null ? mascotas.size() : 0;

		// Filtro texto: nombre o email
		if (texto != null && !texto.isEmpty()) {
			boolean coincideTexto = (nombreCompleto != null && nombreCompleto.contains(texto))
				|| (email != null && email.contains(texto));
			if (!coincideTexto) {
				return false;
			}
		}

		// Filtro estado
		if (estadoFiltro != null && estadoTutor != null && estadoTutor != estadoFiltro) {
			return false;
		}

		// Filtro documento
		if (documentoFiltro != null && !documentoFiltro.isEmpty()) {
			if (documentoTutor == null || !documentoTutor.contains(documentoFiltro)) {
				return false;
			}
		}

		// Filtro mínimo de mascotas
		if (minimoMascotas != null && numeroMascotas < minimoMascotas) {
			return false;
		}

		// Filtro por especie
		if (especieFiltro != null && !especieFiltro.isEmpty() && mascotas != null && !mascotas.isEmpty()) {
			boolean tieneEspecie = mascotas.stream()
				.map(Mascota::getEspecie)
				.map(this::normalizar)
				.anyMatch(especie -> especie != null && especie.equals(especieFiltro));
			if (!tieneEspecie) {
				return false;
			}
		}

		return true;
	}

	private ResumenTutorAdmin mapToResumen(Tutor tutor) {
		String nombreCompleto = tutor.getCuentaUsuario().getFullName();
		String email = tutor.getCuentaUsuario().getEmail();
		String telefono = tutor.getPhoneNumber();
		String direccionCompleta = tutor.getDireccion() != null ? tutor.getDireccion().getCompleto() : null;

		List<Mascota> mascotas = tutor.getMascotas();
		int numeroMascotas = mascotas != null ? mascotas.size() : 0;
		String nombresMascotas = mascotas == null ? "" : mascotas.stream()
			.map(Mascota::getNombre)
			.filter(nombre -> nombre != null && !nombre.isBlank())
			.reduce((a, b) -> a + ", " + b)
			.orElse("");

		String estado = tutor.getCuentaUsuario().getStatus() != null
			? tutor.getCuentaUsuario().getStatus().name()
			: null;

		return new ResumenTutorAdmin(
			tutor.getId(),
			nombreCompleto,
			email,
			telefono,
			direccionCompleta,
			numeroMascotas,
			nombresMascotas,
			estado
		);
	}

	private String normalizar(String valor) {
		return valor == null ? null : valor.toLowerCase(Locale.ROOT).trim();
	}
}

