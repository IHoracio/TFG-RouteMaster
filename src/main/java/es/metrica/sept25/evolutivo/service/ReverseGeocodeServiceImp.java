package es.metrica.sept25.evolutivo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.web.util.UriComponentsBuilder;
import es.metrica.sept25.evolutivo.entity.reverseGeocode.ReverseGeocodeGroup;
import org.springframework.web.client.RestTemplate;

@Service
public class ReverseGeocodeServiceImp implements ReverseGeocodeService{

	private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Override
	public String getAddress(double lat, double lng, String apiKey) {
		String latlng = lat + "," + lng;
        String url = UriComponentsBuilder
                .fromUriString(GEOCODE_URL)
                .queryParam("latlng", latlng)
                .queryParam("key", apiKey)
                .toUriString();
        
        ReverseGeocodeGroup response = restTemplate.getForObject(url, ReverseGeocodeGroup.class);
        return "";
	}

}
