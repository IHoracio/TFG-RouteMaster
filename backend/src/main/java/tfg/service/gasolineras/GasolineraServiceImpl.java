package tfg.service.gasolineras;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tfg.controller.maps.routes.savedRoutes.SavedRouteController;
import tfg.domain.dto.maps.routes.Coords;
import tfg.entity.gasolinera.Gasolinera;
import tfg.entity.gasolinera.Municipio;
import tfg.entity.gasolinera.PlaceDetailsResponse;
import tfg.enums.BrandEnum;
import tfg.repository.BrandRepository;
import tfg.repository.GasolineraRepository;
import tfg.service.maps.geocode.GeocodeService;

@Service
public class GasolineraServiceImpl implements GasolineraService {
	
	private static final Logger log = LoggerFactory.getLogger(GasolineraServiceImpl.class);
	private static final String API_URL = "https://api.precioil.es/estaciones/";
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	MunicipioService municipioService;

	@Autowired
	GeocodeService geocodeService;

	@Autowired
	GasolineraRepository gasolineraRepository;
	
	@Autowired
	BrandRepository brandRepository;

    GasolineraServiceImpl() {
    }

	@Override
	@Cacheable(value = "gasolinera_id", cacheManager = "gasCacheManager")
	public Optional<Gasolinera> getGasolineraForId(Long idEstacion) {
		log.info("[gas-service] [" + LocalDateTime.now().toString() + "] Attempting to retrieve gas station with ID: "
				+ idEstacion + ".");

		Gasolinera retrieved = null;
		try {
			retrieved = restTemplate.getForObject(API_URL + "detalles/" + idEstacion, Gasolinera.class);
		} catch (HttpClientErrorException.NotFound e) {
			log.warn("[gas-service] [" + LocalDateTime.now().toString() + "] " + "No gas station was found for ID "
					+ idEstacion + ".");
		}

		if (Objects.nonNull(retrieved)) {
			log.info("[gas-service] [" + LocalDateTime.now().toString()
					+ "] Succesfully retrieved gas station with ID: " + idEstacion + ".");
			return Optional.of(retrieved);
		} else {
			return Optional.empty();
		}
	}

	@Override
	@Cacheable(value = "getGasStationsForMunStr", cacheManager = "gasCacheManager")
	public List<Gasolinera> getGasolinerasForMunicipio(String municipio) {
		log.info("[gas-service] [" + LocalDateTime.now().toString()
				+ "] Attempting to retrieve gas stations for municipality string: " + municipio + ".");
		Optional<Municipio> municipioOpt = municipioService.getMunicipioFromString(municipio);
		List<Gasolinera> foundMunicipios = new ArrayList<>();
		if (municipioOpt.isPresent()) {
			log.info("[gas-service] [" + LocalDateTime.now().toString() + "] Found municipality for string: "
					+ municipio + ".");
			Long munId = municipioOpt.get().getIdMunicipio();
			Gasolinera[] gasolinerasPorMunId = restTemplate.getForObject(API_URL + "municipio/" + munId,
					Gasolinera[].class);
			if (Objects.nonNull(gasolinerasPorMunId)) {
				log.info("[gas-service] [" + LocalDateTime.now().toString() + "] Found gas stations for municipality: "
						+ municipio + ".");
				foundMunicipios.addAll(Arrays.asList(gasolinerasPorMunId));
			} else {
				log.warn("[gas-service] [" + LocalDateTime.now().toString()
						+ "] Couldn't retrieve any data for municipality: " + municipio + ".");
			}
		} else {
			log.warn("[gas-service] [" + LocalDateTime.now().toString() + "] Municipality for string: " + municipio
					+ " was not found.");
		}

		return foundMunicipios;
	}

	@Override
	@Cacheable(value = "getGasStationsInRadiusCoords", cacheManager = "gasCacheManager")

