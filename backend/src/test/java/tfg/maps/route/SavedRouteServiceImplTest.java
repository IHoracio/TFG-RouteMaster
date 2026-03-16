package tfg.maps.route;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import tfg.domain.dto.maps.routes.FullRouteData;
import tfg.domain.dto.maps.routes.savedRoutes.PointDTO;
import tfg.domain.dto.maps.routes.savedRoutes.SavedRouteDTO;
import tfg.domain.dto.maps.routes.savedRoutes.SavedRouteRequest;
import tfg.entity.maps.routes.Point;
import tfg.entity.maps.routes.SavedRoute;
import tfg.entity.user.User;
import tfg.repository.SavedRouteRepository;
import tfg.repository.UserRepository;
import tfg.service.gasolineras.GasolineraService;
import tfg.service.maps.routes.savedRoutes.SavedRouteServiceImpl;
import tfg.service.weather.WeatherService;

@ExtendWith(MockitoExtension.class)
public class SavedRouteServiceImplTest {

    @Mock
    private SavedRouteRepository repository;
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private GasolineraService gasolineraService;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private SavedRouteServiceImpl service;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    void saveRoute_ok() throws Exception {
        SavedRouteRequest request = new SavedRouteRequest();
        request.setName("Mi Ruta");
        request.setLanguage("es");
        request.setGasRadius(5L);
        request.setPuntosDTO(List.of(
            new PointDTO("ORIGIN", "Calle Falsa 123"),
            new PointDTO("DESTINATION", "Avenida Siempre Viva")
        ));
        request.setPolylineCoords(new ArrayList<>());
        request.setLegCoords(new ArrayList<>());

        // Construimos el objeto simulado que "devolvería" la base de datos
        SavedRoute savedRoute = new SavedRoute();
        savedRoute.setRouteId("uuid-42"); 
        savedRoute.setName("Mi Ruta");
        savedRoute.setUser(testUser);
        
        Point p1 = new Point();
        p1.setType(Point.TypePoint.ORIGIN);
        p1.setAddress("Calle Falsa 123");
        Point p2 = new Point();
        p2.setType(Point.TypePoint.DESTINATION);
        p2.setAddress("Avenida Siempre Viva");
        savedRoute.setPuntos(List.of(p1, p2));

        when(objectMapper.writeValueAsString(any())).thenReturn("[]");
        when(repository.save(any(SavedRoute.class))).thenReturn(savedRoute);

        SavedRouteDTO result = service.saveRoute(request, testUser);

        assertNotNull(result);
        assertEquals("uuid-42", result.getRouteId());
        assertEquals("Mi Ruta", result.getName());
        assertEquals(2, result.getPoints().size());
        
        verify(repository).save(any(SavedRoute.class));
    }

    @Test
    void deleteRoute_existingRouteAndSameUser() {
        SavedRoute route = new SavedRoute();
        route.setRouteId("uuid-1");
        route.setUser(testUser);

        when(repository.findByRouteId("uuid-1")).thenReturn(Optional.of(route));

        service.deleteRoute("uuid-1", testUser);

        verify(repository).delete(route);
    }

    @Test
    void deleteRoute_routeNotFound() {
        when(repository.findByRouteId("uuid-1")).thenReturn(Optional.empty());

        service.deleteRoute("uuid-1", testUser);

        verify(repository, never()).delete(any());
    }

    @Test
    void deleteRoute_routeBelongsToOtherUser() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");

        SavedRoute route = new SavedRoute();
        route.setRouteId("uuid-1");
        route.setUser(otherUser);

        when(repository.findByRouteId("uuid-1")).thenReturn(Optional.of(route));

        service.deleteRoute("uuid-1", testUser);

