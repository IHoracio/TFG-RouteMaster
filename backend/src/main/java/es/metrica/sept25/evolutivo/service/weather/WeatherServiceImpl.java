package es.metrica.sept25.evolutivo.service.weather;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.domain.dto.weather.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WeatherServiceImpl implements WeatherService {

	private static final Logger log = LoggerFactory.getLogger(WeatherServiceImpl.class);

	private static final String API_URL = "https://api.openweathermap.org/data/3.0/onecall";
	private static final String EXCLUDE_PARAMS = "current,minutely,daily";

	@Autowired
	private RestTemplate restTemplate;

	@Value("${evolutivo.api_key_openweather:}")
	private String API_KEY_OPENWEATHER;

	@Cacheable(value = "weather", cacheManager = "climateCacheManager")
	public Optional<Weather> getWeather(double lat, double lng, String lang, String address) {
		log.info("[weather-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to get the weather object for coordinates: [lat=" 
				+ lat + ", lng=" + lng + ", lang=" + lang + ", address=" + address + "].");
		
		if (API_KEY_OPENWEATHER == null || API_KEY_OPENWEATHER.isEmpty()) {
			log.warn("[weather-service] [" + LocalDateTime.now().toString() + "] "
					+ "OpenWeatherMap API key is not configured. Please set evolutivo.api_key_openweather property.");
			return Optional.empty();
		}
		
		try {
			String url = UriComponentsBuilder
					.fromUriString(API_URL)
					.queryParam("lat", lat)
					.queryParam("lon", lng)
					.queryParam("units", "metric")
					.queryParam("exclude", EXCLUDE_PARAMS)
					.queryParam("lang", lang)
					.queryParam("appid", API_KEY_OPENWEATHER)
					.toUriString();

			Weather weather = restTemplate.getForObject(url, Weather.class);
			
			if (weather != null) {
				// Set the address in the weather object
				weather.setDireccion(address);
				
				log.info("[weather-service] [" + LocalDateTime.now().toString() + "] "
						+ "Successfully retrieved weather for coordinates: [lat=" 
						+ lat + ", lng=" + lng + "].");
				return Optional.of(weather);
			}
			
			log.warn("[weather-service] [" + LocalDateTime.now().toString() + "] "
					+ "No weather data returned for coordinates: [lat=" 
					+ lat + ", lng=" + lng + "].");
			return Optional.empty();
			
		} catch (Exception e) {
			log.error("[weather-service] [" + LocalDateTime.now().toString() + "] "
					+ "Failed to retrieve weather for coordinates: [lat=" 
					+ lat + ", lng=" + lng + "]. Error: " + e.getMessage());
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
