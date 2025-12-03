package es.metrica.sept25.evolutivo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(contact = @Contact(name = "Proyecto evolutivo"), description = "Documentación con OpenAPI para el proyecto evolutivo", title = "Documentación de endpoints", version = "1.0"), servers = {
		@Server(description = "Ejecución local", url = "http://localhost:8080"), })
@SecurityScheme(name = "Seguridad de AEMET", description = "Autentificación con API Key necesaria", scheme = "API_KEY", type = SecuritySchemeType.APIKEY, bearerFormat = "String", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {
}
