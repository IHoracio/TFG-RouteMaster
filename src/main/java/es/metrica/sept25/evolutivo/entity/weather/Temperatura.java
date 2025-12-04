package es.metrica.sept25.evolutivo.entity.weather;

public class Temperatura {

	private int maxima;
	private int minima;
	private Dato dato;

	public int getMaxima() {
		return maxima;
	}

	public void setMaxima(int maxima) {
		this.maxima = maxima;
	}

	public int getMinima() {
		return minima;
	}

	public void setMinima(int minima) {
		this.minima = minima;
	}

	public Dato getDato() {
		return dato;
	}

	public void setDato(Dato dato) {
		this.dato = dato;
	}

}
