package es.metrica.sept25.evolutivo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
@OpenAPIDefinition(
	    info = @Info(
	            title = "Documentación de endpoints",
	            version = "1.0",
	            description = "Documentación con OpenAPI para el proyecto evolutivo",
	            contact = @Contact(name = "Proyecto evolutivo")
	    ),
	    servers = {
	        @Server(description = "Ejecución local", url = "http://localhost:8080")
	    }
	)
	@SecurityScheme(
	    name = "aemetApiKey",
	    description = "API Key necesaria para AEMET",
	    type = SecuritySchemeType.APIKEY,
	    in = SecuritySchemeIn.HEADER,
	    paramName = "api_key"
	)
	public class OpenApiConfig {}

