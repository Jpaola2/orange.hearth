package com.orangehearth.OrangeHearth.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orangehearth.OrangeHearth.dto.request.SolicitudCreacionCita;
import com.orangehearth.OrangeHearth.dto.request.SolicitudActualizacionCita;
import com.orangehearth.OrangeHearth.dto.response.RespuestaCita;
import com.orangehearth.OrangeHearth.dto.response.RespuestaVeterinario;
import com.orangehearth.OrangeHearth.exception.ExcepcionAccionNoAutorizada;
import com.orangehearth.OrangeHearth.exception.ExcepcionRecursoNoEncontrado;
import com.orangehearth.OrangeHearth.model.entity.Cita;
import com.orangehearth.OrangeHearth.model.entity.Mascota;
import com.orangehearth.OrangeHearth.model.entity.Tutor;
import com.orangehearth.OrangeHearth.model.enums.EstadoCita;
import com.orangehearth.OrangeHearth.repository.RepositorioCitas;
import com.orangehearth.OrangeHearth.repository.RepositorioMascotas;
import com.orangehearth.OrangeHearth.repository.RepositorioTutores;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicioCitas {

	private final RepositorioCitas citaRepository;
	private final RepositorioTutores tutorRepository;
	private final RepositorioMascotas mascotaRepository;
	private final ServicioVeterinarios veterinariosService;

	@Transactional
	public RespuestaCita crear(SolicitudCreacionCita request) {
		Tutor tutor = tutorRepository.findById(request.tutorId())
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("Tutor no encontrado"));

		Mascota mascota = mascotaRepository.findById(request.mascotaId())
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("Mascota no encontrada"));

		RespuestaVeterinario vet = veterinariosService.getById(request.veterinarioId());

		Cita cita = Cita.builder()
			.tutor(tutor)
			.mascota(mascota)
			.veterinarioId(request.veterinarioId())
			.veterinarioNombre(vet.nombreCompleto())
			.veterinarioEspecialidad(vet.especialidad())
			.fechaHora(request.fechaHora())
			.estado(EstadoCita.PROGRAMADA)
			.motivo(request.motivo())
			.build();

		Cita saved = citaRepository.save(cita);
		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<RespuestaCita> listarTodas() {
		return citaRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public List<RespuestaCita> listarPorTutor(Long tutorId) {
		return citaRepository.findByTutorId(tutorId).stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public List<RespuestaCita> listarPorVeterinario(Long veterinarioId) {
		return citaRepository.findByVeterinarioId(veterinarioId).stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public long contarPendientes() {
		return citaRepository.findAll().stream()
			.filter(c -> c.getEstado() == EstadoCita.PROGRAMADA || c.getEstado() == EstadoCita.CONFIRMADA)
			.count();
	}

	@Transactional(readOnly = true)
	public long contarTotales() {
		return citaRepository.count();
	}

	@Transactional
	public RespuestaCita actualizar(Long citaId, SolicitudActualizacionCita request) {
		Cita cita = citaRepository.findById(citaId)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("Cita no encontrada"));

		boolean esAdmin = Boolean.TRUE.equals(request.admin());
		Long tutorId = request.tutorId();
		Long veterinarioId = request.veterinarioId();

		if (!esAdmin) {
			if (tutorId != null) {
				if (!cita.getTutor().getId().equals(tutorId)) {
					throw new ExcepcionAccionNoAutorizada("El tutor no puede gestionar citas de otros tutores.");
				}
			}
			else if (veterinarioId != null) {
				if (!cita.getVeterinarioId().equals(veterinarioId)) {
					throw new ExcepcionAccionNoAutorizada("El veterinario no puede gestionar citas de otros profesionales.");
				}
			}
			else {
				throw new ExcepcionAccionNoAutorizada("No se identificó el actor que intenta modificar la cita.");
			}
		}

		if (request.nuevaFechaHora() != null) {
			cita.setFechaHora(request.nuevaFechaHora());
		}
		if (request.nuevoEstado() != null) {
			cita.setEstado(request.nuevoEstado());
		}

		Cita updated = citaRepository.save(cita);
		return toResponse(updated);
	}

	private RespuestaCita toResponse(Cita cita) {
		return new RespuestaCita(
			cita.getId(),
			cita.getTutor().getId(),
			cita.getTutor().getCuentaUsuario().getFullName(),
			cita.getTutor().getCuentaUsuario().getEmail(),
			cita.getMascota().getId(),
			cita.getMascota().getNombre(),
			cita.getMascota().getEspecie(),
			cita.getVeterinarioId(),
			cita.getVeterinarioNombre(),
			cita.getVeterinarioEspecialidad(),
			cita.getFechaHora(),
			cita.getEstado(),
			cita.getMotivo()
		);
	}
}
