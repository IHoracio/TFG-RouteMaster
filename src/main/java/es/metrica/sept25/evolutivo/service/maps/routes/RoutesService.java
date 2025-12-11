package es.metrica.sept25.evolutivo.service.maps.routes;

import java.util.List;
import java.util.Optional;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.RouteGroup;

public interface RoutesService {
	Optional<RouteGroup> getDirections(String origin, String destination, List<String> waypoints, boolean optimizeWaypoints, boolean optimizeRoute, String language);
}
