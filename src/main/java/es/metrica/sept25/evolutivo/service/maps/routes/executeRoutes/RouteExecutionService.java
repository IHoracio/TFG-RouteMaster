package es.metrica.sept25.evolutivo.service.maps.routes.executeRoutes;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.executionRoutes.RouteExecutionDTO;

public interface RouteExecutionService {

	public RouteExecutionDTO executeSavedRoute(Long id);
}
