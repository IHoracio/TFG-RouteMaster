package es.metrica.sept25.evolutivo.service.maps.routes;

import java.util.List;
import java.util.Optional;

import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.RouteGroup;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.StepWithStations;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.StepWithWeather;

public interface RoutesService {
	RouteGroup deleteLastLeg(RouteGroup response);

	Optional<RouteGroup> getDirections(String origin, String destination, List<String> waypoints,
			boolean optimizeWaypoints, boolean optimizeRoute, String language);

	List<StepWithStations> getGasStationsForRoute(RouteGroup routeGroup, Long radius);

	String getUrl(List<String> waypoints, UriComponentsBuilder url);

	List<StepWithWeather> getWeatherForRoute(RouteGroup routeGroup);

	List<Coords> extractRoutePoints(RouteGroup routeGroup);
}
