package com.orangehearth.OrangeHearth.model.entity;

import java.time.LocalDateTime;

import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;
import com.orangehearth.OrangeHearth.model.enums.Rol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaUsuario extends EntidadAuditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "full_name", nullable = false)
	private String fullName;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password_hash")
	private String password;

	@Column(name = "security_question", length = 255, nullable = true)
	private String securityQuestion;

	@Column(name = "security_answer_hash", length = 255, nullable = true)
	private String securityAnswerHash;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Rol role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EstadoCuenta status;

	@Column(name = "password_change_required")
	private boolean passwordChangeRequired;

	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;

	@Column(name = "password_updated_at")
	private LocalDateTime passwordUpdatedAt;

	// Métodos auxiliares en español para mantener compatibilidad con los renombramientos del proyecto.
	public Rol getRol() {
		return role;
	}

	public void setRol(Rol rol) {
		this.role = rol;
	}

	public EstadoCuenta getEstado() {
		return status;
	}

	public void setEstado(EstadoCuenta estado) {
		this.status = estado;
	}

	public String getNombreCompleto() {
		return fullName;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.fullName = nombreCompleto;
	}

	public String getCorreo() {
		return email;
	}

	public void setCorreo(String correo) {
		this.email = correo;
	}
}
