package es.metrica.sept25.evolutivo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.entity.weather.Weather;

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
		return restTemplate.getForObject(url, Weather.class);
	}

}
