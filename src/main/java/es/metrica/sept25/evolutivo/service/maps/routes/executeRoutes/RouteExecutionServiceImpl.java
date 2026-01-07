package es.metrica.sept25.evolutivo.service.maps.routes.executeRoutes;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Leg;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.RouteGroup;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.executionRoutes.RouteExecutionDTO;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes.PointDTO;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes.SavedRouteDTO;
import es.metrica.sept25.evolutivo.service.maps.routes.RoutesService;
import es.metrica.sept25.evolutivo.service.maps.routes.savedRoutes.SavedRouteService;

@Service
public class RouteExecutionServiceImpl implements RouteExecutionService{

	@Autowired
	private SavedRouteService savedRouteService;

	@Autowired
	private RoutesService routesService;


	@Override
	@Cacheable("execSavedRoute")
	public Optional<RouteExecutionDTO> executeSavedRoute(Long id) {
		Optional<SavedRouteDTO> savedRouteOpt = savedRouteService.getSavedRoute(id);

		if (savedRouteOpt.isEmpty()) {
			return Optional.empty();
		}

		SavedRouteDTO savedRoute = savedRouteOpt.get();

		if (savedRoute.getPoints().isEmpty()) {
			return Optional.empty();
		}

		PointDTO inicio = savedRoute.getPoints().get(0);
		PointDTO fin = savedRoute.getPoints().get(savedRoute.getPoints().size() - 1);
		List<String> intermedios = savedRoute.getPoints().subList(1, savedRoute.getPoints().size() - 1)
				.stream()
				.map(PointDTO::getAddress)
				.toList();

		Optional<RouteGroup> routeGroupOpt = routesService.getDirections(
				inicio.getAddress(),
				fin.getAddress(),
				intermedios,
				false,
				false,
				"es"
				);

		if (routeGroupOpt.isEmpty()) {
			return Optional.empty();
		}

		RouteGroup routeGroup = routeGroupOpt.get();
		Leg leg = routeGroup.getRoutes().get(0).getLegs().get(0);

		RouteExecutionDTO dto = new RouteExecutionDTO();
		dto.setDistanceMeters(leg.getDistance().getValue());
		dto.setDurationSeconds(leg.getDuration().getValue());

		List<String> polylines = leg.getSteps().stream()
				.map(step -> step.getPolyline().getPoints())
				.toList();

		dto.setPolylines(polylines);

		return Optional.of(dto);
	}
}
