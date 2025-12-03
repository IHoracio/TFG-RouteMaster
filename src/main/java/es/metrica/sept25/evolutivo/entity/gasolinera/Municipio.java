package es.metrica.sept25.evolutivo.entity.gasolinera;

import es.metrica.sept25.evolutivo.service.ProvinciaService;
import es.metrica.sept25.evolutivo.service.ProvinciaServiceImpl;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Municipio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "req_id")
	Long idMunicipio;

	@Column(name = "nombre_municipio")
	String nombreMunicipio;

	@ManyToOne
	@JoinColumn(name = "id_provincia")
	private Provincia provincia;

	public Municipio() {
	};

	public Municipio(Long idMunicipio, String nombreMunicipio, Long idProvincia) {
		super();
		this.idMunicipio = idMunicipio;
		this.nombreMunicipio = nombreMunicipio;
		ProvinciaService p = new ProvinciaServiceImpl();
		this.provincia = p.getProvinciaById(idProvincia).orElseGet(null);
	}

	public Long getIdMunicipio() {
		return idMunicipio;
	}

	public void setIdMunicipio(Long idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public String getNombreMunicipio() {
		return nombreMunicipio;
	}

	public void setNombreMunicipio(String nombreMunicipio) {
		this.nombreMunicipio = nombreMunicipio;
	}

	public Provincia getProvincia() {
		return this.provincia;
	}

	public void setIdProvincia(Provincia provincia) {
		this.provincia = provincia;
	}

	@Override
	public String toString() {
		return "Municipio[id=" + this.idMunicipio + ", idProv=" + this.provincia.getIdProvincia() + ", nombre="
				+ this.nombreMunicipio + "]";
	}
}
