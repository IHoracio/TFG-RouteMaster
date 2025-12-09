package es.metrica.sept25.evolutivo.service.maps.geocode;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.AddressComponent;
import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeGroup;
import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeGroupAddress;
import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeResultAddress;
import es.metrica.sept25.evolutivo.entity.maps.routes.Coords;

@Service
public class GeocodeServiceImpl implements GeocodeService {

	private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public Optional<Coords> getCoordinates(String address, String apiKey) {
		
		String url = UriComponentsBuilder
                .fromUriString(GEOCODE_URL)
                .queryParam("address", address)
                .queryParam("key", apiKey)
                .toUriString();
		
		GeocodeGroup response = restTemplate.getForObject(url, GeocodeGroup.class);


		if (response != null && response.getResults().length > 0) {
			return Optional.of(response.getResults()[0].getGeometry().getLocation());
        }
		return Optional.empty();
	}
	
	@Override
    public Optional<String> getMunicipio(double lat, double lng, String apiKey) {
        String url = UriComponentsBuilder
                .fromUriString(GEOCODE_URL)
                .queryParam("latlng", lat + "," + lng)
                .queryParam("key", apiKey)
                .toUriString();

        GeocodeGroupAddress response = restTemplate.getForObject(url, GeocodeGroupAddress.class);

        if (response != null && response.getResults() != null && response.getResults().length > 0) {
            GeocodeResultAddress result = response.getResults()[0];

            for (AddressComponent comp : result.getAddress_components()) {
                if (comp.getTypes() != null) {
                    List<String> types = comp.getTypes();
                    if (types.contains("locality")) {
                        return Optional.of(comp.getLong_name());
                    }
                    if (types.contains("administrative_area_level_4")) {
                        return Optional.of(comp.getLong_name());
                    }
                }
            }
        }
        return Optional.empty();
    }

}
