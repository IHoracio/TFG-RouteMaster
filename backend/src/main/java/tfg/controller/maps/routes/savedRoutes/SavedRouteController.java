package tfg.controller.maps.routes.savedRoutes;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import tfg.domain.dto.maps.routes.FullRouteData;
import tfg.domain.dto.maps.routes.savedRoutes.SavedRouteDTO;
import tfg.domain.dto.maps.routes.savedRoutes.SavedRouteRequest;
import tfg.entity.user.User;
import tfg.service.maps.routes.savedRoutes.SavedRouteService;
import tfg.service.session.CookieService;
import tfg.service.user.UserService;

@RestController
@Tag(
        name = "Rutas guardadas", 
        description = "Endpoints que hacen CRUD sobre las rutas que se guardan asociadas a un usuario."
)
@RequestMapping("/api/savedRoute")
public class SavedRouteController {

    private static final Logger log = LoggerFactory.getLogger(SavedRouteController.class);

    @Autowired
    private SavedRouteService savedRouteService;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Guarda una ruta calculada")
    @ApiResponses(value = { 
            @ApiResponse(responseCode = "200", description = "Ruta guardada correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado") 
    })
    @PostMapping("/save")
    public ResponseEntity<SavedRouteDTO> saveRoute(
            HttpServletRequest request,
            @RequestBody SavedRouteRequest saveRequest) { 

        log.info("Petición POST recibida en /api/savedRoute/save para guardar una nueva ruta.");

        String email = cookieService.getCookieValue(request, "sesionActiva").orElse("");
        Optional<User> userOpt = userService.getEntityByEmail(email);

        if (userOpt.isEmpty()) {
            log.warn("Intento de guardar ruta fallido. No se encontró sesión o usuario activo.");
            return ResponseEntity.status(404).build();
        }

        User user = userOpt.get();
        SavedRouteDTO saved = savedRouteService.saveRoute(saveRequest, user);
        
        log.info("Ruta '{}' guardada exitosamente para el usuario: {}", saved.getName(), email);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Ejecuta una ruta guardada", description = "Devuelve el trazado y calcula el clima/gasolina en tiempo real asegurando que pertenece al usuario.")
    @ApiResponses(value = { 
            @ApiResponse(responseCode = "200", description = "Ruta ejecutada con éxito"),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado (no logueado)"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (la ruta no pertenece al usuario)"),
            @ApiResponse(responseCode = "404", description = "Ruta no encontrada") 
    })
    @GetMapping("/execute/{routeId}")
    public ResponseEntity<FullRouteData> executeSavedRoute(
            @PathVariable String routeId,
            HttpServletRequest request) {

        log.info("Petición GET recibida en /api/savedRoute/execute/{} para ejecutar ruta.", routeId);

        String email = cookieService.getCookieValue(request, "sesionActiva").orElse("");
        Optional<User> userOpt = userService.getEntityByEmail(email);

        if (userOpt.isEmpty()) {
            log.warn("Petición denegada para ejecutar ruta {}: Usuario no autorizado.", routeId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<List<SavedRouteDTO>> userRoutes = savedRouteService.getAllSavedRoutes(email);
        
        boolean isOwner = userRoutes.isPresent() && userRoutes.get().stream()
                .anyMatch(route -> route.getRouteId().equals(routeId));

        if (!isOwner) {
            log.warn("Alerta de Seguridad: El usuario {} intentó ejecutar la ruta {} que no le pertenece.", email, routeId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<FullRouteData> response = savedRouteService.executeRoute(routeId);

        if (response.isEmpty()) {
            log.warn("La ruta {} no se pudo recuperar de la base de datos.", routeId);
            return ResponseEntity.notFound().build();
        }

        log.info("Ruta {} ejecutada y devuelta con éxito al usuario {}.", routeId, email);
        return ResponseEntity.ok(response.get());
    }

    @Operation(summary = "Obtiene una ruta guardada por su ID")
    @ApiResponses(value = { 
            @ApiResponse(responseCode = "200", description = "Ruta encontrada"),
            @ApiResponse(responseCode = "404", description = "Ruta no encontrada") 
    })
    @GetMapping("/get/{routeId}")
    public ResponseEntity<SavedRouteDTO> getSavedRoute(@PathVariable String routeId) { 
        log.info("Petición GET recibida en /api/savedRoute/get/{} para obtener información de ruta.", routeId);
        
        Optional<SavedRouteDTO> routeDTO = savedRouteService.getSavedRoute(routeId);

        if (routeDTO.isEmpty()) {
            log.warn("No se encontró la ruta con ID: {}", routeId);
            return ResponseEntity.notFound().build();
        }

        log.info("Información de la ruta {} obtenida exitosamente.", routeId);
        return ResponseEntity.ok(routeDTO.get());
    }

    @Operation(summary = "Elimina una ruta guardada")
    @ApiResponses(value = { 
            @ApiResponse(responseCode = "204", description = "Ruta eliminada con éxito"),
            @ApiResponse(responseCode = "404", description = "Ruta no encontrada.") 
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable String id, HttpServletRequest request) { 

        log.info("Petición DELETE recibida en /api/savedRoute/delete/{} para borrar ruta.", id);

        String email = cookieService.getCookieValue(request, "sesionActiva").orElse("");
        Optional<User> user = userService.getEntityByEmail(email);

        if (user.isEmpty()) {
            log.warn("No se pudo eliminar la ruta {}: Usuario no autorizado o sesión inválida.", id);
            return ResponseEntity.notFound().build();
        }

        savedRouteService.deleteRoute(id, user.get());
        log.info("Proceso de eliminación finalizado para la ruta {} (solicitado por {}).", id, email);
        
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtiene todas las rutas guardadas")
    @ApiResponses(value = { 
            @ApiResponse(responseCode = "200", description = "Ruta encontrada"),
            @ApiResponse(responseCode = "404", description = "Ruta no encontrada") 
    })
    @GetMapping
    public ResponseEntity<List<SavedRouteDTO>> getAllSavedRoutes(HttpServletRequest request) {

        log.info("Petición GET recibida en /api/savedRoute para listar todas las rutas del usuario.");

        String email = cookieService.getCookieValue(request, "sesionActiva").orElse("");
        Optional<List<SavedRouteDTO>> routes = savedRouteService.getAllSavedRoutes(email);

        if (routes.isPresent()) {
            log.info("Se han devuelto {} rutas guardadas para el usuario {}.", routes.get().size(), email);
            return ResponseEntity.ok(routes.get());
        }

        log.warn("No se encontraron rutas para el usuario o la sesión de {} es inválida.", email);
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Cambia de nombre la ruta dada (por ID) al nuevo nombre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Renombrada con éxito"),
            @ApiResponse(responseCode = "404", description = "Ruta no encontrada o nombre duplicado")
    })
    @PostMapping("/rename")
    public ResponseEntity<SavedRouteDTO> renameSavedRoute(
            @RequestParam String newName, 
            @RequestParam String routeId,
            HttpServletRequest request
            ) {

        log.info("Petición POST recibida en /api/savedRoute/rename para cambiar el nombre de la ruta {} a '{}'.", routeId, newName);

        String email = cookieService.getCookieValue(request, "sesionActiva").orElse("");
        Optional<List<SavedRouteDTO>> routes = savedRouteService.getAllSavedRoutes(email);

        if (routes.isEmpty() || routes.get().isEmpty()) {
            log.warn("No se puede renombrar: El usuario {} no tiene rutas o no existe.", email);
            return ResponseEntity.notFound().build();
        }

        List<SavedRouteDTO> routesList = routes.get();

        long routesWithSameName = routesList.stream()
                .filter(sRoute -> sRoute.getName().equals(newName))
                .count();

        Optional<SavedRouteDTO> savedRouteOpt = routesList.stream()
                .filter(sRoute -> sRoute.getRouteId().equals(routeId))
                .findFirst();

        if (routesWithSameName > 0 || savedRouteOpt.isEmpty()) {
            log.warn("No se puede renombrar la ruta {}. Ya existe una ruta con el nombre '{}' o la ruta no pertenece al usuario.", routeId, newName);
            return ResponseEntity.notFound().build();
        }

        SavedRouteDTO renamedRoute = savedRouteService.renameRoute(newName, savedRouteOpt.get());
        log.info("Ruta {} renombrada exitosamente a '{}' para el usuario {}.", routeId, newName, email);
        
        return ResponseEntity.ok(renamedRoute);
    }
}