package es.metrica.sept25.evolutivo.service;

import es.metrica.sept25.evolutivo.entity.maps.routes.RouteGroup;

public interface RoutesService {
	RouteGroup getDirections(String origin, String destination, String language, String apiKey);
}
