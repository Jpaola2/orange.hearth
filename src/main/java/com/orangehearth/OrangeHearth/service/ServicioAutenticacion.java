package com.orangehearth.OrangeHearth.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orangehearth.OrangeHearth.dto.request.SolicitudInicioSesionTutor;
import com.orangehearth.OrangeHearth.dto.request.SolicitudInicioSesionVeterinario;
import com.orangehearth.OrangeHearth.dto.response.RespuestaAutenticacion;
import com.orangehearth.OrangeHearth.exception.ExcepcionValidacion;
import com.orangehearth.OrangeHearth.model.entity.CuentaUsuario;
import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;
import com.orangehearth.OrangeHearth.model.enums.Rol;
import com.orangehearth.OrangeHearth.repository.RepositorioCuentasUsuario;
import com.orangehearth.OrangeHearth.repository.RepositorioVeterinarios;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicioAutenticacion {

	private final RepositorioCuentasUsuario userAccountRepository;
	private final PasswordEncoder passwordEncoder;
	private final RepositorioVeterinarios veterinarianRepository;

	@Transactional
	public RespuestaAutenticacion loginTutor(SolicitudInicioSesionTutor request) {
		CuentaUsuario account = userAccountRepository.findByEmailIgnoreCase(request.correo())
			.filter(user -> user.getRol() == Rol.TUTOR)
			.orElseThrow(() -> new ExcepcionValidacion("Credenciales inválidas."));

		return authenticate(account, request.password());
	}

	@Transactional
	public RespuestaAutenticacion loginVeterinarian(SolicitudInicioSesionVeterinario request) {
		CuentaUsuario account = userAccountRepository.findByEmailIgnoreCase(request.correo())
			.filter(user -> user.getRol() == Rol.VETERINARIO)
			.orElseThrow(() -> new ExcepcionValidacion("Credenciales inválidas."));

		veterinarianRepository.findByUserAccountId(account.getId())
			.filter(vet -> vet.getProfessionalLicense().equalsIgnoreCase(request.tarjetaProfesional()))
			.orElseThrow(() -> new ExcepcionValidacion("La tarjeta profesional no coincide con el registro."));

		return authenticate(account, request.password());
	}

	private RespuestaAutenticacion authenticate(CuentaUsuario account, String rawPassword) {
		if (account.getStatus() != EstadoCuenta.ACTIVE) {
			throw new ExcepcionValidacion("La cuenta se encuentra inactiva.");
		}

		if (!passwordEncoder.matches(rawPassword, account.getPassword())) {
			throw new ExcepcionValidacion("Credenciales inválidas.");
		}

		account.setLastLoginAt(LocalDateTime.now());
		return RespuestaAutenticacion.of("Autenticación exitosa.", account.getRol(), account.getFullName());
	}
}
