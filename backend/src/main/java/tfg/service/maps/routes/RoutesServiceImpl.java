package tfg.service.maps.routes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;

import tfg.domain.dto.maps.routes.Coords;
import tfg.domain.dto.maps.routes.CoordsWithWeather;
import tfg.domain.dto.maps.routes.FullRouteData;
import tfg.domain.dto.maps.routes.Leg;
import tfg.domain.dto.maps.routes.RouteGroup;
import tfg.domain.dto.maps.routes.Step;
import tfg.domain.dto.weather.EstadoCielo;
import tfg.domain.dto.weather.HourlyWeather;
import tfg.domain.dto.weather.Weather;
import tfg.entity.gasolinera.Gasolinera;
import tfg.service.gasolineras.GasolineraService;
import tfg.service.maps.geocode.GeocodeService;
import tfg.service.maps.geocode.ReverseGeocodeService;
import tfg.service.weather.WeatherService;

@Service
public class RoutesServiceImpl implements RoutesService {

	private static final Logger log = LoggerFactory.getLogger(RoutesServiceImpl.class);

	
	private static final String API_URL = "https://maps.googleapis.com/maps/api/directions/json";
	private static final String MODE = "driving";
	private static final String OPTIMIZE = "optimize:true|";
	private static final String AVOID_TOLLS = "tolls";

	@Value("${evolutivo.api_key_google}")
	private String API_KEY_GOOGLE;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	GasolineraService gasolineraService;

	@Autowired
	private WeatherService weatherService;
	
	@Autowired
	private ReverseGeocodeService reverseGeocodeService;
	
	@Autowired
	private GeocodeService geocodeService;
	
	@Override
	public Optional<FullRouteData> getFullRouteData(String origin, String destination, List<String> waypoints,
	                                      boolean optimizeWaypoints, boolean optimizeRoute, String language, 
	                                      boolean avoidTolls, Long gasRadius) {

	    Optional<RouteGroup> routeGroupOpt = getDirections(origin, destination, waypoints, optimizeWaypoints, 
	                                                        optimizeRoute, language, avoidTolls);
	    
	    if (routeGroupOpt.isEmpty()) {
	        return Optional.empty();
	    }
	    
	    RouteGroup routeGroup = routeGroupOpt.get();
	    
	    List<Coords> polylineCoords = extractRoutePolylinePoints(routeGroup);
	    List<Coords> legCoords = getLegCoords(routeGroup);
	    List<Gasolinera> gasStations = getGasStationsCoordsForRoute(routeGroup, gasRadius);
	    List<CoordsWithWeather> weatherData = getWeatherForRoute(routeGroup, language);
	    
	    return Optional.of(new FullRouteData(polylineCoords, legCoords, gasStations, weatherData));
	}
	
	@Override
	public Optional<RouteGroup> getDirections(String origin, String destination, List<String> waypoints,
			boolean optimizeWaypoints, boolean optimizeRoute, String language, boolean avoidTolls) {

		Set<String> invalidDirections = new HashSet<String>();
		
		Optional<Coords> originCoords = geocodeService.getCoordinates(origin);

		if (originCoords.isEmpty()) {
			invalidDirections.add(origin);
		}
		
		Optional<Coords> destinationCoords = geocodeService.getCoordinates(destination);

		if (destinationCoords.isEmpty()) {
			invalidDirections.add(destination);
		}

		List<Coords> waypointsCoords = new ArrayList<>();
		
		if(!waypoints.isEmpty()) {
			for (String waypoint : waypoints) {
				Optional<Coords> waypointCoords = geocodeService.getCoordinates(waypoint);
				
				if (waypointCoords.isEmpty()) {
					invalidDirections.add(waypoint);
				} else {
					waypointsCoords.add(waypointCoords.get());
				}
			}
		}
		
		if(!invalidDirections.isEmpty()) {
			
			log.error("[routes-service] [" + LocalDateTime.now().toString() + "] "
					+ "Invalid directions were found: \n" 
					+ invalidDirections.toString() + ".");
			return Optional.empty();
		}
		
		UriComponentsBuilder url = UriComponentsBuilder
				.fromUriString(API_URL)
				.queryParam("mode", MODE)
				.queryParam("language", language)
				.queryParam("key", API_KEY_GOOGLE)
				.queryParam("origin", originCoords.get().toString());

		if (avoidTolls) {
		    url.queryParam("avoid", AVOID_TOLLS);
		}
		
		if (!optimizeRoute) {
			url.queryParam("destination", destinationCoords.get().toString());
		}

		String result = "";
		if (!waypoints.isEmpty() && 
				optimizeWaypoints || optimizeRoute) {

			result += OPTIMIZE;
			if (optimizeRoute) {
				waypointsCoords.add(destinationCoords.get());
				url.queryParam("destination", originCoords.get().toString());
			}
		}

		result = getUrl(waypointsCoords, url);

		RouteGroup response = restTemplate.getForObject(result, RouteGroup.class);

		if (!waypoints.isEmpty() && optimizeRoute) {
			response = deleteLastLeg(response);
		}

		log.info("[routes-service] [" + LocalDateTime.now().toString() + "] "
				+ "Sucessfully calculated directions for the given data: \n ["
				+ origin + " - " + waypoints.toString() + " - " + destination
				+ " | OptWay=" + optimizeWaypoints + ", OptRoute=" + optimizeRoute
				+ ", lang=" + language + "]");
		return Optional.of(response);
	}

