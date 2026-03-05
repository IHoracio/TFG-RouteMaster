package tfg.service.maps.routes;

import java.util.List;
import java.util.Optional;

import org.springframework.web.util.UriComponentsBuilder;

import tfg.domain.dto.maps.routes.Coords;
import tfg.domain.dto.maps.routes.CoordsWithWeather;
import tfg.domain.dto.maps.routes.FullRouteData;
import tfg.domain.dto.maps.routes.RouteGroup;
import tfg.entity.gasolinera.Gasolinera;
import tfg.enums.EmissionType;

public interface RoutesService {

	List<Gasolinera> getGasStationsCoordsForRoute(RouteGroup routeGroup, Long radius);

	String getUrl(List<Coords> waypoints, UriComponentsBuilder url);

	List<CoordsWithWeather> getWeatherForRoute(RouteGroup routeGroup, String lang);

	List<Coords> extractRoutePoints(RouteGroup routeGroup);

	List<Coords> getLegCoords(RouteGroup routeGroup);
	
	List<Coords> extractRoutePolylinePoints(RouteGroup routeGroup);
	
	List<Coords> decodePolyline(String polylinePoints);

	Optional<RouteGroup> getDirections(String origin, String destination, List<String> waypoints,
			boolean optimizeWaypoints, boolean optimizeRoute, String language, boolean avoidTolls);
	
	Optional<FullRouteData> getFullRouteData(String origin, String destination, List<String> waypoints,
            boolean optimizeWaypoints, boolean optimizeRoute, String language, 
            boolean avoidTolls, Long gasRadius);


}
