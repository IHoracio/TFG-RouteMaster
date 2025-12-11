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

	/**
	 * NO USAR SALVO ESTRICTAMENTE NECESARIO
	 */
	@Override
	@Cacheable("municipios")
	public List<Municipio> getMunicipios() {
		List<Provincia> provList = provinciaService.getProvincias();
		List<Municipio> munList = municipioRepository.findAll();

		List<Long> provIds = provList.stream().map(p -> p.getIdProvincia()).collect(Collectors.toList());

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
	@Cacheable("municipio_id")
	public Optional<Municipio> getMunicipioFromId(Long idMunicipio) {
		List<Municipio> munList = getMunicipios();
		return munList.stream()
				.filter(m -> m.getIdMunicipio() == idMunicipio)
				.findFirst();
	}

	@Override
	@Cacheable("municipio_str")
	public Optional<Municipio> getMunicipioFromString(String munStr) {
		List<Municipio> munList = getMunicipios();
		return munList.stream()
				.filter(m -> m.getNombreMunicipio().toLowerCase().equals(munStr.toLowerCase()))
				.findFirst();
	}
}
