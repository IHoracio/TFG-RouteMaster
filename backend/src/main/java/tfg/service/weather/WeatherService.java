package tfg.service.weather;

import java.util.List;
import java.util.Optional;

import tfg.domain.dto.maps.routes.Coords;
import tfg.domain.dto.maps.routes.CoordsWithWeather;
import tfg.domain.dto.weather.Weather;

public interface WeatherService {
	public Optional<Weather> getWeather(double lat, double lng, String lang, String address);
	
	List<CoordsWithWeather> getWeatherForLegs(List<Coords> legCoords, String lang);
}
