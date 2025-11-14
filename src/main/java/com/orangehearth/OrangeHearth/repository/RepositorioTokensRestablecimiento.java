package com.orangehearth.OrangeHearth.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orangehearth.OrangeHearth.model.entity.TokenRestablecimientoContrasena;

public interface RepositorioTokensRestablecimiento extends JpaRepository<TokenRestablecimientoContrasena, Long> {
	Optional<TokenRestablecimientoContrasena> findByTokenAndUsedAtIsNull(String token);
	void deleteByExpiresAtBefore(LocalDateTime expirationThreshold);
}
