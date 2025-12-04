package es.metrica.sept25.evolutivo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.entity.weather.WeatherLink;
import es.metrica.sept25.evolutivo.service.weather.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@Tag(name = "Clima")
public class WeatherController {

	@Autowired
	private WeatherService weatherService;

	@Operation(summary = "Devuelve el clima para un código postal concreto", description = "Compone un objeto Weather que contiene toda la "
			+ "información meteorológica para un código postal concreto.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Route found"),
			@ApiResponse(responseCode = "401", description = "apiKey wasn't found"),
			@ApiResponse(responseCode = "400", description = "Bad request") })
	@SecurityRequirement(name = "aemetApiKey")
	@GetMapping("/checkWeather/{zipCode}")
	public ResponseEntity<WeatherLink> getWeather(@PathVariable String zipCode, HttpServletRequest request) {
		String apiKey = request.getHeader("api_key");

		if (apiKey == null || apiKey.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		if (zipCode.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		WeatherLink weather = weatherService.getWeatherLink(zipCode, apiKey);
		if (weather == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<WeatherLink>(weather, HttpStatus.OK);

	}
}
