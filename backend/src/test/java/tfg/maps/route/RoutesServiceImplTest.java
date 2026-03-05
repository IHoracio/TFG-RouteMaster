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
import org.springframework.web.util.UriComponentsBuilder;

import tfg.domain.dto.maps.routes.*;
import tfg.domain.dto.weather.*;
import tfg.entity.gasolinera.Gasolinera;
import tfg.service.gasolineras.GasolineraService;
import tfg.service.maps.geocode.GeocodeService;
import tfg.service.maps.geocode.ReverseGeocodeService;
import tfg.service.maps.routes.RoutesServiceImpl;
import tfg.service.weather.WeatherService;

import org.springframework.web.client.RestTemplate;

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

    @Mock
    private GeocodeService geocodeService;

    @InjectMocks
    private RoutesServiceImpl service;

    private Coords coords1;
    private Coords coords2;

    @BeforeEach
    void setUp() {
        coords1 = new Coords(40.0, -3.0);
        coords2 = new Coords(41.0, -4.0);
    }

    /* =========================
       getDirections()
       ========================= */

    @Test
    void getDirections_success() {
        when(geocodeService.getCoordinates("Origin"))
                .thenReturn(Optional.of(coords1));
        when(geocodeService.getCoordinates("Destination"))
                .thenReturn(Optional.of(coords2));

        RouteGroup rg = simpleRouteGroup();
        when(restTemplate.getForObject(anyString(), eq(RouteGroup.class)))
                .thenReturn(rg);

        Optional<RouteGroup> result = service.getDirections(
                "Origin", "Destination",
                List.of(), false, false,
                "es", false
        );

        assertTrue(result.isPresent());
    }

    @Test
    void getDirections_invalidOrigin_returnsEmpty() {
        when(geocodeService.getCoordinates("Origin"))
                .thenReturn(Optional.empty());
        when(geocodeService.getCoordinates("Destination"))
                .thenReturn(Optional.of(coords2));

        Optional<RouteGroup> result = service.getDirections(
                "Origin", "Destination",
                List.of(), false, false,
                "es", false
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void getDirections_invalidWaypoint_returnsEmpty() {
        when(geocodeService.getCoordinates("Origin"))
                .thenReturn(Optional.of(coords1));
        when(geocodeService.getCoordinates("Destination"))
                .thenReturn(Optional.of(coords2));
        when(geocodeService.getCoordinates("BadWaypoint"))
                .thenReturn(Optional.empty());

        Optional<RouteGroup> result = service.getDirections(
                "Origin", "Destination",
                List.of("BadWaypoint"),
                false, false,
                "es", false
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void getDirections_optimizeRoute_deletesLastLeg() {
        when(geocodeService.getCoordinates(anyString()))
                .thenReturn(Optional.of(coords1));

        Leg leg1 = new Leg();
        Leg leg2 = new Leg();

        Route route = new Route();
        route.setLegs(new ArrayList<>(List.of(leg1, leg2)));

        RouteGroup rg = new RouteGroup();
        rg.setRoutes(List.of(route));

        when(restTemplate.getForObject(anyString(), eq(RouteGroup.class)))
                .thenReturn(rg);

        Optional<RouteGroup> result = service.getDirections(
                "Origin", "Destination",
                List.of("Waypoint"),
                true, true,
                "es", false
        );

        assertEquals(1,
                result.get().getRoutes().get(0).getLegs().size());
    }

    /* =========================
       getUrl()
       ========================= */

    @Test
    void getUrl_buildsCorrectUrl() {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromUriString("http://test");

        String url = service.getUrl(
                List.of(new Coords(40, -3), new Coords(41, -4)),
                builder
        );

        assertTrue(url.contains("40"));
        assertTrue(url.contains("41"));
        assertTrue(url.contains("|"));
    }

    /* =========================
       extractRoutePoints / polyline
       ========================= */

    @Test
    void extractRoutePoints_validRouteGroup_returnsStartLocations() {

        // Step con startLocation
        Step step = new Step();
        Coords start = new Coords(40.0, -3.0);
        step.setStartLocation(start);

        // Leg con steps
        Leg leg = new Leg();
        leg.setSteps(List.of(step));

        // Route con legs
        Route route = new Route();
        route.setLegs(List.of(leg));

        // RouteGroup válido
        RouteGroup routeGroup = new RouteGroup();
        routeGroup.setRoutes(List.of(route));

        // Ejecutar
        List<Coords> result = service.extractRoutePoints(routeGroup);

        // Verificaciones
        assertEquals(1, result.size());
        assertEquals(40.0, result.get(0).getLat());
        assertEquals(-3.0, result.get(0).getLng());
    }
    
    @Test
    void extractRoutePoints_nullRouteGroup_returnsEmpty() {
        assertTrue(service.extractRoutePoints(null).isEmpty());
    }

    @Test
    void extractRoutePolylinePoints_null_returnsEmpty() {
        assertTrue(service.extractRoutePolylinePoints(null).isEmpty());
    }

    @Test
    void decodePolyline_ok() {
        String polyline = "}_ilFf|ys@_@hA";
        List<Coords> result = service.decodePolyline(polyline);

        assertEquals(2, result.size());
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
        weather.setDireccion("Madrid");
        weather.setLat(40.4168);
        weather.setLon(-3.7038);
        weather.setTimezone("Europe/Madrid");
        weather.setTimezoneOffset(3600);
        
        // Set up hourly weather data
        HourlyWeather hourly = new HourlyWeather();
        hourly.setDt(1772528400L);
        hourly.setTemp(20.0);
        hourly.setFeelsLike(18.5);
        hourly.setHumidity(65);
        hourly.setWindSpeed(5.5);
        hourly.setVisibility(10000);
        
        // Set up weather conditions
        EstadoCielo estado = new EstadoCielo();
        estado.setId(800);
        estado.setMain("Clear");
        estado.setDescription("Despejado");
        estado.setIcon("01d");
        hourly.setWeather(List.of(estado));
        
        weather.setHourly(List.of(hourly));
        
        // Set up alerts
        Alerta alerta = new Alerta();
        alerta.setSenderName("AEMET");
        alerta.setEvent("Wind warning");
        weather.setAlerts(List.of(alerta));

        when(weatherService.getWeather(anyDouble(), anyDouble(), anyString(), anyString()))
                .thenReturn(Optional.of(weather));

        List<CoordsWithWeather> result =
                service.getWeatherForRoute(rg, "es");

        assertEquals(2, result.size());
        assertEquals("Madrid", result.get(0).getAddress());
        assertFalse(result.get(0).getFeelsLike().isEmpty());
        assertFalse(result.get(0).getWindSpeed().isEmpty());
        assertFalse(result.get(0).getVisibility().isEmpty());
        assertFalse(result.get(0).getAlerts().isEmpty());
    }

    @Test
    void getWeatherForRoute_noWeather_returnsEmptyMaps() {

        RouteGroup rg = simpleRouteGroup();

        when(weatherService.getWeather(anyDouble(), anyDouble(), anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(reverseGeocodeService.getAddress(anyDouble(), anyDouble()))
                .thenReturn(Optional.of("Madrid"));

        List<CoordsWithWeather> result =
                service.getWeatherForRoute(rg, "es");

        assertEquals(2, result.size());
        assertTrue(result.get(0).getWeatherDescription().isEmpty());
        assertTrue(result.get(0).getTemperatures().isEmpty());
    }

    /* =========================
       getGasStationsCoordsForRoute()
       ========================= */

    @Test
    void getGasStationsCoordsForRoute_success() {

        Gasolinera gas = new Gasolinera();
        gas.setLatitud(40.0);
        gas.setLongitud(-3.0);

        when(gasolineraService.getGasolinerasInRadiusCoords(
                anyDouble(), anyDouble(), eq(10L)))
                .thenReturn(List.of(gas));

        RouteGroup rg = simpleRouteGroup();

        List<Gasolinera> result =
                service.getGasStationsCoordsForRoute(rg, 10L);

        assertEquals(1, result.size());
    }

    /* =========================
       getLegCoords()
       ========================= */

    @Test
    void getLegCoords_success() {

        Leg leg = new Leg();
        leg.setStartLocation(coords1);
        leg.setEndLocation(coords2);

        Route route = new Route();
        route.setLegs(List.of(leg));

        RouteGroup rg = new RouteGroup();
        rg.setRoutes(List.of(route));

        List<Coords> result = service.getLegCoords(rg);

        assertEquals(2, result.size());
    }

    /* =========================
       Helper
       ========================= */

    private RouteGroup simpleRouteGroup() {
        Polyline polyline = new Polyline();
        polyline.setPoints("}_ilFf|ys@_@hA");

        Step step = new Step();
        step.setPolyline(polyline);

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
