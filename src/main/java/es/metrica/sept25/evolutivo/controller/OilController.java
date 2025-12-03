package es.metrica.sept25.evolutivo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.entity.gasolinera.Provincia;
import es.metrica.sept25.evolutivo.service.ProvinciaServiceImpl;

@RestController
public class OilController {

	private ProvinciaServiceImpl provinciaServiceImpl;

	public OilController(ProvinciaServiceImpl provinciaServiceImpl) {
		this.provinciaServiceImpl = provinciaServiceImpl;
	}

	@GetMapping("/provincias")
	public ResponseEntity<List<Provincia>> getProvincias() {
		List<Provincia> list = provinciaServiceImpl.getProvincias();
		if (list.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}
