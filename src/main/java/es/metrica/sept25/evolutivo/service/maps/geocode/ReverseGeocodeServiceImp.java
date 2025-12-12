package es.metrica.sept25.evolutivo.service.maps.geocode;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.entity.maps.reverseGeocode.ReverseGeocodeGroup;

@Service
public class ReverseGeocodeServiceImp implements ReverseGeocodeService{

	private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Value("${evolutivo.api_key_google}")
	private String API_KEY_GOOGLE;
	
	@Override
	public Optional<String> getAddress(double lat, double lng) {
		String latlng = lat + "," + lng;
        String url = UriComponentsBuilder
                .fromUriString(GEOCODE_URL)
                .queryParam("latlng", latlng)
                .queryParam("key", API_KEY_GOOGLE)
                .toUriString();
        
        ReverseGeocodeGroup response = restTemplate.getForObject(url, ReverseGeocodeGroup.class);
        
        if(response != null && response.getResults().length > 0) {
        	return Optional.of(response.getResults()[0].getFormatted_address());
        }
        
        return Optional.empty();
	}

}
