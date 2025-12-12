package es.metrica.sept25.evolutivo.domain.dto.maps.routes;

import java.util.Map;

public class CoordsWithWeather {

	private double lat;
	private double lng;
	 private Map<Integer, String> weatherDescription;
	private Map<Integer, Double> temperatures;

	public CoordsWithWeather(double lat, double lng, Map<Integer, String> weatherDescription, Map<Integer, Double> temperatures) {
		this.lat = lat;
		this.lng = lng;
		this.weatherDescription = weatherDescription;
		this.temperatures = temperatures;
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

	public Map<Integer, String> getWeatherDescription() {
		return weatherDescription;
	}

	public void setWeatherDescription(Map<Integer, String> weatherDescription) {
		this.weatherDescription = weatherDescription;
	}

	public Map<Integer, Double> getTemperatures() {
		return temperatures;
	}

	public void setTemperatures(Map<Integer, Double> temperatures) {
		this.temperatures = temperatures;
	}

	
}
