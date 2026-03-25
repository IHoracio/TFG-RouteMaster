package tfg.controller.maps.routes;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import tfg.domain.dto.maps.routes.Coords;
import tfg.domain.dto.maps.routes.FullRouteData;
import tfg.domain.dto.maps.routes.RouteGroup;
import tfg.domain.dto.maps.routes.RouteRequestDto;
import tfg.service.maps.routes.RoutesService;

@RestController
@Tag(
		name = "Direcciones",
		description = "Conjunto de endpoints que se aprovechan de la API de Routes de "
				+ "Google Maps para calcular rutas, puntos de ruta y otros datos "
				+ "relacionados con la creación de rutas, el clima y las gasolineras "
				+ "en un trayecto."
		)
@RequestMapping("/api")
public class RoutesController {

	@Autowired
	private RoutesService routesService;

	
	@Operation(
		    summary = "Obtiene todos los datos de la ruta en una sola llamada",
		    description = "Devuelve polilíneas, legs, gasolineras y clima para la ruta dada, llamando a Google solo una vez.")
		@ApiResponses(value = {
		    @ApiResponse(responseCode = "200", description = "Datos de ruta obtenidos exitosamente."),
		    @ApiResponse(responseCode = "400", description = "Solicitud errónea.")
		})
	@PostMapping("/route/fullData")
	public ResponseEntity<FullRouteData> getFullRouteData(@RequestBody RouteRequestDto request) {

	    Optional<FullRouteData> response = routesService.getFullRouteData(
	            request.origin(), 
	            request.destination(), 
	            request.waypoints(), 
	            request.optimizeWaypoints(), 
	            request.optimizeRoute(), 
	            request.language(), 
	            request.avoidTolls(), 
	            request.gasRadius()
	    );

	    return response
	            .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
	            .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
	}

}