	private RouteGroup deleteLastLeg(RouteGroup response) {
		List<Leg> legs = response.getRoutes().getFirst().getLegs();
		legs.removeLast();
		response.getRoutes().getFirst().setLegs(legs);

		return response;
	}

	@Override
	public String getUrl(List<Coords> waypoints, UriComponentsBuilder url) {
		log.info("[routes-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to compose a URL from a list of waypoints.");
		url.queryParam("waypoints", "");

		return url.toUriString() + waypoints.stream().map(coord -> coord.toString()).collect(Collectors.joining("|"));
	}

	@Override
	public List<Coords> extractRoutePoints(RouteGroup routeGroup) {
		if (routeGroup == null || routeGroup.getRoutes() == null) {
			log.error("[routes-service] [" + LocalDateTime.now().toString() + "] "
					+ "No route or routeGroup were given.");
			return List.of();
		}

		return routeGroup.getRoutes().stream()
				.flatMap(route -> route.getLegs().stream())
				.flatMap(leg -> leg.getSteps().stream())
				.map(Step::getStartLocation)
				.collect(Collectors.toList());
	}
	
	@Override
	public List<Coords> extractRoutePolylinePoints(RouteGroup routeGroup) {
		log.info("[routes-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to extract route polylinePoints for a given route.");
		if (routeGroup == null || routeGroup.getRoutes() == null) {
			log.error("[routes-service] [" + LocalDateTime.now().toString() + "] "
					+ "No route or routeGroup were given.");
			return List.of();
		}
		
		return routeGroup.getRoutes().stream()
				.flatMap(route -> route.getLegs().stream())
				.flatMap(leg -> leg.getSteps().stream())
				.map(step -> decodePolyline(step.getPolyline().getPoints()))
				.flatMap(coordsList -> coordsList.stream())
				.collect(Collectors.toList());
	}

	@Override
	public List<Coords> decodePolyline(String polylinePoints) {
		EncodedPolyline polyline = new EncodedPolyline(polylinePoints);
		List<LatLng> latLngs = polyline.decodePath();
		
		return latLngs.stream()
				.map(latLng -> new Coords(latLng.lat, latLng.lng))
				.collect(Collectors.toList());
	}

