package tfg.service.maps.routes.savedRoutes;

import java.util.List;
import java.util.Optional;

import tfg.domain.dto.maps.routes.FullRouteData;
import tfg.domain.dto.maps.routes.savedRoutes.SavedRouteDTO;
import tfg.domain.dto.maps.routes.savedRoutes.SavedRouteRequest;
import tfg.entity.user.User;

public interface SavedRouteService {

    Optional<SavedRouteDTO> getSavedRoute(String routeId);

    void deleteRoute(String routeId, User user);

    Optional<List<SavedRouteDTO>> getAllSavedRoutes(String email);

    SavedRouteDTO saveRoute(SavedRouteRequest request, User user);
    
    SavedRouteDTO renameRoute(String name, SavedRouteDTO savedRoute);

    Optional<FullRouteData> executeRoute(String routeId);
}