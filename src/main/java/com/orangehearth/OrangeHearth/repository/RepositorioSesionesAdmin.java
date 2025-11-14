package com.orangehearth.OrangeHearth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orangehearth.OrangeHearth.model.entity.SesionAdmin;

public interface RepositorioSesionesAdmin extends JpaRepository<SesionAdmin, Long> {
	Optional<SesionAdmin> findByTokenAndRevokedFalse(String token);
}
