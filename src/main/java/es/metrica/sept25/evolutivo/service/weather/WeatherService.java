package es.metrica.sept25.evolutivo.service.weather;

import es.metrica.sept25.evolutivo.entity.weather.WeatherLink;

public interface WeatherService {
	WeatherLink getWeatherLink(String zipCode, String apiKey);
}
