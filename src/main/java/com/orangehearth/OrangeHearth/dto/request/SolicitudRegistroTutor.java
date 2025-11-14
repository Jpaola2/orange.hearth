package com.orangehearth.OrangeHearth.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SolicitudRegistroTutor(
	@NotBlank(message = "El nombre completo es obligatorio")
	String nombreCompleto,
	@NotBlank(message = "El tipo de documento es obligatorio")
	String tipoDocumento,
	@Size(min = 2, max = 10, message = "El documento debe tener entre 2 y 10 dígitos")
	String numeroDocumento,
	@Pattern(regexp = "^\\d{10}$", message = "El teléfono debe tener 10 dígitos")
	String telefono,
	@Email(message = "Correo inválido")
	@NotBlank(message = "El correo es obligatorio")
	String correo,
	@NotBlank(message = "La contraseña es obligatoria")
	String password,
	@Valid SolicitudDireccion direccion,
	@Valid SolicitudMascota mascota
) { }
