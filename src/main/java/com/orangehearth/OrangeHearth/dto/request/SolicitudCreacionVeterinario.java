package com.orangehearth.OrangeHearth.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SolicitudCreacionVeterinario(
    @NotBlank(message = "El nombre es obligatorio")
    String nombreCompleto,
    @Email(message = "Correo inválido")
    String correo,
    @Pattern(regexp = "^\\d{10}$", message = "El teléfono debe tener 10 dígitos")
    String telefono,
    @NotBlank(message = "La tarjeta profesional es obligatoria")
    String tarjetaProfesional,
    String especialidad,
    @Min(value = 0, message = "Los años de experiencia no pueden ser negativos")
    Integer aniosExperiencia,
    @NotBlank(message = "La contraseña es obligatoria")
    String password,
    @NotBlank(message = "La cédula es obligatoria")
    @Pattern(regexp = "^\\d{8,13}$", message = "La cédula debe tener entre 8 y 13 dígitos")
    @JsonAlias({"doc_numero", "docNumero"})
    String cedula,
    @JsonAlias({"doc_tipo", "docTipo"})
    String docTipo,
    @NotBlank(message = "La pregunta de seguridad es obligatoria")
    String securityQuestion,
    @NotBlank(message = "La respuesta de seguridad es obligatoria")
    String securityAnswer
) { }
