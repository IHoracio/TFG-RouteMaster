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
import tfg.domain.dto.maps.routes.autocomplete.PlaceSelection;
import tfg.domain.dto.maps.routes.savedRoutes.PointDTO;
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
	
	@Override
	public Optional<FullRouteData> getFullRouteData(PlaceSelection origin, PlaceSelection destination, List<PlaceSelection> waypoints,
            boolean optimizeWaypoints, boolean optimizeRoute, String language, 
            boolean avoidTolls, Long gasRadius) {
		
	    Optional<RouteGroup> routeGroupOpt = getDirections(origin, destination, waypoints, optimizeWaypoints, 
	                                                        optimizeRoute, language, avoidTolls);
	    
	    if (routeGroupOpt.isEmpty()) {
	        return Optional.empty();
	    }
	    
		RouteGroup routeGroup = routeGroupOpt.get();
		
		// 1. Construir la lista de PointDTO dinámicamente a partir de los parámetros del formulario
	    List<PointDTO> puntosDTO = new ArrayList<>();
	    
	    if (origin != null) {
	        puntosDTO.add(new PointDTO("ORIGIN", origin));
	    }
	    
	    if (waypoints != null) {
	        for (PlaceSelection wp : waypoints) {
	            if (wp != null) {
	                puntosDTO.add(new PointDTO("WAYPOINT", wp));
	            }
	        }
	    }
	    
	    if (destination != null) {
	        puntosDTO.add(new PointDTO("DESTINATION", destination));
	    }
		        
		        return Optional.of(new FullRouteData(
		        	getTotalLegsDistance(routeGroup),
		        	getTotalLegsDuration(routeGroup),
		        	puntosDTO,
		            extractRoutePolylinePoints(routeGroup),
		            getLegCoords(routeGroup),
		            getGasStationsCoordsForRoute(routeGroup, gasRadius),
		            getWeatherForRoute(routeGroup, language)
		        ));
	}
	
	private String getTotalLegsDistance(RouteGroup routeGroup) {
	    if (routeGroup == null || 
	        routeGroup.getRoutes() == null || 
	        routeGroup.getRoutes().isEmpty()) {
	        log.warn("[routes-service] No se pudo calcular distancia total: routeGroup vacío");
	        return "0 km";
	    }

	    long totalMeters = routeGroup.getRoutes().get(0).getLegs().stream()
	            .mapToLong(leg -> leg.getDistance() != null ? leg.getDistance().getValue() : 0)
	            .sum();

	    // Convertir a km y formatear como Google
	    double totalKm = totalMeters / 1000.0;
	    
	    if (totalKm >= 100) {
	        return String.format("%.0f km", totalKm);           // 245 km
	    } else if (totalKm >= 10) {
	        return String.format("%.1f km", totalKm);           // 45.5 km
	    } else {
	        return String.format("%.2f km", totalKm);           // 8.75 km
	    }
	}

	private String getTotalLegsDuration(RouteGroup routeGroup) {
	    if (routeGroup == null || 
	        routeGroup.getRoutes() == null || 
	        routeGroup.getRoutes().isEmpty()) {
	        log.warn("[routes-service] No se pudo calcular duración total: routeGroup vacío");
	        return "0 min";
	    }

	    long totalSeconds = routeGroup.getRoutes().get(0).getLegs().stream()
	            .mapToLong(leg -> leg.getDuration() != null ? leg.getDuration().getValue() : 0)
	            .sum();

	    long hours = totalSeconds / 3600;
	    long minutes = (totalSeconds % 3600) / 60;

	    if (hours > 0) {
	        if (minutes > 0) {
	            return String.format("%d h %d min", hours, minutes);
	        } else {
	            return String.format("%d h", hours);
	        }
	    } else {
	        return String.format("%d min", minutes);
	    }
	}

	@Override
	public Optional<RouteGroup> getDirections(PlaceSelection origin, PlaceSelection destination, List<PlaceSelection> waypoints,
	        boolean optimizeWaypoints, boolean optimizeRoute, String language, boolean avoidTolls) {
	    
	    if (origin == null || destination == null) {
	        log.warn("[routes-service] Intento de calcular ruta con origen o destino nulo.");
	        return Optional.empty();
	    }

	    log.info("[routes-service] Iniciando cálculo de ruta: Origin[{}], Destination[{}], Waypoints Count: {}", 
	            origin.name() != null ? origin.name() : origin.address(), 
	            destination.name() != null ? destination.name() : destination.address(),
	            waypoints.size());

	    // --- MEJORA: Formateo dinámico de Origen y Destino ---
	    String formattedOrigin = formatLocation(origin);
	    String formattedDestination = formatLocation(destination);
	    
	    log.debug("[routes-service] Ubicaciones formateadas para Google API -> Origin: {}, Destination: {}", 
	            formattedOrigin, formattedDestination);

	    UriComponentsBuilder urlBuilder = UriComponentsBuilder
	            .fromUriString(API_URL)
	            .queryParam("mode", MODE)
	            .queryParam("language", language)
	            .queryParam("key", API_KEY_GOOGLE)
	            .queryParam("origin", formattedOrigin); // Usamos el valor formateado
	    
	    if (avoidTolls) {
	        urlBuilder.queryParam("avoid", AVOID_TOLLS);
	        log.debug("[routes-service] Evitando peajes activado.");
	    }
	    
	    if (optimizeRoute) {
	        // Para rutas circulares (Round Trip), el destino es el origen
	        urlBuilder.queryParam("destination", formattedOrigin);
	        log.debug("[routes-service] Optimización de ruta completa activa (Round Trip).");
	    } else {
	        urlBuilder.queryParam("destination", formattedDestination); // Usamos el valor formateado
	    }

	    // --- MEJORA: Manejo de Waypoints con formateo dinámico ---
	    StringBuilder waypointsValue = new StringBuilder();
	    if (optimizeWaypoints || optimizeRoute) {
	        waypointsValue.append(OPTIMIZE);
	    }
	    
	    List<String> wpIdentifiers = waypoints.stream()
	            .filter(java.util.Objects::nonNull)
	            .map(this::formatLocation) // Aplicamos la misma lógica a los waypoints
	            .collect(Collectors.toList());
	    
	    if (optimizeRoute) {
	        wpIdentifiers.add(formattedDestination);
	    }
	    
	    if (!wpIdentifiers.isEmpty()) {
	        waypointsValue.append(String.join("|", wpIdentifiers));
	        urlBuilder.queryParam("waypoints", waypointsValue.toString());
	        log.debug("[routes-service] Waypoints procesados para URL: {}", waypointsValue);
	    }
	    
	    String finalUrl = urlBuilder.build().toUriString();

	    try {
	        long startTime = System.currentTimeMillis();
	        log.debug("[routes-service] Llamando a Google Maps con URL: {}", finalUrl);
	        
	        RouteGroup response = restTemplate.getForObject(finalUrl, RouteGroup.class);
	        long duration = System.currentTimeMillis() - startTime;

	        if (response != null && "OK".equals(response.getStatus())) {
	            if (optimizeRoute) {
	                response = deleteLastLeg(response);
	            }
	            log.info("[routes-service] Ruta calculada exitosamente en {}ms.", duration);
	            return Optional.of(response);
	        } else {
	            String status = (response != null) ? response.getStatus() : "NULL_RESPONSE";
	            log.error("[routes-service] Google Maps retornó un status de error: {}. Cuerpo: {}", status, response);
	            return Optional.empty();
	        }

	    } catch (Exception e) {
	        log.error("[routes-service] Excepción crítica al llamar a Google Maps API: {}", e.getMessage());
	        return Optional.empty();
	    }
	}

	/**
	 * Método auxiliar para formatear la ubicación.
	 * Prioriza el Place ID si existe y es válido. 
	 * Si no, usa Coordenadas (lat,lng).
	 * Si no, usa la dirección textual.
	 */
	private String formatLocation(PlaceSelection location) {
	    if (location == null) return "";

	    // 1. Validar Place ID (Que no sea nulo, ni vacío, ni el literal "null")
	    String pid = location.placeId();
	    if (pid != null && !pid.trim().isEmpty() && !pid.equalsIgnoreCase("null")) {
	        return "place_id:" + pid;
	    }

	    // 2. Si no hay Place ID, usar Coordenadas (Formato: lat,lng)
	    if (location.coords() != null) {
	        log.debug("[routes-service] Usando coordenadas como fallback para: {}", location.name());
	        return location.coords().getLat() + "," + location.coords().getLng();
	    }

	    // 3. Último recurso: Dirección textual
	    log.warn("[routes-service] No se encontró Place ID ni Coordenadas, usando address: {}", location.address());
	    return location.address();
	}

	private RouteGroup deleteLastLeg(RouteGroup response) {
		List<Leg> legs = response.getRoutes().getFirst().getLegs();
		legs.removeLast();
		response.getRoutes().getFirst().setLegs(legs);

		return response;
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
