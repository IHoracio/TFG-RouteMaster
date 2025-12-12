package es.metrica.sept25.evolutivo.service.ine;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.entity.ine.INEMunicipio;
import es.metrica.sept25.evolutivo.repository.INEMunicipioRepository;
import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeService;

@Service
public class INEServiceImp implements INEService {

	@Autowired
	private GeocodeService geocodeService;

	@Autowired
	private INEMunicipioRepository ineMunicipioRepository;

	@Autowired
	private RestTemplate restTemplate;

	private static final String INE_URL = "http://servicios.ine.es/wstempus/js/ES/VALORES_VARIABLE/19";

	@Override
	@Cacheable("codigoIne")
	public Optional<String> getCodigoINE(double lat, double lng) {

		Optional<String> municipio = geocodeService.getMunicipio(lat, lng);

		if (municipio.isEmpty())
			return Optional.empty();

		if (ineMunicipioRepository.count() == 0) {
			UriComponentsBuilder url = UriComponentsBuilder.fromUriString(INE_URL).queryParam("page", "1");
			INEMunicipio[] response = restTemplate.getForObject(url.toUriString(), INEMunicipio[].class);

			if (response == null)
				return Optional.empty();

			List<INEMunicipio> ineMunList = Arrays.asList(response);
			ineMunicipioRepository.saveAllAndFlush(ineMunList);
		}

		List<INEMunicipio> listaGuardada = ineMunicipioRepository.findAll();

		return Optional.of(listaGuardada.stream().filter(m -> m.getNombre().equalsIgnoreCase(municipio.get()))
				.findFirst().get().getCodigoINE());
	}
}
