package es.metrica.sept25.evolutivo.service.weather;

import java.util.List;

import es.metrica.sept25.evolutivo.domain.dto.weather.Weather;

public interface WeatherService {
	List<Weather> getWeatherLink(String zipCode, String apiKey);
}
