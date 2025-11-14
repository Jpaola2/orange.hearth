package com.orangehearth.OrangeHearth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orangehearth.OrangeHearth.model.entity.Mascota;

public interface RepositorioMascotas extends JpaRepository<Mascota, Long> {
	List<Mascota> findByTutorId(Long tutorId);
}
