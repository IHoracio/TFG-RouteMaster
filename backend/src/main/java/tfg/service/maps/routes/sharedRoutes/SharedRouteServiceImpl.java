package tfg.service.maps.routes.sharedRoutes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tfg.domain.dto.maps.routes.Coords;
import tfg.domain.dto.maps.routes.CoordsWithWeather;
import tfg.domain.dto.maps.routes.FullRouteData;
import tfg.domain.dto.maps.routes.savedRoutes.PointDTO;
import tfg.domain.dto.maps.routes.sharedRoutes.ShareRouteRequest;
import tfg.entity.gasolinera.Gasolinera;
import tfg.entity.maps.routes.SharedRoute;
import tfg.repository.SharedRouteRepository;
import tfg.service.gasolineras.GasolineraService;
import tfg.service.weather.WeatherService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SharedRouteServiceImpl implements SharedRouteService {

    private static final Logger log = LoggerFactory.getLogger(SharedRouteServiceImpl.class);

    private final SharedRouteRepository sharedRouteRepository;
    private final ObjectMapper objectMapper;
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
            // Convertir listas a JSON
            String polylineJson = objectMapper.writeValueAsString(request.getPolylineCoords());
            String legsJson = objectMapper.writeValueAsString(request.getLegCoords());
            String puntosJson = objectMapper.writeValueAsString(request.getPuntosDTO());

            SharedRoute sharedRoute = new SharedRoute(
                    token,
                    polylineJson,
                    legsJson,
                    puntosJson,
                    request.getTotalDistance(),
                    request.getTotalDuration(),
                    request.getGasRadius(),
                    request.getLang()
            );

            sharedRouteRepository.save(sharedRoute);

            log.debug("Ruta compartida guardada con token: {} | Distancia: {} | Duración: {}",
                    token, request.getTotalDistance(), request.getTotalDuration());

            return token;

        } catch (JsonProcessingException e) {
            log.error("Error al guardar ruta compartida con token: {}", token, e);
            throw new RuntimeException("Error al guardar las coordenadas de la ruta", e);
        }
    }

    @Override
    public Optional<FullRouteData> getSharedRouteData(String token) {

        Optional<SharedRoute> savedRouteOpt = sharedRouteRepository.findById(token);

        if (savedRouteOpt.isEmpty()) {
            log.warn("No se encontró ruta compartida con token: {}", token);
            return Optional.empty();
        }

        SharedRoute params = savedRouteOpt.get();

        try {
            // Deserializar JSONs
            List<Coords> polylineCoords = objectMapper.readValue(
                    params.getPolylineCoordsJson(), new TypeReference<List<Coords>>() {}
            );

            List<Coords> legCoords = objectMapper.readValue(
                    params.getLegCoordsJson(), new TypeReference<List<Coords>>() {}
            );
            
            List<PointDTO> puntosDTO = objectMapper.readValue(
                    params.getPuntosJson(), new TypeReference<List<PointDTO>>() {}
            );

            // Datos frescos (se recalculan en cada consulta)
            List<Gasolinera> freshGasStations = gasolineraService
                    .findGasStationsNearRoute(polylineCoords, params.getGasRadius());

            List<CoordsWithWeather> freshWeather = weatherService
                    .getWeatherForLegs(legCoords, params.getLang());

            FullRouteData fullData = new FullRouteData(
                    params.getTotalDistance(),
                    params.getTotalDuration(),
                    puntosDTO,
                    polylineCoords,
                    legCoords,
                    freshGasStations,
                    freshWeather
            );

            return Optional.of(fullData);

        } catch (JsonProcessingException e) {
            log.error("Error al deserializar datos de la ruta compartida con token: {}", token, e);
            throw new RuntimeException("Error al leer los datos guardados de la ruta", e);
        }
    }
}