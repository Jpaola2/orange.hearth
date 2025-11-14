package com.orangehearth.OrangeHearth.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.orangehearth.OrangeHearth.model.value.Direccion;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tutores")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tutor extends EntidadAuditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "user_account_id", nullable = false)
	private CuentaUsuario userAccount;

	@Column(name = "document_type", nullable = false)
	private String documentType;

	@Column(name = "document_number", nullable = false, unique = true)
	private String documentNumber;

	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;

	@Embedded
	private Direccion address;

	@OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Mascota> mascotas = new ArrayList<>();

	public CuentaUsuario getCuentaUsuario() {
		return userAccount;
	}

	public void setCuentaUsuario(CuentaUsuario cuentaUsuario) {
		this.userAccount = cuentaUsuario;
	}

	public Direccion getDireccion() {
		return address;
	}

	public void setDireccion(Direccion direccion) {
		this.address = direccion;
	}
}
