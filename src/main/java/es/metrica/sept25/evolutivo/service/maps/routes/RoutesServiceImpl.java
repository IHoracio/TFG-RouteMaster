package es.metrica.sept25.evolutivo.service.maps.routes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.CoordsWithStations;
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

	@Override
	public Optional<RouteGroup> getDirections(String origin, String destination, List<String> waypoints,
			boolean optimizeWaypoints, boolean optimizeRoute, String language) {
		origin = origin.replaceAll(" ", "");
		destination = destination.replaceAll(" ", "");

		UriComponentsBuilder url = UriComponentsBuilder.fromUriString(API_URL).queryParam("mode", MODE)
				.queryParam("language", language).queryParam("key", API_KEY_GOOGLE).queryParam("origin", origin);

		if (waypoints.isEmpty() || !optimizeRoute)
			url.queryParam("destination", destination);

		String result = "";
		if (!waypoints.isEmpty()) {
			if (optimizeWaypoints)
				result += OPTIMIZE;
			else if (optimizeRoute) {
				waypoints.add(destination);
				url.queryParam("destination", origin);
			}
		}
		result = getUrl(waypoints, url);

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
	public String getUrl(List<String> waypoints, UriComponentsBuilder url) {
		url.queryParam("waypoints", "");

		return url.toUriString() + waypoints.stream().map(s -> s.replaceAll(" ", "")).collect(Collectors.joining("|"));
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
	public List<CoordsWithWeather> getWeatherForRoute(RouteGroup routeGroup) {
		return extractRoutePoints(routeGroup).stream().map(coords -> {
			Optional<String> codigoINE = ineService.getCodigoINE(coords.getLat(), coords.getLng());
			if (codigoINE.isEmpty()) {
				return new CoordsWithWeather(coords.getLat(), coords.getLng(), new HashMap<>(), new HashMap<>());
			}

			Optional<Weather> weatherOpt = weatherService.getWeather(codigoINE.get());
			if (weatherOpt.isEmpty()) {
				return new CoordsWithWeather(coords.getLat(), coords.getLng(), new HashMap<>(), new HashMap<>());
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
//			Double temperatura = null;
//			if (dia.getTemperatura() != null && !dia.getTemperatura().isEmpty()) {
//				temperatura = dia.getTemperatura().get(0).getValue();
//			}

			return new CoordsWithWeather(coords.getLat(), coords.getLng(), mapaDescripciones, mapaTemperaturas);
		}).toList();
	}

	@Override
	public List<CoordsWithStations> getGasStationsForRoute(RouteGroup routeGroup, Long radius) {
		return extractRoutePoints(routeGroup).stream().map(
				coords -> {
					List<Gasolinera> stationsPerPoint = gasolineraService
							.getGasolinerasInRadiusCoords(coords.getLat(), coords.getLng(), radius);
					return new CoordsWithStations(coords.getLat(), coords.getLng(), stationsPerPoint);	
				})
				.toList();
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
