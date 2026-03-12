package tfg.service.maps.routes.sharedRoutes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tfg.domain.dto.maps.routes.FullRouteData;
import tfg.entity.maps.routes.SharedRoute;
import tfg.repository.SharedRouteRepository;
import tfg.service.maps.routes.RoutesService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SharedRouteServiceImpl implements SharedRouteService {

    private static final Logger log = LoggerFactory.getLogger(SharedRouteServiceImpl.class);

    private final SharedRouteRepository sharedRouteRepository;
    private final RoutesService routesService;

    public SharedRouteServiceImpl(SharedRouteRepository sharedRouteRepository, RoutesService routesService) {
        this.sharedRouteRepository = sharedRouteRepository;
        this.routesService = routesService;
    }

    @Override
    public String generateShareToken(String origin, String destination, List<String> waypoints,
                                     boolean optimizeWaypoints, boolean optimizeRoute, 
                                     String language, boolean avoidTolls, Long gasRadius) {
        
        log.info("Generando nuevo token para compartir ruta desde '{}' hasta '{}'", origin, destination);

        String token = UUID.randomUUID().toString();

        SharedRoute sharedRoute = new SharedRoute(token, origin, destination, waypoints, 
                                                  optimizeWaypoints, optimizeRoute, 
                                                  language, avoidTolls, gasRadius);
        
        sharedRouteRepository.save(sharedRoute);
        log.debug("Ruta compartida guardada en BD con token: {}", token);

        return token;
    }

    @Override
    public Optional<FullRouteData> getSharedRouteData(String token) {
        
        log.info("Intentando recuperar datos de la ruta con token: {}", token);

        Optional<SharedRoute> savedRouteOpt = sharedRouteRepository.findById(token);

        if (savedRouteOpt.isEmpty()) {
            log.warn("No se encontró ninguna ruta compartida con el token: {}", token);
            return Optional.empty();
        }

        SharedRoute params = savedRouteOpt.get();
        log.debug("Parámetros recuperados con éxito. Llamando a la API de Google/servicios...");

        // Llamamos a tu servicio original con los parámetros recuperados
        return routesService.getFullRouteData(
                params.getOrigin(),
                params.getDestination(),
                params.getWaypoints(),
                params.isOptimizeWaypoints(),
                params.isOptimizeRoute(),
                params.getLanguage(),
                params.isAvoidTolls(),
                params.getGasRadius()
        );
    }
}
