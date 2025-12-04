package es.metrica.sept25.evolutivo.service.maps.geocode;

public interface ReverseGeocodeService {

	 String getAddress(double lat, double lng, String apiKey);
}
