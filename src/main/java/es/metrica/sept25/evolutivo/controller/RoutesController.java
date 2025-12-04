package es.metrica.sept25.evolutivo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import es.metrica.sept25.evolutivo.entity.maps.routes.RouteGroup;
import es.metrica.sept25.evolutivo.service.RoutesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
public class RoutesController {

	private final RoutesService routesService;

	@Autowired
	public RoutesController(RoutesService routesService) {
		this.routesService = routesService;
	}

	@Operation(
			summary = "Obtiene de un punto A → B la ruta", 
			description = "Devuelve la información esencial de la ruta en coche: "
					+ "punto de origen y destino, distancia total, tiempo estimado "
					+ "y los pasos principales del recorrido, incluyendo las coordenadas de cada tramo.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "401", description = "apiKey wasn't found"),
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Route found") })
	@SecurityRequirement(name = "googleApiKey")
	@GetMapping("/routes")
	public ResponseEntity<RouteGroup> getDirections(
			@RequestParam(required = false, defaultValue = "Madrid Calle Alcalá y Gran Vía, Madrid") String origin,
			@RequestParam(required = false, defaultValue = "Segovia") String destination,
			@RequestParam(required = false, defaultValue = "es") String language, HttpServletRequest request) {

		String apiKey = request.getHeader("api_key");
		if (apiKey == null || apiKey.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		RouteGroup response = routesService.getDirections(origin, destination, language, apiKey);
		if (response == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}