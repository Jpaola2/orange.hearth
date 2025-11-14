package com.orangehearth.OrangeHearth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orangehearth.OrangeHearth.model.entity.Veterinarian;

public interface RepositorioVeterinarios extends JpaRepository<Veterinarian, Long> {
	Optional<Veterinarian> findByProfessionalLicense(String professionalLicense);
	Optional<Veterinarian> findByUserAccountId(Long userAccountId);
}
