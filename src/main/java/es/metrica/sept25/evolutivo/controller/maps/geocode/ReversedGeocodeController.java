package es.metrica.sept25.evolutivo.controller.maps.geocode;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.service.maps.geocode.ReverseGeocodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@Tag(name = "Geocode")
@RequestMapping("/geocode")
public class ReversedGeocodeController {

	@Autowired
	private ReverseGeocodeService reverseGeocodeService;

	@Operation(summary = "Obtiene la dirección de unas cordenadas", 
			description = "Devuelve el `formatted_address` correspondiente a la latitud y longitud "
					+ "indicadas usando la API de Google Geocoding.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "401", description = "apiKey no encontrada"),
			@ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
			@ApiResponse(responseCode = "200", description = "Coordenadas encontradas") 
	})
	@SecurityRequirement(name = "googleApiKey")
	@GetMapping("/reverse")
	public ResponseEntity<String> getAddress(
			@RequestParam double lat, 
			@RequestParam double lng,
			HttpServletRequest request) {

		String apiKey = request.getHeader("api_key");
		if (apiKey == null || apiKey.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Optional<String> address = reverseGeocodeService.getAddress(lat, lng, apiKey);
		if (address.get() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(address.get(), HttpStatus.OK);
	}
}
