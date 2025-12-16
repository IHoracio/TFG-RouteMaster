package es.metrica.sept25.evolutivo.service.maps.routes;

import java.util.List;
import java.util.Optional;

import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.RouteGroup;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.CoordsWithStations;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.CoordsWithWeather;

public interface RoutesService {
	RouteGroup deleteLastLeg(RouteGroup response);

	Optional<RouteGroup> getDirections(String origin, String destination, List<String> waypoints,
			boolean optimizeWaypoints, boolean optimizeRoute, String language);

	List<CoordsWithStations> getGasStationsForRoute(RouteGroup routeGroup, Long radius);

	String getUrl(List<String> waypoints, UriComponentsBuilder url);

	List<CoordsWithWeather> getWeatherForRoute(RouteGroup routeGroup);

	List<Coords> extractRoutePoints(RouteGroup routeGroup);

	List<Coords> getLegCoords(RouteGroup routeGroup);
	
	List<Coords> extractRoutePolylinePoints(RouteGroup routeGroup);
	
	List<Coords> decodePolyline(String polylinePoints);
}
