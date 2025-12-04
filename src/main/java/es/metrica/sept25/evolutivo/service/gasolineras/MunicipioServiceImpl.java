package es.metrica.sept25.evolutivo.service.gasolineras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;
import es.metrica.sept25.evolutivo.entity.gasolinera.Provincia;
import es.metrica.sept25.evolutivo.repository.MunicipioRepository;

@Service
public class MunicipioServiceImpl implements MunicipioService {

	private static final String API_URL = "https://api.precioil.es/municipios/provincia/";

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ProvinciaService provinciaService;

	@Autowired
	MunicipioRepository municipioRepository;

	@Override
	@Cacheable("municipios")
	/**
	 * NO USAR SALVO ESTRICTAMENTE NECESARIO
	 */
	public List<Municipio> getMunicipios() {
		List<Provincia> provList = provinciaService.getProvincias();
		List<Municipio> munList = municipioRepository.findAll();
		
		List<Long> provIds = provList
				.stream()
				.map(p -> p.getIdProvincia())
				.collect(Collectors.toList());
		
		if (Objects.isNull(munList) | munList.isEmpty()) {
			ArrayList<Municipio> tempList = new ArrayList<>();
			provIds.forEach(l -> {
				Municipio[] munArr = restTemplate.getForObject(API_URL + l, Municipio[].class);
				if (!Objects.isNull(munArr)) {
					tempList.addAll(Arrays.asList(munArr));
					municipioRepository.saveAllAndFlush(tempList);
				}
			});
			return tempList;
		}
		
		return munList;
	}

	@Override
	@Cacheable("municipio")
	public Optional<Provincia> getProvinciaForMunicipio(Municipio mun) {
		Long provId = mun.getIdProvincia();
		List<Provincia> provList = provinciaService.getProvincias();
		return provList.stream().filter(p -> p.getIdProvincia() == provId).findFirst();
	}

}
