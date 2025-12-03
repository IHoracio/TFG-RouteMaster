package es.metrica.sept25.evolutivo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import es.metrica.sept25.evolutivo.entity.Weather;

@Service
public class WeatherServiceImpl {

	private static final String API_URL = "https://opendata.aemet.es/opendata/api/prediccion/especifica/municipio/diaria/";
	@Autowired
	private RestTemplate restTemplate;

	public Weather getWeatherLink(String zipCode, String apiKey) {
		return restTemplate.getForObject(API_URL + zipCode + "?api_key=" + apiKey, Weather.class);
	}

}
