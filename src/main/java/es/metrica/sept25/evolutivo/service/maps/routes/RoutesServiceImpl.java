package es.metrica.sept25.evolutivo.service.maps.routes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.CoordsWithWeather;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Leg;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.RouteGroup;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Step;
import es.metrica.sept25.evolutivo.domain.dto.weather.Dia;
import es.metrica.sept25.evolutivo.domain.dto.weather.EstadoCielo;
import es.metrica.sept25.evolutivo.domain.dto.weather.Temperatura;
import es.metrica.sept25.evolutivo.domain.dto.weather.Weather;
import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.service.gasolineras.GasolineraService;
import es.metrica.sept25.evolutivo.service.ine.INEService;
import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeService;
import es.metrica.sept25.evolutivo.service.maps.geocode.ReverseGeocodeService;
import es.metrica.sept25.evolutivo.service.weather.WeatherService;

@Service
public class RoutesServiceImpl implements RoutesService {

	private static final String API_URL = "https://maps.googleapis.com/maps/api/directions/json";
	private static final String MODE = "driving";
	private static final String OPTIMIZE = "optimize:true|";

	@Value("${evolutivo.api_key_google}")
	private String API_KEY_GOOGLE;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private INEService ineService;

	@Autowired
	GasolineraService gasolineraService;

	@Autowired
	private WeatherService weatherService;
	
	@Autowired
	private ReverseGeocodeService reverseGeocodeService;
	
	@Autowired
	private GeocodeService geocodeService;

	@Override
	public Optional<RouteGroup> getDirections(String origin, String destination, List<String> waypoints,
			boolean optimizeWaypoints, boolean optimizeRoute, String language) {
		Set<String> invalidDirections = new HashSet<String>();
		
		Optional<Coords> originCoords = geocodeService.getCoordinates(origin);
		if(originCoords.isEmpty()) invalidDirections.add(origin);
		
		Optional<Coords> destinationCoords = geocodeService.getCoordinates(destination);
		if(destinationCoords.isEmpty()) invalidDirections.add(destination);
		
	    List<Coords> waypointsCoords = new ArrayList<>();
		
		if(!waypoints.isEmpty()) {
			for(String waypoint : waypoints) {
				Optional<Coords> waypointCoords = geocodeService.getCoordinates(waypoint);
				
				if(waypointCoords.isEmpty()) invalidDirections.add(waypoint);
				else waypointsCoords.add(waypointCoords.get());
			}
		}
		
		if(!invalidDirections.isEmpty()) {
			
			System.err.println("Direcciones invalidas: " + invalidDirections.toString());
			return Optional.empty();
		}
		
		UriComponentsBuilder url = UriComponentsBuilder.fromUriString(API_URL).queryParam("mode", MODE)
				.queryParam("language", language).queryParam("key", API_KEY_GOOGLE).queryParam("origin", originCoords.get().toString());

		if (!optimizeRoute)
			url.queryParam("destination", destinationCoords.get().toString());

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
		if (!waypoints.isEmpty() && optimizeRoute)
			response = deleteLastLeg(response);

		return Optional.of(response);
	}

	@Override
	public RouteGroup deleteLastLeg(RouteGroup response) {
		List<Leg> legs = response.getRoutes().getFirst().getLegs();
		legs.removeLast();
		response.getRoutes().getFirst().setLegs(legs);

		return response;
	}

	@Override
	public String getUrl(List<Coords> waypoints, UriComponentsBuilder url) {
		url.queryParam("waypoints", "");

		return url.toUriString() + waypoints.stream().map(coord -> coord.toString()).collect(Collectors.joining("|"));
	}

	@Override
	public List<Coords> extractRoutePoints(RouteGroup routeGroup) {
		if (routeGroup == null || routeGroup.getRoutes() == null)
			return List.of();

		return routeGroup.getRoutes().stream()
				.flatMap(route -> route.getLegs().stream())
				.flatMap(leg -> leg.getSteps().stream())
				.map(Step::getStartLocation)
				.collect(Collectors.toList());
	}
	
	@Override
	public List<Coords> extractRoutePolylinePoints(RouteGroup routeGroup) {
		if (routeGroup == null || routeGroup.getRoutes() == null)
			return List.of();
		
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
	public List<CoordsWithWeather> getWeatherForRoute(RouteGroup routeGroup) {
		return extractRoutePoints(routeGroup).stream().map(coords -> {
			
			String address = reverseGeocodeService
	                .getAddress(coords.getLat(), coords.getLng())
	                .orElse("Ubicación desconocida");
			
			Optional<String> codigoINE = ineService.getCodigoINE(coords.getLat(), coords.getLng());
			if (codigoINE.isEmpty()) {
				return new CoordsWithWeather(address, new HashMap<>(), new HashMap<>());
			}

			Optional<Weather> weatherOpt = weatherService.getWeather(codigoINE.get());
			if (weatherOpt.isEmpty()) {
				return new CoordsWithWeather(address, new HashMap<>(), new HashMap<>());
			}

			Weather weather = weatherOpt.get();

			Dia dia = weather.getPrediccion().getDia().get(0);

			Map<Integer, String> mapaDescripciones = new HashMap<>();
	        if (dia.getEstadoCielo() != null && !dia.getEstadoCielo().isEmpty()) {
	            for (EstadoCielo estado : dia.getEstadoCielo()) {
	                mapaDescripciones.put(estado.getPeriodo(), estado.getDescripcion());
	            }
	        }

			Map<Integer, Double> mapaTemperaturas = new HashMap<>();

			if (dia.getTemperatura() != null && !dia.getTemperatura().isEmpty()) {
			    for (Temperatura temp : dia.getTemperatura()) {
			        mapaTemperaturas.put(temp.getPeriodo(), temp.getValue());
			    }
			}

			return new CoordsWithWeather(address, mapaDescripciones, mapaTemperaturas);
		}).toList();
	}

	@Override
	public List<Coords> getGasStationsCoordsForRoute(RouteGroup routeGroup, Long radius) {
		List<Coords> coords = extractRoutePoints(routeGroup);
		
		List<Coords> stationsForRoute = coords.stream().flatMap(
				coord ->
				{ 
					List<Gasolinera> g = gasolineraService.getGasolinerasInRadiusCoords(coord.getLat(), coord.getLng(), radius);
					return g.stream();
				 })
				.peek(g -> {
					System.out.println(g.getDireccion());
					System.out.println(g.getLatitud() + "|" + g.getLongitud());
				})
				.map(station -> {
					return new Coords(station.getLatitud(), station.getLongitud());
				})
				.distinct()
				.toList();
		
		return stationsForRoute;
	}
	
	@Override
	public List<Coords> getLegCoords(RouteGroup routeGroup) {
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

		return legCoords;
	}

}
