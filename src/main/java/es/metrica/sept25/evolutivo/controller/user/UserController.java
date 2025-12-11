package es.metrica.sept25.evolutivo.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "User")
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @Operation(summary = "Crear un nuevo usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/create")
    public ResponseEntity<User> createUser(
            @Parameter(description = "Email del usuario", example = "usuario@example.com") 
            @RequestParam(required = true) String email,

            @Parameter(description = "Contraseña del usuario", example = "password123") 
            @RequestParam(required = true) String password,

            @Parameter(description = "Nombre del usuario", example = "Usuario") 
            @RequestParam(required = true) String name,

            @Parameter(description = "Apellido del usuario", example = "Prueba") 
            @RequestParam(required = true) String surname) {

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setSurname(surname);

        User saved = service.save(user);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Obtener un usuario por mail")
    @ApiResponses({
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

    @Operation(summary = "Eliminar un usuario por mail")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "Email del usuario a eliminar", example = "usuario@example.com") @RequestParam String mail) {
        service.deleteByEmail(mail);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar todos los usuarios")
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(service.getAll());
    }
}
