package tfg.domain.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Prediccion {
	
	@JsonProperty("temp")
	private Double temp;
	
	@JsonProperty("feels_like")
	private Double feelsLike;
	
	@JsonProperty("temp_min")
	private Double tempMin;
	
	@JsonProperty("temp_max")
	private Double tempMax;
	
	@JsonProperty("humidity")
	private Integer humidity;

	public Double getTemp() {
		return temp;
	}

	public void setTemp(Double temp) {
		this.temp = temp;
	}

	public Double getFeelsLike() {
		return feelsLike;
	}

	public void setFeelsLike(Double feelsLike) {
		this.feelsLike = feelsLike;
	}

	public Double getTempMin() {
		return tempMin;
	}

	public void setTempMin(Double tempMin) {
		this.tempMin = tempMin;
	}

	public Double getTempMax() {
		return tempMax;
	}

	public void setTempMax(Double tempMax) {
		this.tempMax = tempMax;
	}

	public Integer getHumidity() {
		return humidity;
	}

	public void setHumidity(Integer humidity) {
		this.humidity = humidity;
	}

	// Helper method for backward compatibility
	public java.util.List<Dia> getDia() {
		// Create a single Dia object with current weather data
		Dia dia = new Dia();
		dia.setFecha(java.time.LocalDate.now().toString());
		
		// Create temperature entry
		Temperatura temperatura = new Temperatura();
		temperatura.setValue(this.temp);
		temperatura.setPeriodo(0);
		dia.setTemperatura(java.util.Collections.singletonList(temperatura));
		
		return java.util.Collections.singletonList(dia);
	}

	@Override
	public String toString() {
		return "Prediccion [temp=" + temp + ", feelsLike=" + feelsLike + ", tempMin=" + tempMin + ", tempMax=" + tempMax + ", humidity=" + humidity + "]";
	}
}
