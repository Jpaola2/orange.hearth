package com.orangehearth.OrangeHearth.config;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.orangehearth.OrangeHearth.model.entity.CuentaUsuario;
import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;
import com.orangehearth.OrangeHearth.model.enums.Rol;
import com.orangehearth.OrangeHearth.repository.RepositorioCuentasUsuario;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InicializadorDatos implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(InicializadorDatos.class);

	private final RepositorioCuentasUsuario userAccountRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.admin.email}")
	private String adminEmail;

	@Override
	public void run(String... args) {
		userAccountRepository.findByEmailIgnoreCase(adminEmail).ifPresentOrElse(
			admin -> log.info("Cuenta de administrador existente detectada ({})", adminEmail),
			() -> {
				CuentaUsuario admin = CuentaUsuario.builder()
					.fullName("Gerencia OrangeHearth")
					.email(adminEmail)
					.password(passwordEncoder.encode(UUID.randomUUID().toString()))
					.role(Rol.ADMIN)
					.status(EstadoCuenta.ACTIVE)
					.passwordUpdatedAt(LocalDateTime.now())
					.build();
				userAccountRepository.save(admin);
				log.info("Cuenta de administrador creada para {}", adminEmail);
			}
		);
	}
}
