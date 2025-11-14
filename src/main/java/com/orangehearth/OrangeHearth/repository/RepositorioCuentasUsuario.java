package com.orangehearth.OrangeHearth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orangehearth.OrangeHearth.model.entity.CuentaUsuario;

public interface RepositorioCuentasUsuario extends JpaRepository<CuentaUsuario, Long> {
	Optional<CuentaUsuario> findByEmailIgnoreCase(String email);
	boolean existsByEmailIgnoreCase(String email);
}
