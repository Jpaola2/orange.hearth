package com.orangehearth.OrangeHearth.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "mascotas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mascota extends EntidadAuditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nombre;

	@Column(nullable = false)
	private String especie;

	@Column(nullable = false)
	private String raza;

	@Column(name = "edad_valor", nullable = false)
	private Integer edadValor;

	@Column(name = "edad_unidad", nullable = false)
	private String edadUnidad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tutor_id", nullable = false)
	private Tutor tutor;
}
