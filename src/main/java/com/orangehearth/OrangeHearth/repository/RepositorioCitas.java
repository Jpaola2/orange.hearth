package com.orangehearth.OrangeHearth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orangehearth.OrangeHearth.model.entity.Cita;

public interface RepositorioCitas extends JpaRepository<Cita, Long> {

	List<Cita> findByTutorId(Long tutorId);

	List<Cita> findByVeterinarioId(Long veterinarioId);
}

