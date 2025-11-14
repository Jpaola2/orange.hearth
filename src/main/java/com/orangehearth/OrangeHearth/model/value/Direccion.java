package com.orangehearth.OrangeHearth.model.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Direccion {

	@Column(name = "via")
	private String via;

	@Column(name = "numero_principal")
	private String numeroPrincipal;

	@Column(name = "letra_uno")
	private String letraUno;

	@Column(name = "bis_letra")
	private String bis;

	@Column(name = "letra_dos")
	private String letraDos;

	@Column(name = "cardinalidad")
	private String cardinal;

	@Column(name = "numero_secundario")
	private String numeroSecundario;

	@Column(name = "numero_placa")
	private String numeroPlaca;

	@Column(name = "detalle_adicional")
	private String adicional;

	@Column(name = "direccion_completa")
	private String completo;
}
