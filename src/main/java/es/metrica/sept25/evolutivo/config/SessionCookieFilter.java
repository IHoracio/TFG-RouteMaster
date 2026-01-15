package es.metrica.sept25.evolutivo.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import es.metrica.sept25.evolutivo.service.session.CookieService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SessionCookieFilter extends OncePerRequestFilter {

    private final CookieService cookieService;

    public SessionCookieFilter(CookieService cookieService) {
        this.cookieService = cookieService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Only protect API endpoints, skip login/logout and static resources
        if (path.startsWith("/api/")
//                && !path.equals("/api/register")
//                && !path.equals("/api/login")
//                && !path.equals("/api/logout")
                ) {

            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                response.setStatus(HttpServletResponse.SC_FOUND);
                response.setHeader("Location", "/login");
                return;
            }

            Optional<Cookie> sessionCookie = Arrays.stream(cookies)
                    .filter(c -> "sesionActiva".equals(c.getName()))
                    .findFirst();

            if (sessionCookie.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_FOUND);
                response.setHeader("Location", "/login");
                return;
            }

            String rawValue = sessionCookie.get().getValue();
            if (rawValue == null || rawValue.isEmpty() || !cookieService.validate(rawValue)) {
                response.setStatus(HttpServletResponse.SC_FOUND);
                response.setHeader("Location", "/login");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
