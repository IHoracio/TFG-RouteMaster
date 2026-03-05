package tfg.service.maps.routes.executeRoutes;

import java.util.Optional;

import tfg.domain.dto.maps.routes.executionRoutes.RouteExecutionDTO;

public interface RouteExecutionService {

	public Optional<RouteExecutionDTO> executeSavedRoute(Long id);
}
