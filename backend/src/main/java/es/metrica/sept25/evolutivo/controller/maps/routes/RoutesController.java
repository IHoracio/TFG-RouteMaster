package es.metrica.sept25.evolutivo.controller.maps.routes;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.FullRouteData;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.RouteGroup;
import es.metrica.sept25.evolutivo.service.maps.routes.RoutesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
			summary = "Calcular rutas",
			description = """
					Devuelve la informaci\u00f3n esencial de la ruta en coche: punto de origen y destino o destinos, distancia total, tiempo estimado y los pasos principales del recorrido, incluyendo las coordenadas de cada tramo.Se pueden activar dos optimizaciones para la ruta: \n
					1. Te optimiza los puntos intermedios, recolocandolos para tener la ruta mas \n
					2. Te optimiza los puntos intermedios y el destino incluido, por lo que el destino final puede cambiar.""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Ruta encontrada."),
			@ApiResponse(responseCode = "404", description = "Solicitud errónea: no se pudo calcular la ruta.")
	})
	@GetMapping("/routes")
	public ResponseEntity<RouteGroup> getDirections(
			@RequestParam(required = true, defaultValue = "El Vellon") String origin,
			@RequestParam(required = true, defaultValue = "El Molar") String destination,
			@RequestParam(required = false, defaultValue = "") List<String> waypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeWaypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeRoute,
			@RequestParam(required = false, defaultValue = "es") String language,
			@RequestParam(required = false, defaultValue = "false") boolean avoidTolls
			) {

		Optional<RouteGroup> response = routesService.getDirections(origin, destination, waypoints, optimizeWaypoints, optimizeRoute, language, avoidTolls);

		if (response.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(response.get(), HttpStatus.OK);
	}

	@Operation(
			summary = "Lista de coordenadas de los pasos de una ruta",
			description = "Devuelve una lista de coordenadas para cada uno de los pasos de la ruta dada.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Pasos encontrados para la ruta dada."),
			@ApiResponse(responseCode = "404", description = "Solicitud errónea: no se pudieron calcular los pasos de la ruta.")
	})
	@GetMapping("/route/stepCoords")
	public ResponseEntity<List<Coords>> getCoordsForRoute(
			@RequestParam(required = true, defaultValue = "El Vellon") String origin,
			@RequestParam(required = true, defaultValue = "El Molar") String destination,
			@RequestParam(required = false, defaultValue = "") List<String> waypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeWaypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeRoute,
			@RequestParam(required = false, defaultValue = "es") String language,
			@RequestParam(required = false, defaultValue = "false") boolean avoidTolls
			) {

		Optional<RouteGroup> response = routesService.getDirections(origin, destination, waypoints, optimizeWaypoints, optimizeRoute, language, avoidTolls);

		if (response.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		List<Coords> coordsList = routesService.extractRoutePoints(response.get());

		return new ResponseEntity<>(coordsList, HttpStatus.OK);
	}
	
	@Operation(
		    summary = "Obtiene todos los datos de la ruta en una sola llamada",
		    description = "Devuelve polilíneas, legs, gasolineras y clima para la ruta dada, llamando a Google solo una vez.")
		@ApiResponses(value = {
		    @ApiResponse(responseCode = "200", description = "Datos de ruta obtenidos exitosamente."),
		    @ApiResponse(responseCode = "400", description = "Solicitud errónea.")
		})
		@GetMapping("/route/fullData")
		public ResponseEntity<FullRouteData> getFullRouteData(
		    @RequestParam(required = true, defaultValue = "El Vellon") String origin,
		    @RequestParam(required = true, defaultValue = "El Molar") String destination,
		    @RequestParam(required = false, defaultValue = "") List<String> waypoints,
		    @RequestParam(required = false, defaultValue = "false") boolean optimizeWaypoints,
		    @RequestParam(required = false, defaultValue = "false") boolean optimizeRoute,
		    @RequestParam(required = false, defaultValue = "es") String language,
		    @RequestParam(required = false, defaultValue = "false") boolean avoidTolls,
		    @RequestParam(required = true, defaultValue = "1") Long gasRadius
		) {
		    Optional<FullRouteData> response = routesService.getFullRouteData(origin, destination, waypoints, optimizeWaypoints, 
		                                                                       optimizeRoute, language, avoidTolls, gasRadius);

		    if (response.isEmpty()) {
		        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		    }

		    return new ResponseEntity<>(response.get(), HttpStatus.OK);
		}

	@Operation(
			summary = "Lista de coordenadas de los pasos de una ruta",
			description = "Devuelve una lista de coordenadas para cada uno de los pasos su polyline, de la ruta dada.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Pasos encontrados para la ruta dada."),
			@ApiResponse(responseCode = "404", description = "Solicitud errónea: no se pudieron calcular los pasos de la ruta.")
	})
	@GetMapping("/route/polylineCords")
	public ResponseEntity<List<Coords>> getPolylineCoordsForRoute(
			@RequestParam(required = true, defaultValue = "El Vellon") String origin,
			@RequestParam(required = true, defaultValue = "El Molar") String destination,
			@RequestParam(required = false, defaultValue = "") List<String> waypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeWaypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeRoute,
			@RequestParam(required = false, defaultValue = "es") String language,
			@RequestParam(required = false, defaultValue = "false") boolean avoidTolls
			) {

		Optional<RouteGroup> response = routesService.getDirections(origin, destination, waypoints, optimizeWaypoints, optimizeRoute, language, avoidTolls);

		if (response.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		List<Coords> coordsList = routesService.extractRoutePolylinePoints(response.get());

		return new ResponseEntity<>(coordsList, HttpStatus.OK);
	}

	@Operation(
			summary = "Lista de coordenadas de los \"legs\" de una ruta",
			description = "Devuelve las coordenadas para cada una de las \"divisiones\" de la ruta"
					+ ", sin duplicados y en orden de recorrido."
			)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Pasos encontrados para la ruta dada."),
			@ApiResponse(responseCode = "404", description = "Solicitud errónea: no se pudieron calcular los pasos de la ruta.")
	})
	@GetMapping("/route/legCoords")
	public ResponseEntity<List<Coords>> getLegCoordsForRoute(
			@RequestParam(required = true, defaultValue = "El Vellon") String origin,
			@RequestParam(required = true, defaultValue = "El Molar") String destination,
			@RequestParam(required = false, defaultValue = "") List<String> waypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeWaypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeRoute,
			@RequestParam(required = false, defaultValue = "es") String language,
			@RequestParam(required = false, defaultValue = "false") boolean avoidTolls
			) {

		Optional<RouteGroup> response = routesService.getDirections(origin, destination, waypoints, optimizeWaypoints, optimizeRoute, language, avoidTolls);

		if (response.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		List<Coords> coordsList = routesService.getLegCoords(response.get());

		return new ResponseEntity<>(coordsList, HttpStatus.OK);
	}
}