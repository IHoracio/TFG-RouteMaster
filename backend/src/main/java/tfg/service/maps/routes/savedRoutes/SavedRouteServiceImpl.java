package tfg.service.maps.routes.savedRoutes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tfg.domain.dto.maps.routes.Coords;
import tfg.domain.dto.maps.routes.CoordsWithWeather;
import tfg.domain.dto.maps.routes.FullRouteData;
import tfg.domain.dto.maps.routes.savedRoutes.PointDTO;
import tfg.domain.dto.maps.routes.savedRoutes.SavedRouteDTO;
import tfg.domain.dto.maps.routes.savedRoutes.SavedRouteRequest;
import tfg.entity.gasolinera.Gasolinera;
import tfg.entity.maps.routes.Point;
import tfg.entity.maps.routes.SavedRoute;
import tfg.entity.user.User;
import tfg.repository.SavedRouteRepository;
import tfg.repository.UserRepository;
import tfg.service.gasolineras.GasolineraService;
import tfg.service.weather.WeatherService;

@Service
public class SavedRouteServiceImpl implements SavedRouteService {

    private static final Logger log = LoggerFactory.getLogger(SavedRouteServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SavedRouteRepository repository;

    // Nuevas dependencias para las coordenadas y los servicios en vivo
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GasolineraService gasolineraService;

    @Autowired
    private WeatherService weatherService;

    @Override
    @Transactional
    public SavedRouteDTO saveRoute(SavedRouteRequest request, User user) {
        SavedRoute route = new SavedRoute();
        route.setName(request.getName());
        route.setUser(user);
        route.setLanguage(request.getLanguage());
        route.setGasRadius(request.getGasRadius());
        
        // Transformar las coordenadas a JSON
        try {
            route.setPolylineCoordsJson(objectMapper.writeValueAsString(request.getPolylineCoords()));
            route.setLegCoordsJson(objectMapper.writeValueAsString(request.getLegCoords()));
        } catch (JsonProcessingException e) {
            log.error("Error al convertir coordenadas a JSON", e);
            throw new RuntimeException("Error al guardar la ruta", e);
        }

        // Mapear los puntos visuales (Origen, Destino...)
        List<Point> puntos = request.getPuntosDTO().stream().map(dto -> {
            Point point = new Point();
            point.setPlaceSelection(dto.getPlaceSelection());
            point.setType(Point.TypePoint.valueOf(dto.getType().toUpperCase()));
            point.setSavedRoute(route);
            return point;
        }).toList();

        route.setPuntos(puntos);

        SavedRoute saved = repository.save(route);

        log.info("[route-save-service] [{}] Successfully saved route with name: {}.", LocalDateTime.now(), request.getName());
        return mapToDTO(saved);
    }

    // ==========================================================
    // MÉTODO ESTRELLA: Ejecutar la ruta guardada
    // ==========================================================
    @Override
    public Optional<FullRouteData> executeRoute(String routeId) {
        log.info("[route-save-service] [{}] Attempting to execute saved route with ID: {}", LocalDateTime.now(), routeId);
        Optional<SavedRoute> routeOpt = repository.findByRouteId(routeId);

        if (routeOpt.isEmpty()) {
            return Optional.empty();
        }

        SavedRoute route = routeOpt.get();

        try {
            // 1. Leer las coordenadas desde MySQL
            List<Coords> polylineCoords = objectMapper.readValue(
                    route.getPolylineCoordsJson(), new TypeReference<List<Coords>>() {}
            );
            List<Coords> legCoords = objectMapper.readValue(
                    route.getLegCoordsJson(), new TypeReference<List<Coords>>() {}
            );

            // 2. Usar el que se guardó por defecto
            Long radiusToUse = route.getGasRadius();

            // 3. Recuperar Gasolina y Tiempo "frescos"
            List<Gasolinera> freshGasStations = gasolineraService.findGasStationsNearRoute(polylineCoords, radiusToUse);
            List<CoordsWithWeather> freshWeather = weatherService.getWeatherForLegs(legCoords, route.getLanguage());

            FullRouteData fullData = new FullRouteData(polylineCoords, legCoords, freshGasStations, freshWeather);
            return Optional.of(fullData);

        } catch (JsonProcessingException e) {
            log.error("[route-save-service] Error reading coordinates for route ID: {}", routeId, e);
            throw new RuntimeException("Error al leer las coordenadas guardadas", e);
        }
    }

    @Override
    @Transactional
    public void deleteRoute(String routeId, User user) {
        log.info("[route-save-service] [{}] Attempting to delete saved route with ID: {} for user: {}", 
                LocalDateTime.now(), routeId, user.getEmail());
        
        Optional<SavedRoute> route = repository.findByRouteId(routeId);
        
        if (route.isEmpty()) {
            log.error("Couldn't find route with ID: {}", routeId);
            return;
        }

        if (!route.get().getUser().getId().equals(user.getId())) {
            log.error("The user {} tried to delete the route with ID: {} which isn't theirs.", user.getEmail(), routeId);
            return;
        }

        repository.delete(route.get());
    }

    @Override
    public Optional<SavedRouteDTO> getSavedRoute(String routeId) {
        log.info("[route-save-service] [{}] Attempting to retrieve saved route with ID: {}", LocalDateTime.now(), routeId);
        Optional<SavedRoute> route = repository.findByRouteId(routeId);
        
        if (route.isPresent()) {
            return Optional.of(mapToDTO(route.get()));
        } else {
            log.warn("Couldn't retrieve a saved route for ID: {}", routeId);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<List<SavedRouteDTO>> getAllSavedRoutes(String email) {
        log.info("[route-save-service] [{}] Attempting to retrieve all saved routes for user: {}", LocalDateTime.now(), email);
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            List<SavedRouteDTO> routes = user.get().getSavedRoutes().stream()
                    .map(this::mapToDTO)
                    .toList();
            log.info("Retrieved {} saved routes for user: {}", routes.size(), email);
            return Optional.of(routes);
        } else {
            log.warn("Couldn't retrieve saved routes because user was not found: {}", email);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public SavedRouteDTO renameRoute(String name, SavedRouteDTO savedRoute) {
        SavedRoute route = repository.findByRouteId(savedRoute.getRouteId()).orElseThrow();
        route.setName(name);
        
        repository.save(route);

        log.info("[route-save-service] [{}] Successfully renamed route with ID {} to: {}", 
                LocalDateTime.now(), route.getRouteId(), name);
        return mapToDTO(route);
    }

    // Método helper para no repetir código de mapeo
    private SavedRouteDTO mapToDTO(SavedRoute route) {
        SavedRouteDTO dto = new SavedRouteDTO();
        dto.setRouteId(route.getRouteId());
        dto.setName(route.getName());

        dto.setPoints(route.getPuntos().stream().map(p -> {
            PointDTO pdto = new PointDTO();
            pdto.setType(p.getType().name());
            pdto.setPlaceSelection(p.getPlaceSelection());
            return pdto;
        }).toList());

        return dto;
    }
}