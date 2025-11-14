package com.orangehearth.OrangeHearth.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orangehearth.OrangeHearth.dto.request.SolicitudMascota;
import com.orangehearth.OrangeHearth.dto.response.RespuestaMascota;
import com.orangehearth.OrangeHearth.exception.ExcepcionRecursoNoEncontrado;
import com.orangehearth.OrangeHearth.model.entity.Mascota;
import com.orangehearth.OrangeHearth.model.entity.Tutor;
import com.orangehearth.OrangeHearth.repository.RepositorioMascotas;
import com.orangehearth.OrangeHearth.repository.RepositorioTutores;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicioMascotas {

	private final RepositorioMascotas mascotaRepository;
	private final RepositorioTutores tutorRepository;

	@Transactional
	public RespuestaMascota addMascota(Long tutorId, SolicitudMascota request) {
		Tutor tutor = tutorRepository.findById(tutorId)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("Tutor no encontrado"));

		Mascota mascota = Mascota.builder()
			.nombre(request.nombre())
			.especie(request.especie())
			.raza(request.raza())
			.edadValor(request.edadValor())
			.edadUnidad(request.edadUnidad())
			.tutor(tutor)
			.build();

		Mascota saved = mascotaRepository.save(mascota);
		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<RespuestaMascota> findByTutor(Long tutorId) {
		return mascotaRepository.findByTutorId(tutorId).stream()
			.map(this::toResponse)
			.toList();
	}

	@Transactional
	public void delete(Long mascotaId) {
		Mascota mascota = mascotaRepository.findById(mascotaId)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("Mascota no encontrada"));
		mascotaRepository.delete(mascota);
	}

	private RespuestaMascota toResponse(Mascota mascota) {
		return new RespuestaMascota(
			mascota.getId(),
			mascota.getNombre(),
			mascota.getEspecie(),
			mascota.getRaza(),
			mascota.getEdadValor(),
			mascota.getEdadUnidad()
		);
	}
}
