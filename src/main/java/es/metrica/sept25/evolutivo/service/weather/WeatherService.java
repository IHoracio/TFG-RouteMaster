package es.metrica.sept25.evolutivo.service.weather;

import es.metrica.sept25.evolutivo.domain.dto.weather.Weather;

public interface WeatherService {
	public Weather getWeather(String zipCode, String apiKey);
}
