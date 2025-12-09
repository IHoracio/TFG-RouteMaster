package es.metrica.sept25.evolutivo.service.weather;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.metrica.sept25.evolutivo.domain.dto.weather.Dia;
import es.metrica.sept25.evolutivo.domain.dto.weather.Prediccion;
import es.metrica.sept25.evolutivo.domain.dto.weather.Weather;
import es.metrica.sept25.evolutivo.domain.dto.weather.WeatherLink;

@Service
public class WeatherServiceImpl implements WeatherService {

	private static final String API_URL = "https://opendata.aemet.es/opendata/api/prediccion/especifica/municipio/horaria/";

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ObjectMapper objectMapper;

	public Optional<Weather> getWeather(String zipCode, String apiKey) {
		String url = UriComponentsBuilder
    			.fromUriString(API_URL)
    			.path(zipCode)
    		    .queryParam("api_key", apiKey)
    		    .toUriString();

		WeatherLink weather = restTemplate.getForObject(url, WeatherLink.class);
		return getFirstWeatherDay(getWeatherData(weather.getDatos()));
	}

	private List<Weather> getWeatherData(String url) {
		String json = restTemplate.getForObject(url, String.class);
		try {
			return objectMapper.readValue(json, new TypeReference<List<Weather>>() {
			});
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	private Optional<Weather> getFirstWeatherDay(List<Weather> weatherList) {
		if (weatherList.isEmpty())
			return Optional.empty();

		Weather w = weatherList.getFirst();
		Prediccion p = w.getPrediccion();
		Dia firstDay = p.getDia().getFirst();
		p.setDia(new ArrayList<Dia>(Arrays.asList(firstDay)));
		w.setPrediccion(p);
		return Optional.of(w);
	}
}
