package es.metrica.sept25.evolutivo.entity.gasolinera;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Provincia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "req_id")
	Long idProvincia;

	@Column(name = "nombre_provincia")
	String nombreProvincia;
	
	@OneToMany(mappedBy = "provincia")
	private List<Municipio> municipios;

	public Provincia() {
	};

	public Provincia(Long idProvincia, String nombreProvincia) {
		super();
		this.idProvincia = idProvincia;
		this.nombreProvincia = nombreProvincia;
	}

	public Long getIdProvincia() {
		return idProvincia;
	}

	public void setIdProvincia(Long idProvincia) {
		this.idProvincia = idProvincia;
	}

	public String getNombreProvincia() {
		return nombreProvincia;
	}

	public void setNombreProvincia(String nombreProvincia) {
		this.nombreProvincia = nombreProvincia;
	}
	
	public List<Municipio> getMunicipios() {
		return List.copyOf(this.municipios);
	}

	public void setMunicipios(List<Municipio> municipios) {
		this.municipios = municipios;
	}

	@Override
	public String toString() {
		return "Provincia[id=" + this.idProvincia + ", nombre=" + this.nombreProvincia + "]";
	}
}
