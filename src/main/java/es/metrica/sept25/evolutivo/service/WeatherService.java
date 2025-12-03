package es.metrica.sept25.evolutivo.service;

import es.metrica.sept25.evolutivo.entity.Weather;

public interface WeatherService {
	Weather getWeatherLink(String zipCode, String apiKey);
}
