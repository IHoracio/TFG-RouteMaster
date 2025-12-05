package es.metrica.sept25.evolutivo.service.maps.geocode;

import es.metrica.sept25.evolutivo.entity.maps.routes.Coords;

public interface GeocodeService {
	Coords getCoordinates(String address, String apiKey);
	String getMunicipio(double lat, double lng, String apiKey);
}