	@Override
	public List<CoordsWithWeather> getWeatherForRoute(RouteGroup routeGroup, String lang) {
		log.info("[routes-service] [" + LocalDateTime.now() + "] "
	            + "Attempting to get weather for each leg of the route.");

	    List<Coords> legCoords = getLegCoords(routeGroup);

	    if (legCoords.isEmpty()) {
	        return List.of();
	    }

		return legCoords.stream()
				.map(coords -> {
					String address = reverseGeocodeService
							.getAddress(coords.getLat(), coords.getLng())
							.orElse("Unknown address");

					Optional<Weather> weatherOpt =
							weatherService.getWeather(coords.getLat(), coords.getLng(), lang, address);

					if (weatherOpt.isEmpty()) {
						log.warn("[routes-service] [" + LocalDateTime.now().toString() + "] "
								+ "No weather data could be retrieved for coords: "
								+ coords.toString());
						return new CoordsWithWeather(
								address,
								new HashMap<>(),
								new HashMap<>(),
								new HashMap<>(),
								new HashMap<>(),
								new HashMap<>(),
								List.of()
								);
					}

					Weather weather = weatherOpt.get();

					Map<Integer, String> mapaAlertas = new HashMap<>();
					Map<Integer, Double> mapaTemperaturas = new HashMap<>();
					Map<Integer, Double> mapaFeelsLike = new HashMap<>();
					Map<Integer, Double> mapaWindSpeed = new HashMap<>();
					Map<Integer, Integer> mapaVisibility = new HashMap<>();
					
					// Get weather data from hourly array
					if (weather.getHourly() != null && !weather.getHourly().isEmpty()) {
						for (int i = 0; i < weather.getHourly().size(); i++) {
							HourlyWeather hourly = weather.getHourly().get(i);
							
							// Get weather descriptions
							if (hourly.getWeather() != null && !hourly.getWeather().isEmpty()) {
								EstadoCielo estado = hourly.getWeather().get(0);
								mapaAlertas.put(i, estado.getDescription());
							}
							
							// Get temperature
							if (hourly.getTemp() != null) {
								mapaTemperaturas.put(i, hourly.getTemp());
							}
							
							// Get feels like temperature
							if (hourly.getFeelsLike() != null) {
								mapaFeelsLike.put(i, hourly.getFeelsLike());
							}
							
							// Get wind speed
							if (hourly.getWindSpeed() != null) {
								mapaWindSpeed.put(i, hourly.getWindSpeed());
							}
							
							// Get visibility
							if (hourly.getVisibility() != null) {
								mapaVisibility.put(i, hourly.getVisibility());
							}
						}
					}

					return new CoordsWithWeather(
							address,
							mapaAlertas,
							mapaTemperaturas,
							mapaFeelsLike,
							mapaWindSpeed,
							mapaVisibility,
							weather.getAlerts() != null ? weather.getAlerts() : List.of()
							);
				})
				.toList();
	}

	private int calculateMaxCalls(long meters) {

        if (meters <= 5_000) return 2;
        if (meters <= 20_000) return 4;
        if (meters <= 100_000) return 8;
        return 14;
    }
	private List<Coords> getSampledRoutePoints(RouteGroup routeGroup) {

	    List<Coords> allPoints = extractRoutePolylinePoints(routeGroup);

	    if (allPoints.isEmpty()) {
	        log.warn("[routes-service] [" + LocalDateTime.now() + "] No polyline points extracted.");
	        return List.of();
	    }

	    long totalMeters = routeGroup.getRoutes().get(0).getLegs().stream()
	            .mapToLong(leg -> leg.getDistance().getValue())
	            .sum();

	    int maxCalls = calculateMaxCalls(totalMeters);

	    int step = Math.max(
	            1,
	            (int) Math.ceil((double) allPoints.size() / maxCalls)
	    );

	    return IntStream.range(0, allPoints.size())
	            .filter(i -> i % step == 0 || i == allPoints.size() - 1)
	            .mapToObj(allPoints::get)
	            .toList();
	}
	
	@Override
	public List<Gasolinera> getGasStationsCoordsForRoute(RouteGroup routeGroup, Long radius) {
		log.info("[routes-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to extract coordinates for all gas stations in the route's radius: " 
				+ radius + ".");
		List<Coords> sampledRoutePoints = getSampledRoutePoints(routeGroup);

	    if (sampledRoutePoints.isEmpty()) {
	        return List.of();
	    }
		
	    List<Gasolinera> stationsForRoute = sampledRoutePoints.stream()
	    	    .flatMap(point -> gasolineraService
	    	                        .getGasolinerasInRadiusCoords(point.getLat(), point.getLng(), radius)
	    	                        .stream()
	    	    )
	    	    .distinct()
	    	    .collect(Collectors.toList());
		
		return stationsForRoute;
	}
	
	@Override
	public List<Coords> getLegCoords(RouteGroup routeGroup) {
		log.info("[routes-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to extract leg coordinates for a given route.");
		List<Coords> legCoords = routeGroup.getRoutes().stream()
				.flatMap(route -> route.getLegs().stream())
				.flatMap(leg -> {
					Coords startLoc = leg.getStartLocation();
					Coords endLoc = leg.getEndLocation();
					List<Coords> legList = List.of(startLoc, endLoc);
					return legList.stream();
				})
				.distinct()
				.collect(Collectors.toList());

		log.info("[routes-service] [" + LocalDateTime.now().toString() + "] "
				+ "Successfully retrieved the leg coordinates for the given route.");
		return legCoords;
	}

}
