package es.metrica.sept25.evolutivo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.entity.maps.routes.RouteGroup;

@Service
public class RoutesServiceImpl implements RoutesService {

	private static final String API_URL = "https://maps.googleapis.com/maps/api/directions/json";
	private static final String MODE = "driving";

	@Autowired
    private RestTemplate restTemplate;
    
    public RouteGroup getDirections(String origin, String destination, String language, String apiKey) {
    	String url = UriComponentsBuilder
    			.fromUriString(API_URL)
    		    .queryParam("origin", origin)
    		    .queryParam("destination", destination)
    		    .queryParam("mode", MODE)
    		    .queryParam("language", language)
    		    .queryParam("key", apiKey)
    		    .toUriString();
        
        return restTemplate.getForObject(url, RouteGroup.class);
    }
}
