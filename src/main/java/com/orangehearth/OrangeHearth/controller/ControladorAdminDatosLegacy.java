package com.orangehearth.OrangeHearth.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orangehearth.OrangeHearth.dto.response.RespuestaTutor;
import com.orangehearth.OrangeHearth.dto.response.RespuestaVeterinario;
import com.orangehearth.OrangeHearth.dto.request.SolicitudActualizacionVeterinarioLegacy;
import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;
import com.orangehearth.OrangeHearth.service.ServicioAutenticacionAdmin;
import com.orangehearth.OrangeHearth.service.ServicioDatosLegacy;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/legacy")
@Validated
@RequiredArgsConstructor
public class ControladorAdminDatosLegacy {

    private final ServicioAutenticacionAdmin adminAuthService;
    private final ServicioDatosLegacy datosLegacy;

    @GetMapping("/tutores")
    public List<RespuestaTutor> tutores(@RequestHeader("X-ADMIN-TOKEN") String token) {
        adminAuthService.validateSessionToken(token);
        return datosLegacy.listarTutoresLegacy();
    }

    @PatchMapping("/tutores/{id}/estado")
    public RespuestaTutor actualizarEstadoTutor(
        @RequestHeader("X-ADMIN-TOKEN") String token,
        @org.springframework.web.bind.annotation.PathVariable Long id,
        @org.springframework.web.bind.annotation.RequestBody java.util.Map<String,String> body
    ) {
        adminAuthService.validateSessionToken(token);
        EstadoCuenta estado = EstadoCuenta.valueOf(body.getOrDefault("estado", "ACTIVE"));
        return datosLegacy.actualizarEstadoTutorLegacy(id, estado);
    }

    @GetMapping("/veterinarios")
    public List<RespuestaVeterinario> veterinarios(@RequestHeader("X-ADMIN-TOKEN") String token) {
        adminAuthService.validateSessionToken(token);
        return datosLegacy.listarVeterinariosLegacy();
    }

    @GetMapping("/veterinarios/{id}")
    public RespuestaVeterinario veterinarioPorId(
        @RequestHeader("X-ADMIN-TOKEN") String token,
        @org.springframework.web.bind.annotation.PathVariable Long id
    ) {
        adminAuthService.validateSessionToken(token);
        return datosLegacy.buscarVeterinarioLegacyPorId(id);
    }

    @PutMapping("/veterinarios/{id}")
    public RespuestaVeterinario actualizarVeterinario(
        @RequestHeader("X-ADMIN-TOKEN") String token,
        @org.springframework.web.bind.annotation.PathVariable Long id,
        @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid SolicitudActualizacionVeterinarioLegacy req
    ) {
        adminAuthService.validateSessionToken(token);
        return datosLegacy.actualizarVeterinarioLegacy(id, req.nombreCompleto(), req.correo(), req.telefono(), req.especialidad(), req.tarjetaProfesional(), req.cedula());
    }

    @PatchMapping("/veterinarios/{id}/estado")
    public RespuestaVeterinario actualizarEstado(
        @RequestHeader("X-ADMIN-TOKEN") String token,
        @org.springframework.web.bind.annotation.PathVariable Long id,
        @org.springframework.web.bind.annotation.RequestBody java.util.Map<String,String> body
    ) {
        adminAuthService.validateSessionToken(token);
        EstadoCuenta estado = EstadoCuenta.valueOf(body.getOrDefault("estado", "ACTIVE"));
        return datosLegacy.actualizarEstadoVeterinarioLegacy(id, estado);
    }
}
