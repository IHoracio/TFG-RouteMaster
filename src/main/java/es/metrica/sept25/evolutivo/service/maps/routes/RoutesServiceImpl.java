package es.metrica.sept25.evolutivo.service.maps.routes;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.entity.maps.routes.RouteGroup;

@Service
public class RoutesServiceImpl implements RoutesService {

	private static final String API_URL = "https://maps.googleapis.com/maps/api/directions/json";
	private static final String MODE = "driving";
	private static final String OPTIMIZE = "optimize:true";

	@Autowired
	private RestTemplate restTemplate;

	public RouteGroup getDirections(String origin, String destination, List<String> waypoints, String language, String apiKey) {
		UriComponentsBuilder url = UriComponentsBuilder
				.fromUriString(API_URL)
				.queryParam("origin", origin.replaceAll(" ", ""))
				.queryParam("destination", destination.replaceAll(" ", ""))
				.queryParam("mode", MODE)
				.queryParam("language", language)
				.queryParam("key", apiKey);

		if(!waypoints.isEmpty()) {
			String waypointsValue = waypoints.stream()
					.map(s -> s.replaceAll(" ", ""))
					.collect(Collectors.joining("|"));

			url.queryParam("waypoints", waypointsValue);
		}

		String result = url.toUriString().replaceAll("%7", "|");

		return restTemplate.getForObject(result, RouteGroup.class);	
	}

}
