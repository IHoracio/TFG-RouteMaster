package es.metrica.sept25.evolutivo.controller.maps.geocode;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/geocode")
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
	@GetMapping
	public ResponseEntity<Coords> getCoordinates(
			@RequestParam String address, 
			HttpServletRequest request) {

		String apiKey = request.getHeader("api_key");

		if (apiKey == null || apiKey.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Optional<Coords> coords = geocodeService.getCoordinates(address, apiKey);
		if (coords.get() == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(coords.get(), HttpStatus.OK);
	}
	
	@Operation(summary = "Obtiene el municipio de una dirección", description = "Devuelve el municpio del lugar indicado usando la API de Google Geocoding.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "401", description = "apiKey no encontrada"),
			@ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
			@ApiResponse(responseCode = "200", description = "Coordenadas encontradas") 
	})
	@SecurityRequirement(name = "googleApiKey")
	@GetMapping("/municipio")
	public ResponseEntity<String> getMunicipio(
	        @RequestParam double lat,
	        @RequestParam double lng,
	        HttpServletRequest request) {

	    String apiKey = request.getHeader("api_key");

	    if (apiKey == null || apiKey.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	    }

	    Optional<String> municipio = geocodeService.getMunicipio(lat, lng, apiKey);

	    if (municipio == null) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }

	    return new ResponseEntity<>(municipio.get(), HttpStatus.OK);
	}
}
