package com.orangehearth.OrangeHearth.controller;

import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.orangehearth.OrangeHearth.dto.request.SolicitudCodigoAdmin;
import com.orangehearth.OrangeHearth.dto.request.SolicitudVerificacionCodigoAdmin;
import com.orangehearth.OrangeHearth.dto.response.RespuestaSesionAdmin;
import com.orangehearth.OrangeHearth.service.ServicioAutenticacionAdmin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/auth")
@Validated
@RequiredArgsConstructor
public class ControladorAutenticacionAdmin {

	private final ServicioAutenticacionAdmin adminServicioAutenticacion;

    @PostMapping("/request-code")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String requestCode(@Valid @RequestBody SolicitudCodigoAdmin request, HttpServletRequest http) {
        String ip = http != null ? http.getRemoteAddr() : "localhost";
        adminServicioAutenticacion.requestAccessCode(request.correo(), ip);
        return "Código enviado al correo autorizado.";
    }

	@PostMapping("/verify-code")
	public RespuestaSesionAdmin verifyCode(@Valid @RequestBody SolicitudVerificacionCodigoAdmin request) {
		return adminServicioAutenticacion.verifyCode(request.correo(), request.codigo());
	}
}
