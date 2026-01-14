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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.gasolineras.UserSavedGasStationDto;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.PreferredBrandsDTO;
import es.metrica.sept25.evolutivo.domain.dto.user.UserDTO;
import es.metrica.sept25.evolutivo.domain.dto.user.UserResponseDTO;
import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.entity.gasolinera.UserSavedGasStation;
import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences;
import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences.Language;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences.Theme;
import es.metrica.sept25.evolutivo.enums.EmissionType;
import es.metrica.sept25.evolutivo.enums.FuelType;
import es.metrica.sept25.evolutivo.enums.MapViewType;
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
			/*@Parameter(description = "Email del usuario", example = "usuario@example.com") 
			@RequestParam(required = true) String email,

			@Parameter(description = "Contraseña del usuario", example = "password123") 
			@RequestParam(required = true) String password,

			@Parameter(description = "Nombre del usuario", example = "Usuario") 
			@RequestParam(required = true) String name,

			@Parameter(description = "Apellido del usuario", example = "Prueba") 
			@RequestParam(required = true) String surname*/ @RequestBody(required = true) UserDTO userDTO) {

//		System.err.printf("[%s] [%s] [%s] [%s]\n", email, password, name, surname);
//		Optional<User> user = service.createUser(name, surname, password, email);
		System.err.println(userDTO.toString());
		Optional<User> user = service.createUser(userDTO);
		if (user.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		System.err.println(user.toString());
		return ResponseEntity.ok(user.get());
	}

	@Operation(summary = "Obtener un usuario por mail")
	@ApiResponses(value = { 
		@ApiResponse(responseCode = "200", description = "Usuario encontrado"),
		@ApiResponse(responseCode = "404", description = "Usuario no encontrado") 
	})
	@GetMapping("/get")
	public ResponseEntity<UserResponseDTO> getUser(
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

		if (service.getByEmail(email).isPresent()) {
			service.deleteByEmail(email);
			return ResponseEntity.ok("Usuario eliminado correctamente");
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
	}

	@Operation(
		summary = "Listar todos los usuarios", 
		description = "Devuelve la lista de usuarios registrados. Si no "
					+ "hay usuarios devuelve un 204: No Content."
	)
	@ApiResponses(value = { 
		@ApiResponse(responseCode = "200", description = "Lista de usuarios devuelta correctamente"), 
		@ApiResponse(responseCode = "404", description = "No se encontraron usuarios registrados") 
	})
	@GetMapping("/all")
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
		List<UserResponseDTO> usuarios = service.getAll();

		if (usuarios.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(usuarios);
	}
	
	@PutMapping("/{id}/preferences")
	public ResponseEntity<Void> updatePreferences(
			@RequestParam String email,
			@RequestBody PreferredBrandsDTO brandsDto,
	        @RequestParam int radioKm,
	        @RequestParam FuelType fuelType,
	        @RequestParam double maxPrice,
	        @RequestParam MapViewType mapView,
	        @RequestParam boolean avoidTolls,
		    @RequestParam EmissionType vehicleEmissionType
	) {
	    Optional<User> userOpt = service.getEntityByEmail(email);

	    if (userOpt.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }
	    service.updateRoutePreferences(
	        userOpt.get(), 
	        brandsDto.preferredBrands, 
	        radioKm, 
	        fuelType, 
	        maxPrice, 
	        mapView,
	        avoidTolls,
	        vehicleEmissionType
	    );

	    return ResponseEntity.ok().build();
	}
	
	@PutMapping("/{id}/preferences/user")
	public ResponseEntity<Void> updateUserPreferences(
			@RequestParam String email,
	        @RequestParam Theme theme,
	        @RequestParam Language language
	) {
	    Optional<User> userOpt = service.getEntityByEmail(email);	
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
	
	@Operation(summary = "Obtener las preferencias por defecto de las rutas")
	@ApiResponses(value = { 
	    @ApiResponse(responseCode = "200", description = "Preferencias devueltas correctamente"),
	    @ApiResponse(responseCode = "404", description = "Preferencias no encontradas")
	})
	@GetMapping("/defaultPreferences")
	public ResponseEntity<RoutePreferences> getDefaultPreferences(){
		
		Optional<RoutePreferences> rpOpt = service.getDefaultPreferences();
		
		if(rpOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(rpOpt.get());
		 
		 
		 
	}
	
	@Operation(summary = "Obtener las preferencias de rutas de un usuario")
	@ApiResponses(value = { 
	    @ApiResponse(responseCode = "200", description = "Preferencias devueltas correctamente"),
	    @ApiResponse(responseCode = "404", description = "Usuario no encontrado") 
	})
	@GetMapping("/{id}/preferences")
	public ResponseEntity<RoutePreferences> getRoutePreferences(@RequestParam String email) {
	    Optional<User> userOpt = service.getEntityByEmail(email);

	    if (userOpt.isEmpty() ) {
	        return ResponseEntity.notFound().build();
	    }

	    return ResponseEntity.ok(userOpt.get().getRoutePreferences());
	}
	
	@Operation(summary = "Obtener las preferencias de usuario")
	@ApiResponses(value = { 
	    @ApiResponse(responseCode = "200", description = "Preferencias devueltas correctamente"),
	    @ApiResponse(responseCode = "404", description = "Usuario no encontrado") 
	})
	@GetMapping("/{id}/preferences/user")
	public ResponseEntity<UserPreferences> getUserPreferences(@RequestParam String email) {
	    Optional<User> userOpt = service.getEntityByEmail(email);

	    if (userOpt.isEmpty() || userOpt.get().getUserPreferences() == null) {
	        return ResponseEntity.notFound().build();
	    }
	    
	    return ResponseEntity.ok(userOpt.get().getUserPreferences());
	}
	
	@Operation(
		    summary = "Obtener marcas de gasolineras preferidas",
		    description = "Devuelve la lista de marcas de gasolineras configuradas como preferidas por el usuario"
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200",description = "Listado de marcas preferidas"),
		    @ApiResponse(responseCode = "404",description = "Usuario no encontrado")
		})
	@GetMapping("/{id}/preferredBrands/user")
	public ResponseEntity<List<String>> getPreferredBrands(@RequestParam String email) {
	
		Optional<User> userOpt = service.getEntityByEmail(email);

	    if (userOpt.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }

	    List<String> brands = userOpt.get().getRoutePreferences().getPreferredBrands();

	    return ResponseEntity.ok(brands);
	
	}
	
	@Operation(
		    summary = "Añadir gasolinera a favoritos",
		    description = "Añade una gasolinera a la lista de gasolineras favoritas del usuario"
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200",description = "Gasolinera añadida correctamente a favoritos"),
		    @ApiResponse(responseCode = "404",description = "Usuario o gasolinera no encontrada")
		})
	@PutMapping("/favourites/{idEstacion}")
	public ResponseEntity<Void> saveGasStation(
	        @RequestParam String email,
	        @RequestParam String alias,
	        @PathVariable Long idEstacion) {

		service.saveGasStation(email,alias, idEstacion);
	    return ResponseEntity.ok().build();
	}

	@Operation(
		    summary = "Eliminar gasolinera de favoritos",
		    description = "Elimina una gasolinera de la lista de gasolineras favoritas del usuario"
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "204",description = "Gasolinera eliminada correctamente de favoritos"),
		    @ApiResponse(responseCode = "404",description = "Usuario o gasolinera no encontrada")
		})
	@DeleteMapping("/favourites/{idEstacion}")
	public ResponseEntity<Void> removeGasStation(
	        @RequestParam String email,
	        @RequestParam String alias
	        ) {

		service.removeGasStation(email, alias);
	    return ResponseEntity.noContent().build();
	}
	
	@Operation(
		    summary = "Obtener gasolineras favoritas del usuario",
		    description = "Devuelve el listado de gasolineras marcadas como favoritas por el usuario"
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Listado de gasolineras favoritas"),
		    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
		})
	@GetMapping("/favourites")
	public ResponseEntity<List<UserSavedGasStationDto>> getSavedGasStations(
	        @RequestParam String email) {

	    return ResponseEntity.ok(
	            service.getSavedGasStations(email)
	    );
	}
	
	
}
