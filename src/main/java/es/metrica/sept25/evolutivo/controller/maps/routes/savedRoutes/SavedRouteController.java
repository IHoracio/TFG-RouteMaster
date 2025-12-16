package es.metrica.sept25.evolutivo.controller.maps.routes.savedRoutes;

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
	public ResponseEntity<SavedRouteDTO> saveRoute(@RequestParam String name, @RequestBody List<PointDTO> puntos,
			@RequestParam String email) {
		Optional<User> userOpt = userService.getByEmail(email);
		if (userOpt.isEmpty()) {
			return ResponseEntity.status(404).build();
		}

		User user = userOpt.get();

		SavedRouteDTO saved = savedRouteService.saveRoute(name, puntos, user);
		return ResponseEntity.ok(saved);
	}

	@Operation(summary = "Obtiene una ruta guardada por su ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ruta encontrada"),
			@ApiResponse(responseCode = "404", description = "Ruta no encontrada") })
	@GetMapping("/getRoute/{id}")
	public ResponseEntity<SavedRouteDTO> getSavedRoute(@PathVariable Long id) {
		try {
			SavedRouteDTO routeDTO = savedRouteService.getSavedRoute(id);
			return ResponseEntity.ok(routeDTO);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@Operation(summary = "Elimina una ruta guardada")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Ruta eliminada"),
			@ApiResponse(responseCode = "404", description = "Ruta no encontrada"),
			@ApiResponse(responseCode = "403", description = "No autorizada") })
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteRoute(@PathVariable Long id, @RequestParam String email) {
		User user = userService.getByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		savedRouteService.deleteRoute(id, user);
		return ResponseEntity.noContent().build();
	}
}
