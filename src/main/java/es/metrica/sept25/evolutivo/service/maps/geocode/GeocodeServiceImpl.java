package es.metrica.sept25.evolutivo.service.maps.geocode;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.AddressComponent;
import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeGroup;
import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeGroupAddress;
import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeResultAddress;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;

@Service
public class GeocodeServiceImpl implements GeocodeService {

	private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

	@Autowired
	private RestTemplate restTemplate;

	@Value("${evolutivo.api_key_google}")
	private String API_KEY_GOOGLE;

	@Override
	public Optional<Coords> getCoordinates(String address) {

		address = normalizarMunicipioParaGeocode(address);

		String url = UriComponentsBuilder.fromUriString(GEOCODE_URL).queryParam("address", address)
				.queryParam("key", API_KEY_GOOGLE).toUriString();

		GeocodeGroup response = restTemplate.getForObject(url, GeocodeGroup.class);

		if (response != null && response.getResults().length > 0) {
			return Optional.of(response.getResults()[0].getGeometry().getLocation());
		}
		return Optional.empty();
	}

	@Override
	public Optional<String> getMunicipio(double lat, double lng) {
		String url = UriComponentsBuilder.fromUriString(GEOCODE_URL)
				.queryParam("latlng", lat + "," + lng)
				.queryParam("key", API_KEY_GOOGLE).toUriString();

		GeocodeGroupAddress response = restTemplate.getForObject(url, GeocodeGroupAddress.class);

		if (response != null && response.getResults() != null && response.getResults().length > 0) {
			GeocodeResultAddress result = response.getResults()[0];

			for (AddressComponent comp : result.getAddress_components()) {
				if (comp.getTypes() != null) {
					List<String> types = comp.getTypes();
					if (types.contains("locality")) {
						String municipioNormalizado = formatearMunicipioParaINE(comp.getLong_name());
						return Optional.of(municipioNormalizado);
					}
					if (types.contains("administrative_area_level_4")) {
						String municipioNormalizado = formatearMunicipioParaINE(comp.getLong_name());
						return Optional.of(municipioNormalizado);
					}
				}
			}
		}
		return Optional.empty();
	}

	public String normalizarMunicipioParaGeocode(String municipio) {
		if (municipio == null)
			return "";

		municipio = municipio.trim();

		if (municipio.toLowerCase().startsWith("el ")) {
			municipio = municipio.substring(3);
		} else if (municipio.toLowerCase().startsWith("la ")) {
			municipio = municipio.substring(3);
		} else if (municipio.toLowerCase().startsWith("los ")) {
			municipio = municipio.substring(4);
		} else if (municipio.toLowerCase().startsWith("las ")) {
			municipio = municipio.substring(4);
		}
		return municipio;
	}

	public String formatearMunicipioParaINE(String municipio) {
		if (municipio == null || municipio.isBlank())
			return "";

		municipio = municipio.trim();

		String[] articulos = { "El", "La", "Los", "Las" };

		for (String articulo : articulos) {
			if (municipio.toLowerCase().startsWith(articulo.toLowerCase() + " ")) {
				String nombrePrincipal = municipio.substring(articulo.length()).trim();
				return nombrePrincipal + ", " + articulo;
			}
		}

		return municipio;
	}

}
