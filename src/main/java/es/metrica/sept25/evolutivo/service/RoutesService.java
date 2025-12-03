package es.metrica.sept25.evolutivo.service;

import es.metrica.sept25.evolutivo.entity.routes.Routes;

public interface RoutesService {
	Routes getDirections(String origin, String destination, String language, String apiKey);
	
}
