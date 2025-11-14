package com.orangehearth.OrangeHearth.model.entity;

import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "veterinarios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Veterinarian extends EntidadAuditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_account_id", nullable = false)
    private CuentaUsuario userAccount;

	@Column(name = "professional_license", nullable = false, unique = true)
	private String professionalLicense;

	@Column(name = "speciality")
	private String speciality;

	@Column(name = "years_experience")
	private Integer yearsExperience;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EstadoCuenta status;

	public CuentaUsuario getCuentaUsuario() {
		return userAccount;
	}

	public void setCuentaUsuario(CuentaUsuario cuentaUsuario) {
		this.userAccount = cuentaUsuario;
	}
}
