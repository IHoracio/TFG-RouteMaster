package tfg.controller.maps.routes.sharedRoutes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tfg.domain.dto.maps.routes.FullRouteData;
import tfg.service.maps.routes.sharedRoutes.SharedRouteService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class SharedRouteController {

    private static final Logger log = LoggerFactory.getLogger(SharedRouteController.class);

    private final SharedRouteService sharedRouteService;

    public SharedRouteController(SharedRouteService sharedRouteService) {
        this.sharedRouteService = sharedRouteService;
    }

    @PostMapping("/route/share")
    public ResponseEntity<Map<String, String>> generateShareLink(
            @RequestParam(required = true) String origin,
            @RequestParam(required = true) String destination,
            @RequestParam(required = false, defaultValue = "") List<String> waypoints,
            @RequestParam(required = false, defaultValue = "false") boolean optimizeWaypoints,
            @RequestParam(required = false, defaultValue = "false") boolean optimizeRoute,
            @RequestParam(required = false, defaultValue = "es") String language,
            @RequestParam(required = false, defaultValue = "false") boolean avoidTolls,
            @RequestParam(required = true, defaultValue = "1") Long gasRadius
    ) {
        log.info("Petición POST recibida en /route/share");

        String token = sharedRouteService.generateShareToken(
                origin, destination, waypoints, optimizeWaypoints, 
                optimizeRoute, language, avoidTolls, gasRadius
        );

        // Cambia esto por la URL real de tu frontend
        String url = "http://localhost:4200/shared/" + token; 
        
        return ResponseEntity.ok(Map.of("url", url));
    }

    @GetMapping("/route/shared/{token}")
    public ResponseEntity<FullRouteData> getSharedRouteData(@PathVariable String token) {
        
        log.info("Petición GET recibida en /route/shared/{}", token);

        Optional<FullRouteData> response = sharedRouteService.getSharedRouteData(token);

        if (response.isEmpty()) {
            log.warn("Devolviendo 404 Not Found para el token: {}", token);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        log.info("Devolviendo 200 OK con los datos de la ruta compartida");
        return new ResponseEntity<>(response.get(), HttpStatus.OK);
    }
}
