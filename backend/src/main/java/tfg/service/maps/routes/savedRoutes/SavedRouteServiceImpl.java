package tfg.service.maps.routes.savedRoutes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SavedRouteServiceImpl implements SavedRouteService {

    private static final Logger log = LoggerFactory.getLogger(SavedRouteServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SavedRouteRepository repository;

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

        // === NUEVOS CAMPOS ===
        route.setTotalDistance(request.getTotalDistance());
        route.setTotalDuration(request.getTotalDuration());

        // Transformar coordenadas a JSON
        try {
            route.setPolylineCoordsJson(objectMapper.writeValueAsString(request.getPolylineCoords()));
            route.setLegCoordsJson(objectMapper.writeValueAsString(request.getLegCoords()));
        } catch (JsonProcessingException e) {
            log.error("Error al convertir coordenadas a JSON para ruta: {}", request.getName(), e);
            throw new RuntimeException("Error al guardar la ruta", e);
        }

        // Mapear puntos (Origen, Destino, Waypoints...)
        List<Point> puntos = request.getPuntosDTO().stream().map(dto -> {
            Point point = new Point();
            point.setPlaceSelection(dto.getPlaceSelection());
            point.setType(Point.TypePoint.valueOf(dto.getType().toUpperCase()));
            point.setSavedRoute(route);
            return point;
        }).toList();

        route.setPuntos(puntos);

        SavedRoute saved = repository.save(route);

        log.info("[route-save-service] [{}] Ruta guardada correctamente: '{}' | Dist: {} | Dur: {}",
                LocalDateTime.now(), request.getName(), request.getTotalDistance(), request.getTotalDuration());

        return mapToDTO(saved);
    }

    // ==========================================================
    // EJECUTAR RUTA GUARDADA (con distancia y duración)
    // ==========================================================
    @Override
    public Optional<FullRouteData> executeRoute(String routeId) {
        log.info("[route-save-service] [{}] Intentando ejecutar ruta guardada con ID: {}", 
                LocalDateTime.now(), routeId);

        Optional<SavedRoute> routeOpt = repository.findByRouteId(routeId);
        if (routeOpt.isEmpty()) {
            log.warn("Ruta no encontrada con ID: {}", routeId);
            return Optional.empty();
        }

        SavedRoute route = routeOpt.get();

        try {
            // Leer coordenadas guardadas
            List<Coords> polylineCoords = objectMapper.readValue(
                    route.getPolylineCoordsJson(), new TypeReference<List<Coords>>() {}
            );

            List<Coords> legCoords = objectMapper.readValue(
                    route.getLegCoordsJson(), new TypeReference<List<Coords>>() {}
            );

            // Datos frescos
            List<Gasolinera> freshGasStations = gasolineraService
                    .findGasStationsNearRoute(polylineCoords, route.getGasRadius());

            List<CoordsWithWeather> freshWeather = weatherService
                    .getWeatherForLegs(legCoords, route.getLanguage());

            // Crear FullRouteData con los nuevos campos
            FullRouteData fullData = new FullRouteData(
                    route.getTotalDistance(),
                    route.getTotalDuration(),
                    polylineCoords,
                    legCoords,
                    freshGasStations,
                    freshWeather
            );

            return Optional.of(fullData);

        } catch (JsonProcessingException e) {
            log.error("[route-save-service] Error al leer coordenadas de la ruta ID: {}", routeId, e);
            throw new RuntimeException("Error al leer los datos de la ruta guardada", e);
        }
    }

    @Override
    @Transactional
    public void deleteRoute(String routeId, User user) {
        Optional<SavedRoute> routeOpt = repository.findByRouteId(routeId);

        if (routeOpt.isEmpty()) {
            log.error("No se encontró la ruta con ID: {}", routeId);
            return;
        }

        SavedRoute route = routeOpt.get();

        if (!route.getUser().getId().equals(user.getId())) {
            log.error("Usuario {} intentó eliminar una ruta que no le pertenece: {}", 
                    user.getEmail(), routeId);
            return;
        }

        repository.delete(route);
        log.info("[route-save-service] Ruta eliminada correctamente: {}", routeId);
    }

    @Override
    public Optional<SavedRouteDTO> getSavedRoute(String routeId) {
        Optional<SavedRoute> routeOpt = repository.findByRouteId(routeId);
        return routeOpt.map(this::mapToDTO);
    }

    @Override
    public Optional<List<SavedRouteDTO>> getAllSavedRoutes(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.warn("Usuario no encontrado: {}", email);
            return Optional.empty();
        }

        List<SavedRouteDTO> routes = userOpt.get().getSavedRoutes().stream()
                .map(this::mapToDTO)
                .toList();

        return Optional.of(routes);
    }

    @Override
    @Transactional
    public SavedRouteDTO renameRoute(String name, SavedRouteDTO savedRoute) {
        SavedRoute route = repository.findByRouteId(savedRoute.getRouteId())
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

        route.setName(name);
        repository.save(route);

        log.info("[route-save-service] Ruta renombrada a: {}", name);
        return mapToDTO(route);
    }

    // ====================== HELPER ======================
    private SavedRouteDTO mapToDTO(SavedRoute route) {
        SavedRouteDTO dto = new SavedRouteDTO();
        dto.setRouteId(route.getRouteId());
        dto.setName(route.getName());
        dto.setTotalDistance(route.getTotalDistance());
        dto.setTotalDuration(route.getTotalDuration());

        dto.setPoints(route.getPuntos().stream().map(p -> {
            PointDTO pdto = new PointDTO();
            pdto.setType(p.getType().name());
            pdto.setPlaceSelection(p.getPlaceSelection());
            return pdto;
        }).toList());

        return dto;
    }
}