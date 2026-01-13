package es.metrica.sept25.evolutivo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//	@Autowired
//	private PasswordEncoder passwordEncoder;

	@Bean
	SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(a -> 
			a.requestMatchers("/", "/login", "/logout", "/authenticate").permitAll()
			.requestMatchers("/swagger-ui/**", "/v3/api-docs*/**").permitAll()
			.requestMatchers("/api/**").authenticated())
			
		.sessionManagement(s -> 
			s.invalidSessionUrl("/login?expired")
			.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
			.maximumSessions(1)
			.maxSessionsPreventsLogin(true))
		
		.formLogin(Customizer.withDefaults())
		.logout(logout -> 
			logout.deleteCookies("EVOL_SESSION")
			.logoutUrl("/logout")
			.logoutSuccessHandler(
					((request, response, authentication) -> {
						String redirectUrl = request.getHeader("Referer");
						response.sendRedirect(redirectUrl == null ? "/" : redirectUrl);
					})
			)
		);
		return http.build();
	}
}
