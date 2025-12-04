package es.metrica.sept25.evolutivo.service.maps.routes;

import java.util.List;

import es.metrica.sept25.evolutivo.entity.maps.routes.RouteGroup;

public interface RoutesService {
	RouteGroup getDirections(String origin, List<String> destinations, String language, String apiKey);
}
