package tfg.service.weather;

import java.util.Optional;

import tfg.domain.dto.weather.Weather;

public interface WeatherService {
	public Optional<Weather> getWeather(double lat, double lng, String lang, String address);
}
