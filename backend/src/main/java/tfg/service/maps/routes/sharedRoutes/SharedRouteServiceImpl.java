package tfg.service.maps.routes.sharedRoutes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tfg.domain.dto.maps.routes.Coords;
import tfg.domain.dto.maps.routes.CoordsWithWeather;
import tfg.domain.dto.maps.routes.FullRouteData;
import tfg.domain.dto.maps.routes.sharedRoutes.ShareRouteRequest;
import tfg.entity.gasolinera.Gasolinera;
import tfg.entity.maps.routes.SharedRoute;
import tfg.repository.SharedRouteRepository;
import tfg.service.gasolineras.GasolineraService;
import tfg.service.maps.routes.RoutesService;
import tfg.service.weather.WeatherService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SharedRouteServiceImpl implements SharedRouteService {

    private static final Logger log = LoggerFactory.getLogger(SharedRouteServiceImpl.class);

    private final SharedRouteRepository sharedRouteRepository;
    private final ObjectMapper objectMapper; // Para convertir List<Coords> a String JSON
    
    private final GasolineraService gasolineraService; 
    private final WeatherService weatherService;

    public SharedRouteServiceImpl(SharedRouteRepository sharedRouteRepository, 
            ObjectMapper objectMapper,
            GasolineraService gasolineraService,
            WeatherService weatherService) {
		this.sharedRouteRepository = sharedRouteRepository;
		this.objectMapper = objectMapper;
		this.gasolineraService = gasolineraService;
		this.weatherService = weatherService;
	}

    @Override
    public String generateShareToken(ShareRouteRequest request) {
        
        log.info("Generando nuevo token para compartir ruta");

        String token = UUID.randomUUID().toString();

        try {
            // Convertimos las listas de Java a Strings de JSON para MySQL
            String polylineJson = objectMapper.writeValueAsString(request.getPolylineCoords());
            String legsJson = objectMapper.writeValueAsString(request.getLegCoords());

            SharedRoute sharedRoute = new SharedRoute(token, polylineJson, legsJson, request.getGasRadius(), request.getLang());
            sharedRouteRepository.save(sharedRoute);
            log.debug("Ruta compartida guardada en BD con token: {}", token);
            
            return token;
        } catch (JsonProcessingException e) {
        	log.error("Eror al guardar ruta compartida en BD con token: {}", token);
            throw new RuntimeException("Error al guardar las coordenadas", e);
        }
    }

    @Override
    public Optional<FullRouteData> getSharedRouteData(String token) {
    	Optional<SharedRoute> savedRouteOpt = sharedRouteRepository.findById(token);

        if (savedRouteOpt.isEmpty()) {
            return Optional.empty();
        }

        SharedRoute params = savedRouteOpt.get();

        try {
            // Convertimos los JSON de MySQL de vuelta a listas de Java
            List<Coords> polylineCoords = objectMapper.readValue(
                    params.getPolylineCoordsJson(), new TypeReference<List<Coords>>() {}
            );
            List<Coords> legCoords = objectMapper.readValue(
                    params.getLegCoordsJson(), new TypeReference<List<Coords>>() {}
            );

            List<Gasolinera> freshGasStations = gasolineraService.findGasStationsNearRoute(polylineCoords, params.getGasRadius());
            List<CoordsWithWeather> freshWeather = weatherService.getWeatherForLegs(legCoords, params.getLang());

            // Devolvemos el FullRouteData exactamente igual que en tu endpoint principal
            FullRouteData fullData = new FullRouteData(polylineCoords, legCoords, freshGasStations, freshWeather);
            
            return Optional.of(fullData);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al leer las coordenadas guardadas", e);
        }
    }

}
