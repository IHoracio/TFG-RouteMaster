package es.metrica.sept25.evolutivo.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import es.metrica.sept25.evolutivo.entity.gasolinera.Provincia;
import es.metrica.sept25.evolutivo.repository.ProvinciaRepository;

@Service
public class ProvinciaServiceImpl implements ProvinciaService {

	private static final String API_URL = "https://api.precioil.es/provincias";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	ProvinciaRepository provinciaRepository;

	public void save(Provincia provincia) {
		provinciaRepository.save(provincia);
	}

	@Cacheable("provincias")
	public List<Provincia> getProvincias() {
		// Cogemos del repo
		List<Provincia> provList = provinciaRepository.findAll();
		// Si no tenemos, populamos con las españolas
		if (provList.isEmpty()) {
			System.out.println("HERE");
			Provincia[] provArr = restTemplate.getForObject(API_URL, Provincia[].class);
			if (provArr != null) {
				provList = Arrays.asList(provArr);
				provinciaRepository.saveAll(provList.stream().filter(p -> p.getIdProvincia() < 100).toList());
			}
		}

		// Devolvemos las españolas
		return provList;
	}

	public Optional<Provincia> getProvinciaById(Long id) {
		return provinciaRepository.findById(id).or(Optional::empty);
	}
}
