package es.metrica.sept25.evolutivo.service.gasolineras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;
import es.metrica.sept25.evolutivo.entity.maps.routes.Coords;
import es.metrica.sept25.evolutivo.repository.GasolineraRepository;
import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeService;

@Service
public class GasolineraServiceImpl implements GasolineraService {
	private static final String API_URL = "https://api.precioil.es/estaciones/";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	MunicipioService municipioService;
	
	@Autowired
	GeocodeService geocodeService;

	@Autowired
	GasolineraRepository gasolineraRepository;

	@Value("${evolutivo.api_key_google}")
	private String API_KEY_GOOGLE;

	@Override
	@Cacheable("gasolinera_id")
	public Optional<Gasolinera> getGasolineraForId(Long idEstacion) {
		return Optional.of(restTemplate.getForObject(API_URL + "detalles/" + idEstacion, 
				Gasolinera.class));
	}

	@Override
	public List<Gasolinera> getGasolinerasForMunicipio(String municipio) {
		Optional<Municipio> municipioOpt = municipioService.getMunicipioFromString(municipio);
		List<Gasolinera> foundMunicipios = new ArrayList<>();
		if (municipioOpt.isPresent()) {
			Long munId = municipioOpt.get().getIdMunicipio();
			Gasolinera[] gasolinerasPorMunId = restTemplate.getForObject(API_URL + "municipio/" + munId,
					Gasolinera[].class);
			if (Objects.nonNull(gasolinerasPorMunId)) {
				foundMunicipios.addAll(Arrays.asList(gasolinerasPorMunId));
			}
		}

		return foundMunicipios;
	}

	@Override
	public List<Gasolinera> getGasolinerasInRadiusCoords(Double latitud, Double longitud, Long radio) {
		List<Gasolinera> foundRadius = new ArrayList<>();

		if (radio < 1) {
			return foundRadius;
		}

		String urlRadio = UriComponentsBuilder
				.fromUriString(API_URL + "radio")
				.queryParam("latitud", latitud)
				.queryParam("longitud", longitud)
				.queryParam("radio", radio)
				.toUriString();

		Gasolinera[] gasolinerasPorRadio = restTemplate.getForObject(urlRadio, Gasolinera[].class);
		if (Objects.nonNull(gasolinerasPorRadio)) {
			foundRadius.addAll(Arrays.asList(gasolinerasPorRadio));
		}

		return foundRadius;
	}
	
	@Override
	public List<Gasolinera> getGasolinerasInRadiusAddress(String direccion, Long radio) {
		List<Gasolinera> foundRadius = new ArrayList<>();

		Optional<Coords> coordsOpt = geocodeService.getCoordinates(direccion);

		if (coordsOpt.isEmpty() || radio < 1) {
			return foundRadius;
		}

		if (radio < 1) {
			return foundRadius;
		}
		
		String urlRadio = UriComponentsBuilder
				.fromUriString(API_URL + "radio")
				.queryParam("latitud", coordsOpt.get().getLat())
				.queryParam("longitud", coordsOpt.get().getLng())
				.queryParam("radio", radio)
				.toUriString();
		
		Gasolinera[] gasolinerasPorRadio = restTemplate.getForObject(urlRadio, Gasolinera[].class);
		if (Objects.nonNull(gasolinerasPorRadio)) {
			foundRadius.addAll(Arrays.asList(gasolinerasPorRadio));
		}

		return foundRadius;
	}
}