package com.orangehearth.OrangeHearth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitudActualizacionVeterinario(
    @NotBlank(message = "El nombre es obligatorio")
    String nombreCompleto,
    @Email(message = "Correo inválido")
    String correo,
    @NotBlank(message = "El teléfono es obligatorio")
    String telefono,
    @NotBlank(message = "La tarjeta profesional es obligatoria")
    String tarjetaProfesional,
    String especialidad
) {}

