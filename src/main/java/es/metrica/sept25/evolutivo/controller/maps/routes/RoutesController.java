package es.metrica.sept25.evolutivo.controller.maps.routes;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.entity.maps.routes.RouteGroup;
import es.metrica.sept25.evolutivo.service.maps.routes.RoutesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@Tag(name = "Direcciones")
public class RoutesController {

	@Autowired
	private RoutesService routesService;

	@Operation(
			summary = "Obtiene de un punto A → B la ruta", 
			description = "Devuelve la información esencial de la ruta en coche: "
					    + "punto de origen y destino o destinos, distancia total, tiempo estimado "
					    + "y los pasos principales del recorrido, incluyendo las coordenadas de cada tramo.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "401", description = "apiKey wasn't found"),
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Route found") 
			})
	@SecurityRequirement(name = "googleApiKey")
	@GetMapping("/routes")
	public ResponseEntity<RouteGroup> getDirections(
			@RequestParam(required = true, defaultValue = "El Vellon") String origin,
			@RequestParam(required = true, defaultValue = "El Molar") String destination,
			@RequestParam(required = false, defaultValue = "") List<String> waypoints,
			@RequestParam(required = false, defaultValue = "") Boolean optimize,
			@RequestParam(required = false, defaultValue = "es") String language, HttpServletRequest request) {

		String apiKey = request.getHeader("key");
		if (apiKey == null || apiKey.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		RouteGroup response = routesService.getDirections(origin, destination, waypoints, optimize, language, apiKey);
		
		if (response == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}