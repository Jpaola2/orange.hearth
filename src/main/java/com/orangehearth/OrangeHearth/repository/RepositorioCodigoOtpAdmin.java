package com.orangehearth.OrangeHearth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orangehearth.OrangeHearth.model.entity.CodigoOtpAdmin;

public interface RepositorioCodigoOtpAdmin extends JpaRepository<CodigoOtpAdmin, Long> {
	Optional<CodigoOtpAdmin> findTopByAdminAccountIdAndConsumedFalseOrderByCreatedAtDesc(Long adminId);
}
