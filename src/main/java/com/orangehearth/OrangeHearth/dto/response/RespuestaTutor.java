package com.orangehearth.OrangeHearth.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;

public record RespuestaTutor(
	Long id,
	String nombreCompleto,
	String correo,
	String telefono,
	String documento,
	String direccionCompleta,
	List<RespuestaMascota> mascotas,
	EstadoCuenta estado,
	LocalDateTime createdAt
) { }
