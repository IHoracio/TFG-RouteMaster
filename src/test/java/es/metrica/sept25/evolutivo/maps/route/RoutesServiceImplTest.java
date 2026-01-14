package es.metrica.sept25.evolutivo.maps.route;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.CoordsWithWeather;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Distance;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Leg;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Polyline;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Route;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.RouteGroup;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Step;
import es.metrica.sept25.evolutivo.domain.dto.weather.Dia;
import es.metrica.sept25.evolutivo.domain.dto.weather.EstadoCielo;
import es.metrica.sept25.evolutivo.domain.dto.weather.Prediccion;
import es.metrica.sept25.evolutivo.domain.dto.weather.Temperatura;
import es.metrica.sept25.evolutivo.domain.dto.weather.Weather;
import es.metrica.sept25.evolutivo.enums.EmissionType;
import es.metrica.sept25.evolutivo.service.gasolineras.GasolineraService;
import es.metrica.sept25.evolutivo.service.ine.INEService;
import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeService;
import es.metrica.sept25.evolutivo.service.maps.geocode.ReverseGeocodeService;
import es.metrica.sept25.evolutivo.service.maps.routes.RoutesServiceImpl;
import es.metrica.sept25.evolutivo.service.weather.WeatherService;

@ExtendWith(MockitoExtension.class)
class RoutesServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private INEService ineService;

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
        // Configurar API key
//        service.API_KEY_GOOGLE = "TEST_KEY";
    }

    /* =========================
       getDirections() basic test
       ========================= */
    @Test
    void getDirections_success() {
        // Mock coordenadas válidas
        when(geocodeService.getCoordinates("Origin")).thenReturn(Optional.of(coords1));
        when(geocodeService.getCoordinates("Destination")).thenReturn(Optional.of(coords2));

        RouteGroup fakeGroup = new RouteGroup();
        when(restTemplate.getForObject(anyString(), eq(RouteGroup.class)))
                .thenReturn(fakeGroup);

        Optional<RouteGroup> result = service.getDirections(
            "Origin", "Destination", List.of(), false, false, "es", false,
            EmissionType.C
        );

        assertTrue(result.isPresent());
        assertEquals(fakeGroup, result.get());
    }

    @Test
    void getDirections_invalidOrigin() {
        when(geocodeService.getCoordinates("Origin")).thenReturn(Optional.empty());
        when(geocodeService.getCoordinates("Destination")).thenReturn(Optional.of(coords2));

        Optional<RouteGroup> result = service.getDirections(
            "Origin", "Destination", List.of(), false, false, "es", false,
            EmissionType.C
        );

        assertTrue(result.isEmpty());
    }

    /* =========================
       decodePolyline()
       ========================= */
    @Test
    void decodePolyline_ok() {
        // Polyline simple con dos coordenadas
        String polyline = "}_ilFf|ys@_@hA";
        List<Coords> result = service.decodePolyline(polyline);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size()); // 2 coordenadas decodificadas
    }

    /* =========================
       extractRoutePoints() with null group
       ========================= */
    @Test
    void extractRoutePoints_nullRouteGroup() {
        List<Coords> result = service.extractRoutePoints(null);
        assertTrue(result.isEmpty());
    }

    /* =========================
       getWeatherForRoute()
       ========================= */
    @Test
    void getWeatherForRoute_success() {
    	Polyline polyline = new Polyline();
        polyline.setPoints("}_ilFjk~uOwHJy@P");

        Step step = new Step();
        step.setPolyline(polyline);

        Distance distance = new Distance();
        distance.setValue(10_000L);

        Leg leg = new Leg();
        leg.setSteps(List.of(step));
        leg.setDistance(distance);
        leg.setStartLocation(new Coords(40, -3));
        leg.setEndLocation(new Coords(40.1, -3.1));

        Route route = new Route();
        route.setLegs(List.of(leg));

        RouteGroup rg = new RouteGroup();
        rg.setRoutes(List.of(route));

        when(ineService.getCodigoINE(anyDouble(), anyDouble()))
                .thenReturn(Optional.of("28079"));

        when(reverseGeocodeService.getAddress(anyDouble(), anyDouble()))
                .thenReturn(Optional.of("Madrid"));

        Weather weather = new Weather();
        Dia dia = new Dia();
        dia.setEstadoCielo(List.of(new EstadoCielo(0, "Despejado")));
        dia.setTemperatura(List.of(new Temperatura(0, 20.0)));

        Prediccion pred = new Prediccion();
        pred.setDia(List.of(dia));
        weather.setPrediccion(pred);

        when(weatherService.getWeather("28079"))
                .thenReturn(Optional.of(weather));

        List<CoordsWithWeather> result =
                service.getWeatherForRoute(rg);

        assertEquals(1, result.size());

        CoordsWithWeather cw = result.get(0);
        assertEquals("Madrid", cw.getAddress());
        assertEquals("Despejado", cw.getWeatherDescription().get(0));
        assertEquals(20.0, cw.getTemperatures().get(0));
    }
}
