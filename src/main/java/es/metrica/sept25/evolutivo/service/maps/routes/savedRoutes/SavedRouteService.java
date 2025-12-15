package es.metrica.sept25.evolutivo.service.maps.routes.savedRoutes;

import java.util.List;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes.PointDTO;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes.SavedRouteDTO;
import es.metrica.sept25.evolutivo.entity.user.User;

public interface SavedRouteService {

	SavedRouteDTO getSavedRoute(Long id);

	SavedRouteDTO saveRoute(String name, List<PointDTO> puntosDTO, User user);
	
	void deleteRoute(Long id, User user);
}
