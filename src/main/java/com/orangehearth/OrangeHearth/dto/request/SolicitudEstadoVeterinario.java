package com.orangehearth.OrangeHearth.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;

import jakarta.validation.constraints.NotNull;

public record SolicitudEstadoVeterinario(
    @NotNull(message = "El estado es obligatorio")
    @JsonAlias({"estado", "status"})
    EstadoCuenta status
) { }
