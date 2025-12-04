package es.metrica.sept25.evolutivo.entity.weather;

import java.util.List;

public class Dia {

	private List<EstadoCielo> estadoCielo;
	private List<Temperatura> temperatura;
	private List<HumedadRelativa> humedadRelativa;
	private List<String> fecha;

	public List<EstadoCielo> getEstadoCielo() {
		return estadoCielo;
	}

	public void setEstadoCielo(List<EstadoCielo> estadoCielo) {
		this.estadoCielo = estadoCielo;
	}

	public List<Temperatura> getTemperatura() {
		return temperatura;
	}

	public void setTemperatura(List<Temperatura> temperatura) {
		this.temperatura = temperatura;
	}

	public List<HumedadRelativa> getHumedadRelativa() {
		return humedadRelativa;
	}

	public void setHumedadRelativa(List<HumedadRelativa> humedadRelativa) {
		this.humedadRelativa = humedadRelativa;
	}

	public List<String> getFecha() {
		return fecha;
	}

	public void setFecha(List<String> fecha) {
		this.fecha = fecha;
	}

}
