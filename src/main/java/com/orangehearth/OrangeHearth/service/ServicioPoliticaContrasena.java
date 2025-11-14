package com.orangehearth.OrangeHearth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.orangehearth.OrangeHearth.exception.ExcepcionValidacion;

@Component
public class ServicioPoliticaContrasena {

	private static final Pattern UPPER = Pattern.compile("[A-Z]");
	private static final Pattern LOWER = Pattern.compile("[a-z]");
	private static final Pattern DIGIT = Pattern.compile("[0-9]");
	private static final Pattern SYMBOL = Pattern.compile("[!@#$%^&*()_+=\\-\\[\\]{}|;:'\",.<>/?`~]");

	public void validateOrThrow(String password) {
		List<String> errors = new ArrayList<>();

		if (password == null || password.length() < 8) {
			errors.add("Mínimo 8 caracteres.");
		}
		if (password != null) {
			if (!UPPER.matcher(password).find()) {
				errors.add("Debe incluir una mayúscula.");
			}
			if (!LOWER.matcher(password).find()) {
				errors.add("Debe incluir una minúscula.");
			}
			if (!DIGIT.matcher(password).find()) {
				errors.add("Debe incluir un número.");
			}
			if (!SYMBOL.matcher(password).find()) {
				errors.add("Debe incluir un símbolo.");
			}
		}

		if (!errors.isEmpty()) {
			throw new ExcepcionValidacion("Contraseña inválida: " + String.join(" ", errors));
		}
	}
}
