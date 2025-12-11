package es.metrica.sept25.evolutivo.domain.dto.maps.routes;

public class StepWithWeather {

	private double lat;
	private double lng;
	private String weatherDescription;
	private Double temperature;

	public StepWithWeather(double lat, double lng, String weatherDescription, Double temperature) {
		this.lat = lat;
		this.lng = lng;
		this.weatherDescription = weatherDescription;
		this.temperature = temperature;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getWeatherDescription() {
		return weatherDescription;
	}

	public void setWeatherDescription(String weatherDescription) {
		this.weatherDescription = weatherDescription;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}
}
