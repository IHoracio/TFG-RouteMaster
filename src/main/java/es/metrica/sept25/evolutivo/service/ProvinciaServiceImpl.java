package es.metrica.sept25.evolutivo.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import es.metrica.sept25.evolutivo.entity.gasolinera.Provincia;

@Service
public class ProvinciaServiceImpl {

	private static final String API_URL = "https://api.precioil.es/provincias";

	@Autowired
	private RestTemplate restTemplate;


	public List<Provincia> getProvincias() {
		Provincia[] provincias = restTemplate.getForObject(API_URL, Provincia[].class);
		return Arrays.stream(provincias).toList();
	}
}
