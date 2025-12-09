package es.metrica.sept25.evolutivo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;
import es.metrica.sept25.evolutivo.entity.gasolinera.Provincia;
import es.metrica.sept25.evolutivo.service.gasolineras.GasolineraService;
import es.metrica.sept25.evolutivo.service.gasolineras.MunicipioService;
import es.metrica.sept25.evolutivo.service.gasolineras.ProvinciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Gasolineras")
public class OilController {

	@Autowired
	private ProvinciaService provinciaService;

	@Autowired
	private MunicipioService municipioService;

	@Autowired
	private GasolineraService gasolineraService;

	@Operation(
			summary = "Devuelve una lista de gasolineras en un cierto radio de unas coordenadas",
			description = "")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Gasolineras encontradas"), 
			@ApiResponse(responseCode = "204", description = "No se encontraron gasolineras en ese radio") })
	@GetMapping("/oil/gasolineras/{municipio}")
	public ResponseEntity<List<Gasolinera>> getGasolinerasInRadius(
			@RequestParam(required = true, defaultValue = "40.4167279") Double latitud, 
			@RequestParam(required = true, defaultValue = "-3.7032905") Double longitud, 
			@RequestParam(required = true, defaultValue = "5") Long radio) {
		List<Gasolinera> gasolinera = gasolineraService.getGasolinerasForMunicipio(municipio);

		if (gasolinera.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
			
		return new ResponseEntity<List<Gasolinera>>(gasolinera, HttpStatus.OK);
	}

	@Operation(
			summary = "Devuelve una lista de gasolineras dado el nombre de su municipio",
			description = "")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Gasolinera encontrada"), 
			@ApiResponse(responseCode = "204", description = "No se encontró la gasolinera indicada") })
	@GetMapping("/oil/gasolineras/{municipio}")
	public ResponseEntity<List<Gasolinera>> getGasolinerasForMunicipio(@PathVariable String municipio) {
		List<Gasolinera> gasolinera = gasolineraService.getGasolinerasForMunicipio(municipio);

		if (gasolinera.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
			
		return new ResponseEntity<List<Gasolinera>>(gasolinera, HttpStatus.OK);
	}

	@Operation(summary = "Devuelve una gasolinera dado un ID", description = "")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Gasolinera encontrada"),
			@ApiResponse(responseCode = "204", description = "No se encontró la gasolinera indicada") })
	@GetMapping("/oil/id/{idEstacion}")
	public ResponseEntity<Gasolinera> getGasolineraForId(@PathVariable Long idEstacion) {
		Optional<Gasolinera> gasolinera = gasolineraService.getGasolineraForId(idEstacion);

		if (gasolinera.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<Gasolinera>(gasolinera.get(), HttpStatus.OK);
	}

	@Operation(summary = "Devuelve una lista de las provincias españolas", description = "")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Provincias recuperadas"),
			@ApiResponse(responseCode = "204", description = "No se encontraron provincias") })
	@GetMapping("/oil/provincias")
	public ResponseEntity<List<Provincia>> getProvincias() {
		List<Provincia> list = provinciaService.getProvincias();

		if (list.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<List<Provincia>>(list, HttpStatus.OK);
	}

	@Operation(summary = "Devuelve una mega-lista de municipios con el ID de provincia asociada", description = "Query a ejecutar una vez por base de datos. Se auto-cachea.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Provincias recuperadas"),
			@ApiResponse(responseCode = "204", description = "No se encontraron provincias") })
	@GetMapping("/oil/municipios")
	public ResponseEntity<List<Municipio>> getMunicipios() {
		List<Municipio> list = municipioService.getMunicipios();

		if (list.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<List<Municipio>>(list, HttpStatus.OK);
	}

}