        verify(repository, never()).delete(any());
    }

    @Test
    void getSavedRoute_existingRoute() {
        SavedRoute route = new SavedRoute();
        route.setRouteId("uuid-1");
        route.setName("Ruta Test");
        Point p = new Point();
        p.setAddress("Calle 1");
        p.setType(Point.TypePoint.ORIGIN);
        route.setPuntos(List.of(p));

        when(repository.findByRouteId("uuid-1")).thenReturn(Optional.of(route));

        Optional<SavedRouteDTO> result = service.getSavedRoute("uuid-1");

        assertTrue(result.isPresent());
        assertEquals("uuid-1", result.get().getRouteId());
        assertEquals("Ruta Test", result.get().getName());
        assertEquals(1, result.get().getPoints().size());
        assertEquals("ORIGIN", result.get().getPoints().get(0).getType());
    }

    @Test
    void getSavedRoute_routeNotFound() {
        lenient().when(repository.findByRouteId("uuid-1")).thenReturn(Optional.empty());

        Optional<SavedRouteDTO> result = service.getSavedRoute("uuid-1");

        assertTrue(result.isEmpty());
    }
    
    @Test
    void getAllSavedRoutes_userExists_returnsRoutes() {
        SavedRoute route1 = new SavedRoute();
        route1.setRouteId("uuid-1");
        route1.setName("Ruta 1");
        route1.setPuntos(new ArrayList<>());

        SavedRoute route2 = new SavedRoute();
        route2.setRouteId("uuid-2");
        route2.setName("Ruta 2");
        route2.setPuntos(new ArrayList<>());

        testUser.setSavedRoutes(List.of(route1, route2));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<List<SavedRouteDTO>> resultOpt = service.getAllSavedRoutes("test@example.com");

        assertTrue(resultOpt.isPresent());
        assertEquals(2, resultOpt.get().size());
        assertEquals("Ruta 1", resultOpt.get().get(0).getName());
        assertEquals("Ruta 2", resultOpt.get().get(1).getName());
    }

    @Test
    void getAllSavedRoutes_userNotFound_returnsEmpty() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        Optional<List<SavedRouteDTO>> resultOpt = service.getAllSavedRoutes("noexiste@example.com");

        assertTrue(resultOpt.isEmpty());
    }
    
    @Test
    void renameRoute_existingRoute_returnsRenamedDTO() {
        SavedRoute route = new SavedRoute();
        route.setRouteId("uuid-1");
        route.setName("Nombre Antiguo");

        Point p = new Point();
        p.setType(Point.TypePoint.ORIGIN);
        p.setAddress("Calle Test");
        route.setPuntos(List.of(p));

        SavedRouteDTO inputDto = new SavedRouteDTO();
        inputDto.setRouteId("uuid-1");

        when(repository.findByRouteId("uuid-1"))
                .thenReturn(Optional.of(route));

        when(repository.save(any(SavedRoute.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        SavedRouteDTO result = service.renameRoute("Nombre Nuevo", inputDto);

        assertNotNull(result);
        assertEquals("uuid-1", result.getRouteId());
        assertEquals("Nombre Nuevo", result.getName());
        assertEquals(1, result.getPoints().size());
        assertEquals("ORIGIN", result.getPoints().get(0).getType());

        verify(repository).findByRouteId("uuid-1");
        verify(repository).save(route);
    }

    // Nuevo test para executeRoute
    @Test
    void executeRoute_ok() throws Exception {
        SavedRoute route = new SavedRoute();
        route.setRouteId("uuid-1");
        route.setPolylineCoordsJson("[]");
        route.setLegCoordsJson("[]");
        route.setGasRadius(5L);
        route.setLanguage("es");

        when(repository.findByRouteId("uuid-1")).thenReturn(Optional.of(route));
        when(objectMapper.readValue(anyString(), any(com.fasterxml.jackson.core.type.TypeReference.class))).thenReturn(new ArrayList<>());
        when(gasolineraService.findGasStationsNearRoute(any(), any())).thenReturn(new ArrayList<>());
        when(weatherService.getWeatherForLegs(any(), anyString())).thenReturn(new ArrayList<>());

        Optional<FullRouteData> result = service.executeRoute("uuid-1");

        assertTrue(result.isPresent());
        assertNotNull(result.get().getPolylineCoords());
        assertNotNull(result.get().getGasStations());
    }
}