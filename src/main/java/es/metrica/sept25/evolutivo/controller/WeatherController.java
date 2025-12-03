package es.metrica.sept25.evolutivo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.entity.Weather;
import es.metrica.sept25.evolutivo.service.WeatherServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class WeatherController {

	private WeatherServiceImpl weatherServiceImpl;

	public WeatherController(WeatherServiceImpl weatherServiceImpl) {
		this.weatherServiceImpl = weatherServiceImpl;
	}

	@SecurityRequirement(name = "aemetApiKey")
	@GetMapping("/checkWeather/{zipCode}")
	public ResponseEntity<Weather> getWeather(@PathVariable String zipCode,
			HttpServletRequest request) {
		String apiKey = request.getHeader("api_key");
		if (apiKey.isEmpty())
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		if (zipCode.isEmpty())
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		Weather weather = weatherServiceImpl.getWeatherLink(zipCode, apiKey);
		if (weather == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<Weather>(weather, HttpStatus.OK);
		
	}
}
