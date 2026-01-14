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
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        Optional<User> created = userService.createUser(userDTO);
        if (created.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body("User created");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not create user");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDto login, HttpServletResponse response) {
        if (login == null || login.getUser() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing credentials");
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
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        cookieService.closeSession(response, "sesionActiva");
        return ResponseEntity.ok("Logged out");
    }
}
