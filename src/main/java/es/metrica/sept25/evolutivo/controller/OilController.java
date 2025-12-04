package es.metrica.sept25.evolutivo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.entity.gasolinera.Provincia;
import es.metrica.sept25.evolutivo.service.ProvinciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class OilController {

	@Autowired
	private ProvinciaService provinciaService;

	@Operation(
			summary = "Devuelve una lista de las provincias españolas",
			description = "")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Provincias recuperadas"), 
			@ApiResponse(responseCode = "204", description = "No se encontraron provincias") })
	@GetMapping("/provincias")
	public ResponseEntity<List<Provincia>> getProvincias() {
		List<Provincia> list = provinciaService.getProvincias();

		if (list.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
			
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}
