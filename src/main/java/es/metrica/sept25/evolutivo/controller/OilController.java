package es.metrica.sept25.evolutivo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;
import es.metrica.sept25.evolutivo.entity.gasolinera.Provincia;
import es.metrica.sept25.evolutivo.service.gasolineras.MunicipioService;
import es.metrica.sept25.evolutivo.service.gasolineras.ProvinciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Gasolinera")
public class OilController {

	@Autowired
	private ProvinciaService provinciaService;

	@Autowired
	private MunicipioService municipioService;
	
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
			
		return new ResponseEntity<List<Provincia>>(list, HttpStatus.OK);
	}
	

	@Operation(
			summary = "Devuelve una mega-lista de municipios con el ID de provincia asociada",
			description = "")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Provincias recuperadas"), 
			@ApiResponse(responseCode = "204", description = "No se encontraron provincias") })
	@GetMapping("/municipios")
	public ResponseEntity<List<Municipio>> getMunicipios() {
		List<Municipio> list = municipioService.getMunicipios();

		if (list.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
			
		return new ResponseEntity<List<Municipio>>(list, HttpStatus.OK);
	}

}
