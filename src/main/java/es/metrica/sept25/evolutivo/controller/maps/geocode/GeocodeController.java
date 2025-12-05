package es.metrica.sept25.evolutivo.controller.maps.geocode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.entity.maps.routes.Coords;
import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class GeocodeController {

	@Autowired
	private GeocodeService geocodeService;

	@Operation(summary = "Obtiene las coordenadas de una dirección", description = "Devuelve las coordenadas (latitud y longitud) del lugar indicado usando la API de Google Geocoding.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "401", description = "apiKey no encontrada"),
			@ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
			@ApiResponse(responseCode = "200", description = "Coordenadas encontradas") 
	})
	@SecurityRequirement(name = "googleApiKey")
	@GetMapping("geocode")
	public ResponseEntity<Coords> getCoordinates(@RequestParam String address, HttpServletRequest request) {

		String apiKey = request.getHeader("key");

		if (apiKey == null || apiKey.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Coords coords = geocodeService.getCoordinates(address, apiKey);
		if (coords == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(coords, HttpStatus.OK);
	}
}
