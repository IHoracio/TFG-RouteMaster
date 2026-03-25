package tfg.maps.route;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

import tfg.domain.dto.maps.routes.Coords;
import tfg.domain.dto.maps.routes.FullRouteData;
import tfg.domain.dto.maps.routes.autocomplete.PlaceSelection;
import tfg.domain.dto.maps.routes.savedRoutes.PointDTO;
import tfg.domain.dto.maps.routes.savedRoutes.SavedRouteDTO;
import tfg.domain.dto.maps.routes.savedRoutes.SavedRouteRequest;
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
    private PlaceSelection testPlace;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");

        testPlace = new PlaceSelection(
            "PLACE_123", 
            "Calle Mayor 1, Madrid", 
            "Sol", 
            new Coords(40.41, -3.70)
        );
    }

    @Test
    void saveRoute_success_withPlaceSelection() throws Exception {
        SavedRouteRequest request = new SavedRouteRequest();
        request.setName("Ruta Vacaciones");
        request.setPuntosDTO(List.of(new PointDTO("ORIGIN", testPlace)));
        request.setPolylineCoords(new ArrayList<>());
        request.setLegCoords(new ArrayList<>());

        SavedRoute entity = new SavedRoute();
        entity.setRouteId("UUID-AUTO");
        entity.setName("Ruta Vacaciones");
        entity.setPuntos(new ArrayList<>()); // En la vida real el service los añade

        when(objectMapper.writeValueAsString(any())).thenReturn("[]");
        when(repository.save(any(SavedRoute.class))).thenReturn(entity);

        SavedRouteDTO result = service.saveRoute(request, testUser);

        assertNotNull(result);
        assertEquals("Ruta Vacaciones", result.getName());
        verify(repository).save(argThat(route -> 
            route.getPuntos().get(0).getPlaceSelection().placeId().equals("PLACE_123")
        ));
    }

    @Test
    void executeRoute_hydratesDataSuccessfully() throws Exception {
        SavedRoute route = new SavedRoute();
        route.setRouteId("ID_EXT");
        route.setPolylineCoordsJson("[]");
        route.setLegCoordsJson("[]");
        route.setGasRadius(5L);
        route.setLanguage("es");

        when(repository.findByRouteId("ID_EXT")).thenReturn(Optional.of(route));
        // Mocking Jackson para devolver listas vacías
        when(objectMapper.readValue(anyString(), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenReturn(new ArrayList<>());

        Optional<FullRouteData> result = service.executeRoute("ID_EXT");

        assertTrue(result.isPresent());
        verify(gasolineraService).findGasStationsNearRoute(anyList(), eq(5L));
        verify(weatherService).getWeatherForLegs(anyList(), eq("es"));
    }

    @Test
    void deleteRoute_securityCheck_failsIfDifferentUser() {
        User unauthorizedUser = new User();
        unauthorizedUser.setId(99L);

        SavedRoute route = new SavedRoute();
        route.setUser(testUser); // La ruta es del testUser (ID 1)

        when(repository.findByRouteId("ID_1")).thenReturn(Optional.of(route));

        service.deleteRoute("ID_1", unauthorizedUser);

        // Verificamos que NUNCA se llamó al delete porque los IDs no coinciden
        verify(repository, never()).delete(any());
    }
}