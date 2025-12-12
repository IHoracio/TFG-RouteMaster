package es.metrica.sept25.evolutivo.controller.maps.routes;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.RouteGroup;
import es.metrica.sept25.evolutivo.service.maps.routes.RoutesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Direcciones")
public class RoutesController {

	@Autowired
	private RoutesService routesService;

	@Operation(
			summary = "Calcular rutas", 
			description = "Devuelve la información esencial de la ruta en coche: "
					    + "punto de origen y destino o destinos, distancia total, tiempo estimado "
					    + "y los pasos principales del recorrido, incluyendo las coordenadas de cada tramo."
					    + "Se pueden activar dos optimizaciones para la ruta:"
					    + "1- Te optimiza los puntos intermedios, recolocandolos para tener la ruta mas optima hasta el destino predefinido"
					    + "2- Te optimiza los puntos intermedios y el destino incluido, por lo que el destino final puede cambiar")
	@ApiResponses(value = { 
//			@ApiResponse(responseCode = "401", description = "apiKey wasn't found"),
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Route found") 
			})
//	@SecurityRequirement(name = "googleApiKey")
	@GetMapping("/routes")
	public ResponseEntity<RouteGroup> getDirections(
			@RequestParam(required = true, defaultValue = "El Vellon") String origin,
			@RequestParam(required = true, defaultValue = "El Molar") String destination,
			@RequestParam(required = false, defaultValue = "") List<String> waypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeWaypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeRoute,
			@RequestParam(required = false, defaultValue = "es") String language
//			, HttpServletRequest request
			) {

//		String apiKey = request.getHeader("key");
//		if (apiKey == null || apiKey.isEmpty()) {
//			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//		}
		Optional<RouteGroup> response = routesService.getDirections(origin, destination, waypoints, optimizeWaypoints, optimizeRoute, language);
		
		if (response.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(response.get(), HttpStatus.OK);
	}

	@Operation(
			summary = "Lista de coordenadas de los pasos de una ruta", 
			description = "")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Coordinates for route"),
			@ApiResponse(responseCode = "400", description = "Bad request")
			})
	@GetMapping("/routes/stepCoords")
	public ResponseEntity<List<Coords>> getCoordsForRoute(
			@RequestParam(required = true, defaultValue = "El Vellon") String origin,
			@RequestParam(required = true, defaultValue = "El Molar") String destination,
			@RequestParam(required = false, defaultValue = "") List<String> waypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeWaypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeRoute,
			@RequestParam(required = false, defaultValue = "es") String language
			) {

		Optional<RouteGroup> response = routesService.getDirections(origin, destination, waypoints, optimizeWaypoints,
				optimizeRoute, language);
		
		if (response.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		List<Coords> coordsList = routesService.extractRoutePoints(response.get());
		
		return new ResponseEntity<>(coordsList, HttpStatus.OK);
	}

	@Operation(
			summary = "Lista de coordenadas de los \"legs\" de una ruta", 
			description = "")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Coordinates for route"),
			@ApiResponse(responseCode = "400", description = "Bad request")
			})
	@GetMapping("/routes/legCoords")
	public ResponseEntity<List<Coords>> getLegCoordsForRoute(
			@RequestParam(required = true, defaultValue = "El Vellon") String origin,
			@RequestParam(required = true, defaultValue = "El Molar") String destination,
			@RequestParam(required = false, defaultValue = "") List<String> waypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeWaypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeRoute,
			@RequestParam(required = false, defaultValue = "es") String language
			) {

		Optional<RouteGroup> response = routesService.getDirections(origin, destination, waypoints, optimizeWaypoints, optimizeRoute, language);
		
		if (response.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		List<Coords> coordsList = routesService.getLegCoords(response.get());
		
		return new ResponseEntity<>(coordsList, HttpStatus.OK);
	}
}