package es.metrica.sept25.evolutivo.domain.dto.weather;

public class Temperatura {
	private Double value;
	private Integer periodo;

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}


}