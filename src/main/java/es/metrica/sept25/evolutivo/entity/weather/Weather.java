package es.metrica.sept25.evolutivo.entity.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Weather {

	private Origen origen;
	@JsonProperty("nombre")
	private String nombre;
	@JsonProperty("provincia")
	private String provincia;
//	private Prediccion prediccion;


	public String getNombre() {
		return nombre;
	}

	public Origen getOrigen() {
		return origen;
	}

	public void setOrigen(Origen origen) {
		this.origen = origen;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	@Override
	public String toString() {
		return "Weather [nombre=" + nombre + ", provincia=" + provincia + "]";
	}

//
//	public Prediccion getPrediccion() {
//		return prediccion;
//	}
//
//	public void setPrediccion(Prediccion prediccion) {
//		this.prediccion = prediccion;
//	}

}
