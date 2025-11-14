package com.orangehearth.OrangeHearth.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orangehearth.OrangeHearth.dto.request.SolicitudDireccion;
import com.orangehearth.OrangeHearth.dto.request.SolicitudMascota;
import com.orangehearth.OrangeHearth.dto.request.SolicitudRegistroTutor;
import com.orangehearth.OrangeHearth.dto.request.SolicitudActualizacionTutor;
import com.orangehearth.OrangeHearth.dto.response.RespuestaMascota;
import com.orangehearth.OrangeHearth.dto.response.RespuestaTutor;
import com.orangehearth.OrangeHearth.exception.ExcepcionRecursoNoEncontrado;
import com.orangehearth.OrangeHearth.exception.ExcepcionValidacion;
import com.orangehearth.OrangeHearth.model.entity.Mascota;
import com.orangehearth.OrangeHearth.model.entity.Tutor;
import com.orangehearth.OrangeHearth.model.entity.CuentaUsuario;
import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;
import com.orangehearth.OrangeHearth.model.enums.Rol;
import com.orangehearth.OrangeHearth.model.value.Direccion;
import com.orangehearth.OrangeHearth.repository.RepositorioTutores;
import com.orangehearth.OrangeHearth.repository.RepositorioCuentasUsuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicioTutores {

	private final RepositorioTutores tutorRepository;
	private final RepositorioCuentasUsuario userAccountRepository;
	private final PasswordEncoder passwordEncoder;
	private final ServicioPoliticaContrasena passwordPolicyService;

	@Transactional
	public RespuestaTutor register(SolicitudRegistroTutor request) {
		if (userAccountRepository.existsByEmailIgnoreCase(request.correo())) {
			throw new ExcepcionValidacion("El correo ya está registrado.");
		}
		if (tutorRepository.existsByDocumentNumber(request.numeroDocumento())) {
			throw new ExcepcionValidacion("El número de documento ya está registrado.");
		}

		passwordPolicyService.validateOrThrow(request.password());

		CuentaUsuario account = CuentaUsuario.builder()
			.fullName(request.nombreCompleto())
			.email(request.correo().toLowerCase())
			.password(passwordEncoder.encode(request.password()))
			.role(Rol.TUTOR)
			.status(EstadoCuenta.ACTIVE)
			.passwordChangeRequired(false)
			.passwordUpdatedAt(LocalDateTime.now())
			.build();

		Tutor tutor = Tutor.builder()
			.userAccount(account)
			.documentType(request.tipoDocumento())
			.documentNumber(request.numeroDocumento())
			.phoneNumber(request.telefono())
			.address(buildDireccion(request.direccion()))
			.build();

		Mascota mascota = buildMascota(request.mascota(), tutor);
		tutor.getMascotas().add(mascota);

		Tutor saved = tutorRepository.save(tutor);
		return mapToResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<RespuestaTutor> findAll() {
		return tutorRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Transactional(readOnly = true)
	public RespuestaTutor findById(Long id) {
		Tutor tutor = tutorRepository.findById(id)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("Tutor no encontrado"));
		return mapToResponse(tutor);
	}

	@Transactional
	public RespuestaTutor update(Long id, SolicitudActualizacionTutor request) {
		Tutor tutor = tutorRepository.findById(id)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("Tutor no encontrado"));

		if (request.telefono() != null) {
			tutor.setPhoneNumber(request.telefono());
		}
		if (request.direccion() != null) {
			tutor.setDireccion(buildDireccion(request.direccion()));
		}

		return mapToResponse(tutor);
	}

	@Transactional
	public void delete(Long id) {
		Tutor tutor = tutorRepository.findById(id)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("Tutor no encontrado"));
		tutorRepository.delete(tutor);
	}

	private Direccion buildDireccion(SolicitudDireccion request) {
		if (request == null) {
			return null;
		}
		return Direccion.builder()
			.via(request.via())
			.numeroPrincipal(request.numeroPrincipal())
			.letraUno(request.letraUno())
			.bis(request.bis())
			.letraDos(request.letraDos())
			.cardinal(request.cardinal())
			.numeroSecundario(request.numeroSecundario())
			.numeroPlaca(request.numeroPlaca())
			.adicional(request.adicional())
			.completo(request.completo())
			.build();
	}

	private Mascota buildMascota(SolicitudMascota request, Tutor tutor) {
		return Mascota.builder()
			.nombre(request.nombre())
			.especie(request.especie())
			.raza(request.raza())
			.edadValor(request.edadValor())
			.edadUnidad(request.edadUnidad())
			.tutor(tutor)
			.build();
	}

	private RespuestaTutor mapToResponse(Tutor tutor) {
		List<RespuestaMascota> mascotas = tutor.getMascotas().stream()
			.map(m -> new RespuestaMascota(
				m.getId(),
				m.getNombre(),
				m.getEspecie(),
				m.getRaza(),
				m.getEdadValor(),
				m.getEdadUnidad()
			)).toList();

		return new RespuestaTutor(
			tutor.getId(),
			tutor.getCuentaUsuario().getFullName(),
			tutor.getCuentaUsuario().getEmail(),
			tutor.getPhoneNumber(),
			tutor.getDocumentType() + " " + tutor.getDocumentNumber(),
			tutor.getDireccion() != null ? tutor.getDireccion().getCompleto() : "",
			mascotas,
			tutor.getCuentaUsuario().getStatus()
		);
	}
}
