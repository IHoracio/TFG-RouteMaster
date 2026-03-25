package tfg.controller.maps.routes;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import tfg.domain.dto.maps.routes.CoordsWithWeather;
import tfg.domain.dto.maps.routes.RouteGroup;
import tfg.domain.dto.maps.routes.RouteRequestDto;
import tfg.service.maps.routes.RoutesService;

@RestController
@Tag(name = "Direcciones")
public class RouteWeatherController {

	@Autowired
	private RoutesService routesService;


	@Operation(
		    summary = "Lista de coordenadas y el clima para cada set de coordenadas (los pasos de una ruta).", 
		    description = "Recibe un objeto con origen, destino y paradas para calcular el clima por horas en cada punto de la ruta.")
		@ApiResponses(value = { 
		    @ApiResponse(responseCode = "200", description = "Pasos y clima encontrados para la ruta dada."),
		    @ApiResponse(responseCode = "400", description = "Solicitud errónea: no se pudieron calcular los pasos de la ruta.")
		})
		@PostMapping("/routes/weather")
		public ResponseEntity<List<CoordsWithWeather>> getRouteWeather(@RequestBody RouteRequestDto request) {

		    Optional<RouteGroup> routeGroupOpt = routesService.getDirections(
		            request.origin(), 
		            request.destination(), 
		            request.waypoints(), 
		            request.optimizeWaypoints(), 
		            request.optimizeRoute(), 
		            request.language(), 
		            request.avoidTolls()
		    );

		    if (routeGroupOpt.isEmpty()) {
		        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		    }

		    // Calculamos el clima para la ruta obtenida
		    List<CoordsWithWeather> stepsWithWeather = routesService.getWeatherForRoute(
		            routeGroupOpt.get(), 
		            request.language()
		    );
		    
		    return new ResponseEntity<>(stepsWithWeather, HttpStatus.OK);
		}
}
