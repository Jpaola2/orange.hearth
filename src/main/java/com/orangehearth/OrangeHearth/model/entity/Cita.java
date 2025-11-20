package com.orangehearth.OrangeHearth.model.entity;

import java.time.LocalDateTime;

import com.orangehearth.OrangeHearth.model.enums.EstadoCita;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "citas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cita extends EntidadAuditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "tutor_id", nullable = false)
	private Tutor tutor;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "mascota_id", nullable = false)
	private Mascota mascota;

	// ID de la tabla legacy medico_veterinario
	@Column(name = "veterinario_id", nullable = false)
	private Long veterinarioId;

	@Column(name = "veterinario_nombre", nullable = false)
	private String veterinarioNombre;

	@Column(name = "veterinario_especialidad")
	private String veterinarioEspecialidad;

	@Column(name = "fecha_hora", nullable = false)
	private LocalDateTime fechaHora;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado", nullable = false)
	private EstadoCita estado;

	@Column(name = "motivo")
	private String motivo;
}

