package es.metrica.sept25.evolutivo.service.maps.geocode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.entity.maps.geocode.GeocodeGroup;
import es.metrica.sept25.evolutivo.entity.maps.routes.Coords;

@Service
public class GeocodeServiceImpl implements GeocodeService {

	private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public Coords getCoordinates(String address, String apiKey) {
		
		String url = UriComponentsBuilder
                .fromUriString(GEOCODE_URL)
                .queryParam("address", address)
                .queryParam("key", apiKey)
                .toUriString();
		
		GeocodeGroup response = restTemplate.getForObject(url, GeocodeGroup.class);

		if (response != null && response.getResults().length > 0) {
			return response.getResults()[0].getGeometry().getLocation();
		}
		return null;
	}

}
