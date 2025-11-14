package com.orangehearth.OrangeHearth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orangehearth.OrangeHearth.model.entity.Tutor;

public interface RepositorioTutores extends JpaRepository<Tutor, Long> {
	Optional<Tutor> findByDocumentNumber(String documentNumber);
	boolean existsByDocumentNumber(String documentNumber);
}
