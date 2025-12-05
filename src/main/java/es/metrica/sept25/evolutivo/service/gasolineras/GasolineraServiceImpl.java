package es.metrica.sept25.evolutivo.service.gasolineras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;
import es.metrica.sept25.evolutivo.repository.GasolineraRepository;

@Service
public class GasolineraServiceImpl implements GasolineraService {
	private static final String API_URL = "https://api.precioil.es/estaciones/municipio/";

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	MunicipioService municipioService;

	@Autowired
	GasolineraRepository gasolineraRepository;

	@Override
	public void save(Gasolinera gasolinera) {
		gasolineraRepository.save(gasolinera);
	}

	@Override
	@Cacheable("gasolinera_id")
	public Optional<Gasolinera> getGasolineraForId(Long idEstacion) {
		return Optional.of(restTemplate.getForObject(API_URL + idEstacion, Gasolinera.class));
	}

	@Override
	public List<Gasolinera> getGasolinerasForMunicipio(String municipio) {
		Optional<Municipio> municipioOpt = municipioService.getMunicipioFromString(municipio);
		List<Gasolinera> foundMunicipios = new ArrayList<>();
		if (municipioOpt.isPresent()) {
			Long munId = municipioOpt.get().getIdMunicipio();
			Gasolinera[] gasolinerasPorMunId = restTemplate.getForObject(API_URL + munId, Gasolinera[].class);
			if (Objects.nonNull(gasolinerasPorMunId)) {
				foundMunicipios.addAll(Arrays.asList(gasolinerasPorMunId));
			}
		}

		return foundMunicipios;
	}
}