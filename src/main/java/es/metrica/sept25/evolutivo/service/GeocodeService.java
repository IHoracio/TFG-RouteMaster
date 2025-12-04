package es.metrica.sept25.evolutivo.service;

import es.metrica.sept25.evolutivo.entity.maps.routes.Coords;

public interface GeocodeService {

	Coords getCoordinates(String address, String apiKey);
}
