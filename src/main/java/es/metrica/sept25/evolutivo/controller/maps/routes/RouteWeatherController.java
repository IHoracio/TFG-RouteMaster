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
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.StepWithWeather;
import es.metrica.sept25.evolutivo.service.maps.routes.RoutesService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Direcciones")
public class RouteWeatherController {

	@Autowired
	private RoutesService routesService;

	@GetMapping("/routes/weather")
	public ResponseEntity<List<StepWithWeather>> getRouteWeather(
			@RequestParam(required = true, defaultValue = "El Vellon") String origin,
			@RequestParam(required = true, defaultValue = "El Molar") String destination,
			@RequestParam(required = false, defaultValue = "") List<String> waypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeWaypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeRoute,
			@RequestParam(required = false, defaultValue = "es") String language) {

		Optional<RouteGroup> routeGroupOpt = routesService.getDirections(origin, destination, waypoints,
				optimizeWaypoints, optimizeRoute, language);

		if (routeGroupOpt.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		List<StepWithWeather> stepsWithWeather = routesService.getWeatherForRoute(routeGroupOpt.get());
		return new ResponseEntity<>(stepsWithWeather, HttpStatus.OK);
	}
}
