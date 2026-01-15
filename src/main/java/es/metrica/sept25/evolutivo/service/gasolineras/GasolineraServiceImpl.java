package es.metrica.sept25.evolutivo.service.gasolineras;

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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;
import es.metrica.sept25.evolutivo.entity.gasolinera.Brand;
import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;
import es.metrica.sept25.evolutivo.repository.BrandRepository;
import es.metrica.sept25.evolutivo.repository.GasolineraRepository;
import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeService;
import jakarta.annotation.PostConstruct;

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
	@Cacheable(value = "getGasStationsInRadiusAddress", cacheManager = "gasCacheManager")
	public List<Gasolinera> getGasolinerasInRadiusAddress(String address, Long radius) {
		log.info("[gas-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to retrieve gas stations for data: "+
				"[address=(" + address + "), r=" + radius + "].");
		List<Gasolinera> foundRadius = new ArrayList<>();

		Optional<Coords> coordsOpt = geocodeService.getCoordinates(address);

		if (coordsOpt.isEmpty()) {
			log.warn("[gas-service] [" + LocalDateTime.now().toString() + "] "
					+ "Coordinates could not be extracted from the address. "
					+ "Data: [address=(" + address + "), r=" + radius + "].");
			return foundRadius;
		}

		if (radius < 1) {
			log.warn("[gas-service] [" + LocalDateTime.now().toString() + "] "
					+ "Invalid radius value for the method call (" + radius + ")"
					+ "Data: [address=(" + address + "), r=" + radius + "].");
			return foundRadius;
		}
		
		String urlRadio = UriComponentsBuilder
				.fromUriString(API_URL + "radio")
				.queryParam("latitud", coordsOpt.get().getLat())
				.queryParam("longitud", coordsOpt.get().getLng())
				.queryParam("radio", radius)
				.toUriString();
		
		Gasolinera[] gasolinerasPorRadio;
		try {
			gasolinerasPorRadio = restTemplate.getForObject(urlRadio, Gasolinera[].class);
		} catch (HttpClientErrorException.NotFound e) {
			log.warn("[gas-service] [" + LocalDateTime.now().toString() + "] "
					+ "No gas stations were found in a radius: " + radius + " for"
					+ " address = " + address + ".");
			return foundRadius;
		}

		if (Objects.nonNull(gasolinerasPorRadio)) {
			foundRadius.addAll(Arrays.asList(gasolinerasPorRadio));
		}

		return foundRadius;
	}

	@Override
	// @Scheduled(cron = "0 0 */6 * * *") // cada 6 horas
	@Transactional
	@CacheEvict(value = "brands", allEntries = true)
	public void syncBrandsFromGasStations() {
		log.info("[gas-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to retrieve all brands for all gas stations.");
	    List<Municipio> municipios = municipioService.getMunicipios();
	    Set<String> marcas = new HashSet<>();

	    for (Municipio municipio : municipios) {

	        List<Gasolinera> gasolineras =
	                getGasolinerasForMunicipio(municipio.getNombreMunicipio());

	        for (Gasolinera gasolinera : gasolineras) {
	        	String marca = gasolinera.getMarca();

	        	if (marca != null && !marca.isBlank()) {
	        	    marca = marca.replaceFirst("^\"[^\"]*\"\\s*", "").trim();
	        	    
	        	    if (marca.matches(".*[a-zA-Z].*")) {
	        	        marcas.add(marca);
	        	    }
	        	}
	        }
	    }
	    syncBrands(marcas);

		log.info("[gas-service] [" + LocalDateTime.now().toString() + "] "
				+ "Sync brands finished. Total brands: {}", marcas.size());

	}
	@Override
	@Cacheable(value = "brands", cacheManager = "staticCacheManager")
	public List<String> getMarcasFromAllGasolineras() {

	    log.info("[gas-service] [" + LocalDateTime.now().toString() + "] "
	    		+ "Fetching brands from database");

	    return brandRepository.findAll()
	        .stream()
	        .map(Brand::getName)
	        .sorted()
	        .toList();
	}
	
	@Transactional
	public void syncBrands(Set<String> marcas) {

	    LocalDateTime now = LocalDateTime.now();

	    for (String marca : marcas) {
	        brandRepository.findByName(marca)
	            .orElseGet(() -> {
	                Brand brand = new Brand();
	                brand.setName(marca);
	                brand.setLastUpdated(now);
	                return brandRepository.save(brand);
	            });
	    }
	}
	
	@PostConstruct
    public void initBrandsAtStartup() {
        if (brandRepository.count() == 0) {
            log.info("[gas-service] [ " + LocalDateTime.now().toString() + "] "
            		+ "Initial brand sync at startup");
            syncBrandsFromGasStations();

           getMarcasFromAllGasolineras();
        }
    }
}