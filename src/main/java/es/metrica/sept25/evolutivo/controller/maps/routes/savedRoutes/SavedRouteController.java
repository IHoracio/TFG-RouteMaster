package es.metrica.sept25.evolutivo.controller.maps.routes.savedRoutes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes.PointDTO;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes.SavedRouteDTO;
import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.service.maps.routes.savedRoutes.SavedRouteService;
import es.metrica.sept25.evolutivo.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Ruta", description = "Endpoints que guardan, recuperan, ejecutan y borran una ruta")
@RequestMapping("/api/ruta")
public class SavedRouteController {

	@Autowired
	private SavedRouteService savedRouteService;

	@Autowired
	private UserService userService;

	@Operation(summary = "Guarda una ruta calculada")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ruta guardada correctamente"),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado") })
	@PostMapping("/save")
	public ResponseEntity<SavedRouteDTO> saveRoute(@RequestParam String name,
			@RequestParam(required = true) String origin, @RequestParam(required = true) String destination,
			@RequestParam(required = false, defaultValue = "") List<String> waypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeWaypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeRoute,
			@RequestParam(required = false, defaultValue = "es") String language, @RequestParam String email) {
		Optional<User> userOpt = userService.getByEmail(email);

		if (userOpt.isEmpty()) {
			return ResponseEntity.status(404).build();
		}

		User user = userOpt.get();

		List<PointDTO> puntos = new ArrayList<>();

		PointDTO originPoint = new PointDTO();
		originPoint.setAddress(origin);
		originPoint.setType("ORIGIN");
		puntos.add(originPoint);

		if (waypoints != null) {
			for (String wp : waypoints) {
				PointDTO wpPoint = new PointDTO();
				wpPoint.setAddress(wp);
				wpPoint.setType("WAYPOINT");
				puntos.add(wpPoint);
			}
		}

		PointDTO destinationPoint = new PointDTO();
		destinationPoint.setAddress(destination);
		destinationPoint.setType("DESTINATION");
		puntos.add(destinationPoint);

		SavedRouteDTO saved = savedRouteService.saveRoute(name, puntos, user, optimizeWaypoints, optimizeRoute,
				language);
		return ResponseEntity.ok(saved);
	}

	@Operation(summary = "Obtiene una ruta guardada por su ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ruta encontrada"),
			@ApiResponse(responseCode = "404", description = "Ruta no encontrada") })
	@GetMapping("/get/{id}")
	public ResponseEntity<SavedRouteDTO> getSavedRoute(@PathVariable Long id) {
		Optional<SavedRouteDTO> routeDTO = savedRouteService.getSavedRoute(id);

		if (routeDTO.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(routeDTO.get());
	}

	@Operation(summary = "Elimina una ruta guardada")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Ruta eliminada con éxito"),
			@ApiResponse(responseCode = "404", description = "Ruta no encontrada."),
			@ApiResponse(responseCode = "403", description = "No autorizada") })
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteRoute(@PathVariable Long id, @RequestParam String email) {

		Optional<User> user = userService.getByEmail(email);

		if (user.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		savedRouteService.deleteRoute(id, user.get());
		return ResponseEntity.noContent().build();
	}
}
