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
import org.springframework.web.client.RestTemplate;

import tfg.domain.dto.maps.routes.*;
import tfg.domain.dto.maps.routes.autocomplete.PlaceSelection;
import tfg.domain.dto.weather.*;
import tfg.entity.gasolinera.Gasolinera;
import tfg.service.gasolineras.GasolineraService;
import tfg.service.maps.geocode.ReverseGeocodeService;
import tfg.service.maps.routes.RoutesServiceImpl;
import tfg.service.weather.WeatherService;

@ExtendWith(MockitoExtension.class)
class RoutesServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private GasolineraService gasolineraService;

    @Mock
    private WeatherService weatherService;

    @Mock
    private ReverseGeocodeService reverseGeocodeService;

    @InjectMocks
    private RoutesServiceImpl service;

    private PlaceSelection origin;
    private PlaceSelection destination;
    private Coords coords1;
    private Coords coords2;

    @BeforeEach
    void setUp() {
        coords1 = new Coords(40.0, -3.0);
        coords2 = new Coords(41.0, -4.0);
        
        // Inicializamos los nuevos objetos PlaceSelection
        origin = new PlaceSelection("ID_ORIGIN", "Calle Origen, 1", "Origen", coords1);
        destination = new PlaceSelection("ID_DEST", "Calle Destino, 2", "Destino", coords2);
    }

    /* =========================
       getDirections()
       ========================= */

    @Test
    void getDirections_success() {
        RouteGroup rg = simpleRouteGroup();
        rg.setStatus("OK");

        // Mockeamos la llamada a Google Maps API vía RestTemplate
        when(restTemplate.getForObject(anyString(), eq(RouteGroup.class)))
                .thenReturn(rg);

        Optional<RouteGroup> result = service.getDirections(
                origin, destination,
                List.of(), false, false,
                "es", false
        );

        assertTrue(result.isPresent());
        assertEquals("OK", result.get().getStatus());
    }

    @Test
    void getDirections_nullInputs_returnsEmpty() {
        // Test de validación de nulos al inicio del método
        Optional<RouteGroup> result = service.getDirections(
                null, destination,
                List.of(), false, false,
                "es", false
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void getDirections_googleErrorStatus_returnsEmpty() {
        RouteGroup rg = new RouteGroup();
        rg.setStatus("NOT_FOUND");

        when(restTemplate.getForObject(anyString(), eq(RouteGroup.class)))
                .thenReturn(rg);

        Optional<RouteGroup> result = service.getDirections(
                origin, destination,
                List.of(), false, false,
                "es", false
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void getDirections_optimizeRoute_deletesLastLeg() {
        Leg leg1 = new Leg();
        Leg leg2 = new Leg();

        Route route = new Route();
        route.setLegs(new ArrayList<>(List.of(leg1, leg2)));

        RouteGroup rg = new RouteGroup();
        rg.setRoutes(List.of(route));
        rg.setStatus("OK");

        when(restTemplate.getForObject(anyString(), eq(RouteGroup.class)))
                .thenReturn(rg);

        // Activamos optimizeRoute (Round Trip)
        Optional<RouteGroup> result = service.getDirections(
                origin, destination,
                List.of(), false, true,
                "es", false
        );

        // Verificamos que se eliminó el último leg (el de vuelta al origen)
        assertEquals(1, result.get().getRoutes().get(0).getLegs().size());
    }

    /* =========================
       getFullRouteData()
       ========================= */

    @Test
    void getFullRouteData_success() {
        RouteGroup rg = simpleRouteGroup();
        rg.setStatus("OK");

        when(restTemplate.getForObject(anyString(), eq(RouteGroup.class)))
                .thenReturn(rg);
        
        // Mocks para servicios internos que componen el FullRouteData
        when(reverseGeocodeService.getAddress(anyDouble(), anyDouble()))
                .thenReturn(Optional.of("Madrid"));
        when(weatherService.getWeather(anyDouble(), anyDouble(), anyString(), anyString()))
                .thenReturn(Optional.of(new Weather()));

        Optional<FullRouteData> result = service.getFullRouteData(
                origin, destination, List.of(), 
                false, false, "es", false, 10L);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getPolylineCoords());
        assertNotNull(result.get().getWeatherData());
    }

    /* =========================
       getWeatherForRoute()
       ========================= */

    @Test
    void getWeatherForRoute_success() {
        RouteGroup rg = simpleRouteGroup();

        when(reverseGeocodeService.getAddress(anyDouble(), anyDouble()))
                .thenReturn(Optional.of("Madrid"));

        Weather weather = new Weather();
        weather.setHourly(List.of(new HourlyWeather())); // Evita NPE si hay lógica de mapeo

        when(weatherService.getWeather(anyDouble(), anyDouble(), anyString(), anyString()))
                .thenReturn(Optional.of(weather));

        List<CoordsWithWeather> result = service.getWeatherForRoute(rg, "es");

        assertEquals(2, result.size()); // start y end location del leg
        assertEquals("Madrid", result.get(0).getAddress());
    }

    /* =========================
       Helper
       ========================= */

    private RouteGroup simpleRouteGroup() {
        Polyline polyline = new Polyline();
        polyline.setPoints("}_ilFf|ys@_@hA");

        Step step = new Step();
        step.setPolyline(polyline);
        step.setStartLocation(coords1);

        Distance distance = new Distance();
        distance.setValue(10_000L);

        Leg leg = new Leg();
        leg.setSteps(List.of(step));
        leg.setDistance(distance);
        leg.setStartLocation(coords1);
        leg.setEndLocation(coords2);

        Route route = new Route();
        route.setLegs(List.of(leg));

        RouteGroup rg = new RouteGroup();
        rg.setRoutes(List.of(route));
        return rg;
    }
}