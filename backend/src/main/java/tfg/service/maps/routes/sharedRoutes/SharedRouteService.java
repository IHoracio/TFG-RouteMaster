package tfg.service.maps.routes.sharedRoutes;

import java.util.Optional;

import tfg.domain.dto.maps.routes.FullRouteData;
import tfg.domain.dto.maps.routes.sharedRoutes.ShareRouteRequest;

public interface SharedRouteService {
    
    String generateShareToken(ShareRouteRequest request);

    Optional<FullRouteData> getSharedRouteData(String token);
}
