package com.orangehearth.OrangeHearth.controller.advice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.orangehearth.OrangeHearth.dto.response.ErrorApi;
import com.orangehearth.OrangeHearth.exception.ExcepcionRecursoNoEncontrado;
import com.orangehearth.OrangeHearth.exception.ExcepcionAccionNoAutorizada;
import com.orangehearth.OrangeHearth.exception.ExcepcionValidacion;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ManejadorExcepcionesGlobales {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorApi> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return buildResponse(HttpStatus.BAD_REQUEST, "Datos inválidos", ex.getMessage(), request.getRequestURI(), errors);
	}

	@ExceptionHandler(ExcepcionValidacion.class)
	public ResponseEntity<ErrorApi> handleBusinessValidation(ExcepcionValidacion ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.BAD_REQUEST, "Validación", ex.getMessage(), request.getRequestURI(), null);
	}

	@ExceptionHandler(ExcepcionRecursoNoEncontrado.class)
	public ResponseEntity<ErrorApi> handleNotFound(ExcepcionRecursoNoEncontrado ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.NOT_FOUND, "No encontrado", ex.getMessage(), request.getRequestURI(), null);
	}

	@ExceptionHandler(ExcepcionAccionNoAutorizada.class)
	public ResponseEntity<ErrorApi> handleUnauthorized(ExcepcionAccionNoAutorizada ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.UNAUTHORIZED, "No autorizado", ex.getMessage(), request.getRequestURI(), null);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorApi> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", ex.getMessage(), request.getRequestURI(), null);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorApi> handleGeneric(Exception ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado", ex.getMessage(), request.getRequestURI(), null);
	}

	private ResponseEntity<ErrorApi> buildResponse(
		HttpStatus status,
		String error,
		String message,
		String path,
		Map<String, String> validationErrors
	) {
		ErrorApi apiError = new ErrorApi(
			Instant.now(),
			status.value(),
			error,
			message,
			path,
			validationErrors
		);
		return ResponseEntity.status(status).body(apiError);
	}
}
