package es.metrica.sept25.evolutivo.service.gasolineras;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

	@Override
	@Cacheable("provincias")
	public List<Provincia> getProvincias() {
		// Cogemos del repo
		List<Provincia> provList = provinciaRepository.findAll();

		// Si no tenemos, populamos con las españolas
		if (Objects.isNull(provList) | provList.isEmpty()) {
			Provincia[] provArr = restTemplate.getForObject(API_URL, Provincia[].class);
			if (!Objects.isNull(provArr)) {
				provList = Arrays.asList(provArr);
				provinciaRepository.saveAllAndFlush(
						provList.stream().filter(p -> p.getIdProvincia() < 100).collect(Collectors.toList()));
			}
		}

		// Devolvemos las españolas
		return provList;
	}

	public Optional<Provincia> getProvinciaById(Long id) {
		return provinciaRepository.findById(id).or(Optional::empty);
	}
}
