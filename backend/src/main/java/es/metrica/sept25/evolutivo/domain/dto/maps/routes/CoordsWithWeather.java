package es.metrica.sept25.evolutivo.domain.dto.maps.routes;

import java.util.List;
import java.util.Map;
import es.metrica.sept25.evolutivo.domain.dto.weather.Alerta;

public class CoordsWithWeather {

	private String address;
	private Map<Integer, String> weatherDescription;
	private Map<Integer, Double> temperatures;
	private Map<Integer, Double> feelsLike;
	private Map<Integer, Double> windSpeed;
	private Map<Integer, Integer> visibility;
	private List<Alerta> alerts;

	public CoordsWithWeather(String address, Map<Integer, String> weatherDescription,
			Map<Integer, Double> temperatures) {
		this.address = address;
		this.weatherDescription = weatherDescription;
		this.temperatures = temperatures;
		this.feelsLike = Map.of();
		this.windSpeed = Map.of();
		this.visibility = Map.of();
		this.alerts = List.of();
	}

	public CoordsWithWeather(String address, Map<Integer, String> weatherDescription,
			Map<Integer, Double> temperatures, Map<Integer, Double> feelsLike,
			Map<Integer, Double> windSpeed, Map<Integer, Integer> visibility,
			List<Alerta> alerts) {
		this.address = address;
		this.weatherDescription = weatherDescription;
		this.temperatures = temperatures;
		this.feelsLike = feelsLike;
		this.windSpeed = windSpeed;
		this.visibility = visibility;
		this.alerts = alerts;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public Map<Integer, Double> getFeelsLike() {
		return feelsLike;
	}

	public void setFeelsLike(Map<Integer, Double> feelsLike) {
		this.feelsLike = feelsLike;
	}

	public Map<Integer, Double> getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(Map<Integer, Double> windSpeed) {
		this.windSpeed = windSpeed;
	}

	public Map<Integer, Integer> getVisibility() {
		return visibility;
	}

	public void setVisibility(Map<Integer, Integer> visibility) {
		this.visibility = visibility;
	}

	public List<Alerta> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Alerta> alerts) {
		this.alerts = alerts;
	}

}
