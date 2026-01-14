package es.metrica.sept25.evolutivo.controller.auth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.auth.UserLoginDto;
import es.metrica.sept25.evolutivo.domain.dto.user.UserDTO;
import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.service.session.CookieService;
import es.metrica.sept25.evolutivo.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final CookieService cookieService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, CookieService cookieService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.cookieService = cookieService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    @Operation(
    		summary = "Register a new user", 
    		description = "Creates a new user account"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or user exists", content = @Content)
    })
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        Optional<User> created = userService.createUser(userDTO);
        if (created.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body("User created");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not create user");
    }

    @PostMapping("/login")
    @Operation(
    		summary = "Login user", 
    		description = "Validates credentials and creates a session cookie"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logged in", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "204", description = "Already logged in", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "Missing credentials", content = @Content),
        @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    public ResponseEntity<String> login(
    		@RequestBody UserLoginDto login, 
    		HttpServletRequest request, 
    		HttpServletResponse response) {
    	
        if (login == null || login.getUser() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing credentials");
        }
        
        Optional<String> getCookie = cookieService.getCookieValue(request, "sesionActiva");
        if (getCookie.isPresent()) {
        	String clean = getCookie.get().replaceAll("\\[|\\]", "");
        	if (login.getUser().equals(clean)) {
        		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Already logged in");
        	}
        }

        Optional<User> userOpt = userService.getEntityByEmail(login.getUser());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        cookieService.createSessionCookie(response, user.getEmail());
        return ResponseEntity.ok("Logged in");
    }

    @PostMapping("/logout")
    @Operation(
    		summary = "Logout user", 
    		description = "Closes the active session if present"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logged out", content = @Content),
        @ApiResponse(responseCode = "204", description = "No active session", content = @Content)
    })
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        boolean hasSession = cookieService.getCookieValue(request, "sesionActiva").isPresent();
        
        if (!hasSession) {
            // Do nothing if cookie not present
            return ResponseEntity.noContent().build();
        }
        
        cookieService.closeSession(response, "sesionActiva");
        return ResponseEntity.ok("Logged out");
    }
}
