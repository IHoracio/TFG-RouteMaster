package es.metrica.sept25.evolutivo.service.maps.routes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.entity.maps.routes.RouteGroup;

@Service
public class RoutesServiceImpl implements RoutesService {

	private static final String API_URL = "https://maps.googleapis.com/maps/api/directions/json";
	private static final String MODE = "driving";
	private static final String OPTIMIZE = "optimize:true|";

	@Autowired
    private RestTemplate restTemplate;
    
    public RouteGroup getDirections(String origin, List<String> destinations, String language, String apiKey) {
    	UriComponentsBuilder url = UriComponentsBuilder
    			.fromUriString(API_URL)
    		    .queryParam("origin", origin)
    		    .queryParam("destination", destinations.getFirst())
    		    .queryParam("mode", MODE)
    		    .queryParam("language", language)
    		    .queryParam("key", apiKey);
    	
    	if(destinations.size() > 1) {
    		String finalDestination = destinations.getLast();
    		url.queryParam("destination", finalDestination);
    		
    		List<String> intermediateDestinations = IntStream.range(1, destinations.size()-1)
    				.mapToObj(i -> destinations.get(i))
    				.toList();
    		
    		String waypointsParam = OPTIMIZE + intermediateDestinations.stream()
    				.collect(Collectors.joining("|"));
    		
    		url.queryParam("waypoints", waypointsParam);
    	}
        
        return restTemplate.getForObject(url.toUriString(), RouteGroup.class);
    }
}
