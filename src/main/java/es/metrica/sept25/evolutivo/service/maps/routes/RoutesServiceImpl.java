package es.metrica.sept25.evolutivo.service.maps.routes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${evolutivo.api_key_google")
	private String API_KEY_GOOGLE;

	@Autowired
	private RestTemplate restTemplate;

	public Optional<RouteGroup> getDirections(String origin, String destination, List<String> waypoints, boolean optimizeWaypoints, boolean optimizeRoute, String language) {
		origin = origin.replaceAll(" ", "");
		destination = destination.replaceAll(" ", "");

		UriComponentsBuilder url = UriComponentsBuilder
				.fromUriString(API_URL)
				.queryParam("mode", MODE)
				.queryParam("language", language)
				.queryParam("key", API_KEY_GOOGLE)
				.queryParam("origin", origin);
		
		if(waypoints.isEmpty() || !optimizeRoute) url.queryParam("destination", destination);
		
		String result = "";
		if(!waypoints.isEmpty()) {
			if(optimizeWaypoints) result += OPTIMIZE;
			else if(optimizeRoute) {
				waypoints.add(destination);
				url.queryParam("destination", origin);
			}
		} 
		result = getUrl(waypoints, url);

		RouteGroup response = restTemplate.getForObject(result, RouteGroup.class);
		if(!waypoints.isEmpty() && optimizeRoute) response = deleteLastLeg(response);

		return Optional.of(response);	
	}

	private RouteGroup deleteLastLeg(RouteGroup response) {
		List<Leg> legs = response.getRoutes().getFirst().getLegs();
		legs.removeLast();
		response.getRoutes().getFirst().setLegs(legs);

		return response;
	}

	private String getUrl(List<String> waypoints, UriComponentsBuilder url) {
		url.queryParam("waypoints", "");

		return url.toUriString() + waypoints.stream()
		.map(s -> s.replaceAll(" ", ""))
		.collect(Collectors.joining("|"));
	}

}
