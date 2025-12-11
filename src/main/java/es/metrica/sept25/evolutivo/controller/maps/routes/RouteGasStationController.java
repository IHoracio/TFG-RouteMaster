package es.metrica.sept25.evolutivo.controller.maps.routes;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.RouteGroup;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.StepWithStations;
import es.metrica.sept25.evolutivo.service.maps.routes.RoutesService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Direcciones")
public class RouteGasStationController {
	
	@Autowired
	private RoutesService routesService;
	
	@GetMapping("/routes/gasStations")
	public ResponseEntity<List<StepWithStations>> getGasolineras(
			@RequestParam(required = true, defaultValue = "El Vellon") String origin,
			@RequestParam(required = true, defaultValue = "El Molar") String destination,
			@RequestParam(required = false, defaultValue = "") List<String> waypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeWaypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeRoute,
			@RequestParam(required = false, defaultValue = "es") String language,
			@RequestParam(required = true, defaultValue = "5") Long radius
			) {
		
		Optional<RouteGroup> routeGroupOpt = routesService.getDirections(origin, destination, waypoints, optimizeWaypoints, optimizeRoute, language);
		
		if (routeGroupOpt.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		List<StepWithStations> coordsWithStations = routesService.getGasStationsForRoute(routeGroupOpt.get(), radius);

		return new ResponseEntity<>(coordsWithStations, HttpStatus.OK);
	}

}
