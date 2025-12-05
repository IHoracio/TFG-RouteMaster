package es.metrica.sept25.evolutivo.service.weather;

import es.metrica.sept25.evolutivo.entity.weather.Weather;

public interface WeatherService {
	Weather getWeatherLink(String zipCode, String apiKey);
}
