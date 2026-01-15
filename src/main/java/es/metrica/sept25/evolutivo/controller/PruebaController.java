package es.metrica.sept25.evolutivo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.service.session.CookieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class PruebaController {

    @Autowired
    private CookieService cookieService;

    @PostMapping("/api/login")
    @Operation(
    		summary = "Temporary test login", 
    		description = "Creates a session cookie for testing"
	)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Created cookie correctly", content = @Content),
        @ApiResponse(responseCode = "403", description = "Cookie already exists", content = @Content)
    })
    public ResponseEntity<String> login(
    		HttpServletRequest request, 
    		HttpServletResponse response, 
    		String email
    		) {
        if (cookieService.getCookieValue(request, "sesionActiva").isEmpty()) {
            cookieService.createSessionCookie(response, email);
            return ResponseEntity.status(HttpStatus.OK).body("{ body: \"Created cookie correctly\" }");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");
    }

    @PostMapping("/api/logout")
    public String logout() {
        return "";
    }

    @GetMapping("/login")
    @ResponseBody
    public String login() {
        return "Got to login, now what?";
    }
}
