package tfg.service.weather;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import tfg.domain.dto.maps.routes.Coords;
import tfg.domain.dto.maps.routes.CoordsWithWeather;
import tfg.domain.dto.weather.EstadoCielo;
import tfg.domain.dto.weather.HourlyWeather;
import tfg.domain.dto.weather.Weather;
import tfg.service.maps.geocode.GeocodeService;
import tfg.service.maps.geocode.ReverseGeocodeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WeatherServiceImpl implements WeatherService {

	private static final Logger log = LoggerFactory.getLogger(WeatherServiceImpl.class);

	private static final String API_URL = "https://api.openweathermap.org/data/3.0/onecall";
	private static final String EXCLUDE_PARAMS = "current,minutely,daily";

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
    private ReverseGeocodeService reverseGeocodeService;

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
	
	@Override
    public List<CoordsWithWeather> getWeatherForLegs(List<Coords> legCoords, String lang) {
        log.info("[weather-service] [" + LocalDateTime.now() + "] Obteniendo clima para " + legCoords.size() + " puntos de ruta (legs).");
        
        if (legCoords == null || legCoords.isEmpty()) {
            return List.of();
        }

        return legCoords.stream().map(coords -> {
            // 1. Usar ReverseGeocodeService para obtener la dirección real
            String address = reverseGeocodeService
                    .getAddress(coords.getLat(), coords.getLng())
                    .orElse("Unknown address");
            
            // 2. Llamar a OpenWeather para obtener el clima
            Optional<Weather> weatherOpt = getWeather(coords.getLat(), coords.getLng(), lang, address);
            
            // Si falla o no hay clima, devolvemos el objeto vacío (igual que en tu lógica original)
            if (weatherOpt.isEmpty()) {
                log.warn("[weather-service] [" + LocalDateTime.now().toString() + "] "
                        + "No weather data could be retrieved for coords: "
                        + coords.toString());
                
                return new CoordsWithWeather(
                        address,
                        new HashMap<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        List.of()
                );
            }

            Weather weather = weatherOpt.get();

            // 3. Inicializar los mapas para guardar los datos por hora
            Map<Integer, String> mapaAlertas = new HashMap<>();
            Map<Integer, Double> mapaTemperaturas = new HashMap<>();
            Map<Integer, Double> mapaFeelsLike = new HashMap<>();
            Map<Integer, Double> mapaWindSpeed = new HashMap<>();
            Map<Integer, Integer> mapaVisibility = new HashMap<>();
            
            // 4. Poblar los mapas con el array 'hourly' de OpenWeather
            if (weather.getHourly() != null && !weather.getHourly().isEmpty()) {
                for (int i = 0; i < weather.getHourly().size(); i++) {
                    HourlyWeather hourly = weather.getHourly().get(i);
                    
                    if (hourly.getWeather() != null && !hourly.getWeather().isEmpty()) {
                        EstadoCielo estado = hourly.getWeather().get(0);
                        mapaAlertas.put(i, estado.getDescription());
                    }
                    
                    if (hourly.getTemp() != null) {
                        mapaTemperaturas.put(i, hourly.getTemp());
                    }
                    
                    if (hourly.getFeelsLike() != null) {
                        mapaFeelsLike.put(i, hourly.getFeelsLike());
                    }
                    
                    if (hourly.getWindSpeed() != null) {
                        mapaWindSpeed.put(i, hourly.getWindSpeed());
                    }
                    
                    if (hourly.getVisibility() != null) {
                        mapaVisibility.put(i, hourly.getVisibility());
                    }
                }
            }

            // 5. Retornar el DTO completamente montado
            return new CoordsWithWeather(
                    address,
                    mapaAlertas,
                    mapaTemperaturas,
                    mapaFeelsLike,
                    mapaWindSpeed,
                    mapaVisibility,
                    weather.getAlerts() != null ? weather.getAlerts() : List.of()
            );
            
        }).toList();
    }

}
