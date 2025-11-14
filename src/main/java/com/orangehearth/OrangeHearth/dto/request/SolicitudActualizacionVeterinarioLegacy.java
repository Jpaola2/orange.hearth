package com.orangehearth.OrangeHearth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitudActualizacionVeterinarioLegacy(
    @NotBlank String nombreCompleto,
    @Email @NotBlank String correo,
    @NotBlank String telefono,
    @NotBlank String especialidad,
    @NotBlank String tarjetaProfesional,
    String cedula
) {}
