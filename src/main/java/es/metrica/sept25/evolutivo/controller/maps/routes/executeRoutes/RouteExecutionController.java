package es.metrica.sept25.evolutivo.controller.maps.routes.executeRoutes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.executionRoutes.RouteExecutionDTO;
import es.metrica.sept25.evolutivo.service.maps.routes.executeRoutes.RouteExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Ejecutar Ruta")
public class RouteExecutionController {

    @Autowired
    private RouteExecutionService routeExecutionService;

    @Operation(summary = "Ejecuta una ruta guardada por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ruta ejecutada correctamente"),
        @ApiResponse(responseCode = "404", description = "Ruta no encontrada")
    })
    @GetMapping("/executeSavedRoute")
    public ResponseEntity<RouteExecutionDTO> executeSavedRoute(
            @RequestParam Long id) {

        try {
            RouteExecutionDTO dto = routeExecutionService.executeSavedRoute(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
