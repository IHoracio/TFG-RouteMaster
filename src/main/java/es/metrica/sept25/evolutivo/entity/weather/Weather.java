package es.metrica.sept25.evolutivo.entity.weather;

public class Weather {

	private String nombre;
	private String provincia;
	private Prediccion prediccion;

	public String getNombre() {
		return nombre;
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

	public Prediccion getPrediccion() {
		return prediccion;
	}

	public void setPrediccion(Prediccion prediccion) {
		this.prediccion = prediccion;
	}

}
