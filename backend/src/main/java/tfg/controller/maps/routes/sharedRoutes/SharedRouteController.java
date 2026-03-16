package tfg.controller.maps.routes.sharedRoutes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import tfg.domain.dto.maps.routes.FullRouteData;
import tfg.domain.dto.maps.routes.sharedRoutes.ShareRouteRequest;
import tfg.service.maps.routes.sharedRoutes.SharedRouteService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Tag(name = "Rutas Compartidas", description = "Endpoints para generar y consumir enlaces de rutas compartidas.")
public class SharedRouteController {

    private static final Logger log = LoggerFactory.getLogger(SharedRouteController.class);

    private final SharedRouteService sharedRouteService;
    
    @Value("${app.frontend.url}")
    private String frontendUrl;

    public SharedRouteController(SharedRouteService sharedRouteService) {
        this.sharedRouteService = sharedRouteService;
    }

    @Operation(
        summary = "Genera un token/enlace para compartir una ruta",
        description = "Guarda las coordenadas exactas de la ruta y genera un UUID único para compartirla de forma estática, calculando datos dinámicos al vuelo."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Enlace generado exitosamente."),
        @ApiResponse(responseCode = "500", description = "Error interno al procesar las coordenadas.")
    })
    @PostMapping("/route/share")
    public ResponseEntity<Map<String, String>> generateShareLink(
            @RequestBody ShareRouteRequest request 
    ) {
        log.info("Petición POST recibida en /route/share");

        String token = sharedRouteService.generateShareToken(request);

        String url = frontendUrl + "/shared/" + token; 
        
        return ResponseEntity.ok(Map.of("url", url));
    }

    @Operation(
        summary = "Obtiene los datos de una ruta compartida",
        description = "Recupera las coordenadas guardadas usando el token y calcula las gasolineras y el clima en tiempo real."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datos de ruta obtenidos exitosamente."),
        @ApiResponse(responseCode = "404", description = "Token no encontrado o caducado.")
    })
    @GetMapping("/route/shared/{token}")
    public ResponseEntity<FullRouteData> getSharedRouteData(
            @Parameter(description = "El UUID de la ruta compartida", required = true)
            @PathVariable String token
    ) {
        
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