	public List<Gasolinera> getGasolinerasInRadiusCoords(Double lat, Double lng, Long radius) {
		log.info("[gas-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to search for gas stations around data: [lat:(" + lat + "), lng:(" + lng + "), r:"
				+ radius + "].");
		List<Gasolinera> foundRadius = new ArrayList<>();

		if (radius < 1) {
			log.info("[gas-service] [" + LocalDateTime.now().toString() + "] " +
					"Invalid radius (r=" + radius + 
					") for searching gas stations around (lat=[" + lat + 
					"], lng=[" + lng + "]).");
			return foundRadius;
		}

		String urlRadio = UriComponentsBuilder
				.fromUriString(API_URL + "radio")
				.queryParam("latitud", lat)
				.queryParam("longitud", lng)
				.queryParam("radio", radius)
				.toUriString();

		Gasolinera[] gasolinerasPorRadio;
		try {
			gasolinerasPorRadio = restTemplate.getForObject(urlRadio, Gasolinera[].class);
		} catch (HttpClientErrorException.NotFound e) {
			log.warn("[gas-service] [" + LocalDateTime.now().toString() + "] "
					+ "No gas stations were found around data: "
					+ "[lat=(" + lat + "), lng=(" + lng + "), r=" + radius + "].");
			return foundRadius;
		}

		if (Objects.nonNull(gasolinerasPorRadio)) {
			log.info("[gas-service] [" + LocalDateTime.now().toString() + "] "
					+ "Found gas stations around data: "
					+ "[lat=(" + lat + "), lng=(" + lng + "), r=" + radius + "].");
			foundRadius.addAll(Arrays.asList(gasolinerasPorRadio));
		}

		return foundRadius;
	}
	
	@Override
	@Cacheable(value = "getGasStationsInRadiusPlace", cacheManager = "gasCacheManager")
	public List<Gasolinera> getGasolinerasInRadiusPlace(String place, Long radius) {
	    log.info("[gas-service] Buscando gasolineras para input: {} con radio: {}", place, radius);
	    
	    Optional<Coords> coordsOpt;

	    if (place.startsWith("place_id:")) {
	        String placeId = place.replace("place_id:", "");
	        coordsOpt = geocodeService.getCoordinatesFromPlaceId(placeId);
	    } else {
	        coordsOpt = Optional.empty();
	    }

	    if (coordsOpt.isEmpty()) {
	        return new ArrayList<>();
	    }

	    Coords c = coordsOpt.get();
	    return getGasolinerasInRadiusCoords(c.getLat(), c.getLng(), radius);
	}
	
	@Override
    public List<Gasolinera> findGasStationsNearRoute(List<Coords> polylineCoords, Long radius) {
        log.info("[gas-service] [" + LocalDateTime.now() + "] Buscando gasolineras a lo largo de una ruta con radio: " + radius);
        
        Set<Gasolinera> uniqueGasStations = new HashSet<>();
        
        if (polylineCoords == null || polylineCoords.isEmpty()) {
            return new ArrayList<>();
        }

        // OPTIMIZACIÓN: No comprobamos los miles de puntos. 
        // Comprobamos 1 de cada 100 puntos (ajusta este "step" según te convenga).
        int step = Math.max(1, polylineCoords.size() / 20); // Asegura al menos 20 llamadas repartidas en la ruta

        for (int i = 0; i < polylineCoords.size(); i += step) {
            Coords point = polylineCoords.get(i);
            
            List<Gasolinera> gasStationsNearPoint = getGasolinerasInRadiusCoords(
                    point.getLat(), point.getLng(), radius
            );
            
            uniqueGasStations.addAll(gasStationsNearPoint);
        }

        Coords lastPoint = polylineCoords.get(polylineCoords.size() - 1);
        uniqueGasStations.addAll(getGasolinerasInRadiusCoords(lastPoint.getLat(), lastPoint.getLng(), radius));

        log.info("[gas-service] [" + LocalDateTime.now() + "] Se han encontrado " + uniqueGasStations.size() + " gasolineras únicas en la ruta.");
        
        return new ArrayList<>(uniqueGasStations);
    }

	@Override
	@Cacheable(value = "brands", cacheManager = "staticCacheManager")
	public List<String> getMarcasFromAllGasolineras() {

		log.info("[gas-service] [" + LocalDateTime.now() + "] Fetching famous brands from enum");
	    return Arrays.stream(BrandEnum.values())
	                 .map(BrandEnum::getDisplayName)
	                 .sorted()
	                 .toList();
	}
}