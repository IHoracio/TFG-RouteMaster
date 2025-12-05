package es.metrica.sept25.evolutivo.service.weather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.metrica.sept25.evolutivo.entity.weather.Weather;
import es.metrica.sept25.evolutivo.entity.weather.WeatherLink;

@Service
public class WeatherServiceImpl implements WeatherService {

	private static final String API_URL = "https://opendata.aemet.es/opendata/api/prediccion/especifica/municipio/diaria/";

	@Autowired
	private RestTemplate restTemplate;

	public Weather getWeatherLink(String zipCode, String apiKey) {
		String url = UriComponentsBuilder
    			.fromUriString(API_URL)
    			.path(zipCode)
    		    .queryParam("api_key", apiKey)
    		    .toUriString();

		WeatherLink weather = restTemplate.getForObject(url, WeatherLink.class);
		System.err.println(weather.getDatos());
		return getWeather(weather.getDatos());
	}

	public Weather getWeather(String url) {
		String json = restTemplate.getForObject(url, String.class);
		System.err.println(json);
		try {
			Weather weather = new ObjectMapper().readValue(json, Weather.class);
			System.out.println(weather);
			return weather;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
