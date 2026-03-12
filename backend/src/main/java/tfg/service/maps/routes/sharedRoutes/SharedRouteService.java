package tfg.service.maps.routes.sharedRoutes;

import java.util.List;
import java.util.Optional;

import tfg.domain.dto.maps.routes.FullRouteData;

public interface SharedRouteService {
    
    String generateShareToken(String origin, String destination, List<String> waypoints, 
                              boolean optimizeWaypoints, boolean optimizeRoute, 
                              String language, boolean avoidTolls, Long gasRadius);

    Optional<FullRouteData> getSharedRouteData(String token);
}
