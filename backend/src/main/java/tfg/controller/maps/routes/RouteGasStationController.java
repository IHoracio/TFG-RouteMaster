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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import tfg.domain.dto.maps.routes.Coords;
import tfg.domain.dto.maps.routes.RouteGroup;
import tfg.domain.dto.maps.routes.RouteRequestDto;
import tfg.entity.gasolinera.Gasolinera;
import tfg.service.maps.routes.RoutesService;

@RestController
@Tag(name = "Direcciones")
public class RouteGasStationController {

	@Autowired
	private RoutesService routesService;

	@Operation(
		    summary = "Lista de las gasolineras en un radio de los pasos de una ruta",
		    description = "Recibe el origen, destino y paradas para calcular una ruta y buscar gasolineras "
		                + "dentro del radio especificado (gasRadius) a lo largo del trayecto.")
		@ApiResponses(value = {
		    @ApiResponse(responseCode = "200", description = "Gasolineras encontradas con éxito."),
		    @ApiResponse(responseCode = "400", description = "Solicitud errónea: no se pudo calcular la ruta o encontrar gasolineras.")
		})
		@PostMapping("/routes/gasStations")
		public ResponseEntity<List<Gasolinera>> getGasolineras(@RequestBody RouteRequestDto request) {

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

		    // Buscamos las gasolineras usando el radio que viene en el DTO
		    List<Gasolinera> gasStations = routesService.getGasStationsCoordsForRoute(
		            routeGroupOpt.get(), 
		            request.gasRadius() != null ? request.gasRadius() : 1L
		    );

		    return new ResponseEntity<>(gasStations, HttpStatus.OK);
		}
}
