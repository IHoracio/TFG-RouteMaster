package es.metrica.sept25.evolutivo.service.maps.routes;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.entity.maps.routes.Leg;
import es.metrica.sept25.evolutivo.entity.maps.routes.RouteGroup;

@Service
public class RoutesServiceImpl implements RoutesService {

	private static final String API_URL = "https://maps.googleapis.com/maps/api/directions/json";
	private static final String MODE = "driving";
	private static final String OPTIMIZE = "optimize:true|";

	@Autowired
	private RestTemplate restTemplate;

	public RouteGroup getDirections(String origin, String destination, List<String> waypoints, Boolean optimice, String language, String apiKey) {
		UriComponentsBuilder url = UriComponentsBuilder
				.fromUriString(API_URL)
				.queryParam("origin", origin.replaceAll(" ", ""))
				.queryParam("mode", MODE)
				.queryParam("language", language)
				.queryParam("key", apiKey);
		
		boolean canOptimice = !waypoints.isEmpty() && optimice != null && optimice;
		if(!canOptimice) url.queryParam("destination", destination.replaceAll(" ", ""));
		
		String result = "";
		if(!waypoints.isEmpty()) {
			StringBuilder waypointsValue = new StringBuilder();
			
			if(canOptimice) {
				waypoints.add(destination);
				waypointsValue.append(OPTIMIZE);
				url.queryParam("destination", origin);
			}
			
			result = addWaypoints(waypointsValue, waypoints, url);
		} else {
			result = url.toUriString();
		}

		RouteGroup response = restTemplate.getForObject(result, RouteGroup.class);
		if(canOptimice) response = deleteLastLeg(response);
		
		return response;	
	}

	private RouteGroup deleteLastLeg(RouteGroup response) {
		List<Leg> legs = response.getRoutes().getFirst().getLegs();
		legs.removeLast();
		response.getRoutes().getFirst().setLegs(legs);
		
		return response;
	}

	private String addWaypoints(StringBuilder waypointsValue, List<String> waypoints, UriComponentsBuilder url) {
		waypointsValue.append(waypoints.stream()
				.map(s -> s.replaceAll(" ", ""))
				.collect(Collectors.joining("|")));
		url.queryParam("waypoints", "");
		
		return url.toUriString() + waypointsValue.toString();
	}

}
