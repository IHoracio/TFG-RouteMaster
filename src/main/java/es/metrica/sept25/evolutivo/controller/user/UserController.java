package es.metrica.sept25.evolutivo.controller.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences;
import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "User", description = "Endpoints CRUD para la gestión de los usuarios")
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService service;

	// TODO: MEJORAR RESPUESTA DE ESTE ENDPOINT
	@Operation(summary = "Crear un nuevo usuario")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Usuario creado correctamente"),
			@ApiResponse(responseCode = "400", description = "Datos inválidos") 
			})
	@PostMapping("/create")
	public ResponseEntity<User> createUser(
			@Parameter(description = "Email del usuario", example = "usuario@example.com") @RequestParam(required = true) String email,

			@Parameter(description = "Contraseña del usuario", example = "password123") @RequestParam(required = true) String password,

			@Parameter(description = "Nombre del usuario", example = "Usuario") @RequestParam(required = true) String name,

			@Parameter(description = "Apellido del usuario", example = "Prueba") @RequestParam(required = true) String surname) {

		User user = service.createUser(name, surname, password, email);

		return ResponseEntity.ok(user);
	}

	@Operation(summary = "Obtener un usuario por mail")
	@ApiResponses(value = { 
		@ApiResponse(responseCode = "200", description = "Usuario encontrado"),
		@ApiResponse(responseCode = "404", description = "Usuario no encontrado") 
		})
	@GetMapping("/get")
	public ResponseEntity<User> getUser(
			@Parameter(description = "Email del usuario a buscar", example = "usuario@example.com") @RequestParam String mail) {
		return service.getByEmail(mail)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Eliminar un usuario por email")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado") 
			})
	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteUser(@RequestParam String email) {

		Optional<User> userOpt = service.getByEmail(email);

		if (userOpt.isPresent()) {
			service.deleteByEmail(email);
			return ResponseEntity.ok("Usuario eliminado correctamente");
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
	}

	@Operation(summary = "Listar todos los usuarios", 
			description = "Devuelve la lista de usuarios registrados. Si no "
						+ "hay usuarios devuelve un 204: No Content.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Lista de usuarios devuelta correctamente"), 
			@ApiResponse(responseCode = "404", description = "No se encontraron usuarios registrados") })
	@GetMapping("/all")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> usuarios = service.getAll();

		if (usuarios.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(usuarios);
	}
	
	@PutMapping("/{id}/preferences")
	public ResponseEntity<Void> updatePreferences(
			@RequestParam String email,
	        @RequestParam List<String> preferredBrands,
	        @RequestParam int radioKm,
	        @RequestParam String fuelType,
	        @RequestParam double maxPrice,
	        @RequestParam RoutePreferences.MapViewType mapView
	) {
	    Optional<User> userOpt = service.getByEmail(email);

	    if (userOpt.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }

	    service.updateRoutePreferences(
	        userOpt.get(), 
	        preferredBrands, 
	        radioKm, 
	        fuelType, 
	        maxPrice, 
	        mapView
	    );

	    return ResponseEntity.ok().build();
	}
	
	@PutMapping("/{id}/preferences/user")
	public ResponseEntity<Void> updateUserPreferences(
			@RequestParam String email,
	        @RequestParam String theme,
	        @RequestParam String language
	) {
	    Optional<User> userOpt = service.getByEmail(email);
	    if (userOpt.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }

	    service.updateUserPreferences(
	        userOpt.get(),
	        theme,
	        language
	    );

	    return ResponseEntity.ok().build();
	}
}
