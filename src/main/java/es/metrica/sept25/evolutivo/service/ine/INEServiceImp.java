package es.metrica.sept25.evolutivo.service.ine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import es.metrica.sept25.evolutivo.domain.dto.ine.INEMunicipio;
import es.metrica.sept25.evolutivo.domain.dto.ine.INEResponse;
import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeService;

@Service
public class INEServiceImp implements INEService {
	
	public INEServiceImp() {
        System.out.println("INEService cargado OK");
    }

	@Autowired
	private GeocodeService geocodeService;

	@Autowired
	private RestTemplate restTemplate;

	private static final String INE_URL = "https://servicios.ine.es/wstempus/js/ES/VALORES_VARIABLE/19?page=1";

	@Override
	public String getCodigoINE(double lat, double lng) {

		String municipio = geocodeService.getMunicipio(lat, lng, "");
		System.out.println(municipio);
		if (municipio == null)
			return null;

		try {
			INEResponse response = restTemplate.getForObject(INE_URL, INEResponse.class);

			if (response == null || response.getData() == null)
                return null;

            for (INEMunicipio m : response.getData()) {
                if (m.getNombre().equalsIgnoreCase(municipio)) {
                    return m.getCodigoINE();
                }
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
