package es.metrica.sept25.evolutivo.route;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.RouteGroup;
import es.metrica.sept25.evolutivo.domain.dto.weather.Dia;
import es.metrica.sept25.evolutivo.domain.dto.weather.EstadoCielo;
import es.metrica.sept25.evolutivo.domain.dto.weather.Prediccion;
import es.metrica.sept25.evolutivo.domain.dto.weather.Temperatura;
import es.metrica.sept25.evolutivo.domain.dto.weather.Weather;
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
            RoutesServiceImpl.VehicleEmissionType.DIESEL
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
            RoutesServiceImpl.VehicleEmissionType.DIESEL
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
        RouteGroup rg = mock(RouteGroup.class);
        Coords sampleCoord = new Coords(40, -3);

        // Mockeo de getSampledRoutePoints
        RoutesServiceImpl spyService = spy(service);
        doReturn(List.of(sampleCoord)).when(spyService).getSampledRoutePoints(rg);

        // Mock INE
        when(ineService.getCodigoINE(40, -3)).thenReturn(Optional.of("28079"));

        // Mock ReverseGeocode
        when(reverseGeocodeService.getAddress(40, -3)).thenReturn(Optional.of("Madrid"));
        // Mock Weather
        Weather w = new Weather();
        Dia d = new Dia();
        d.setEstadoCielo(List.of(new EstadoCielo(0, "Despejado")));
        d.setTemperatura(List.of(new Temperatura(0, 20.0)));
        w.setPrediccion(new Prediccion());
        w.getPrediccion().setDia(List.of(d));

        when(weatherService.getWeather("28079")).thenReturn(Optional.of(w));

        List<?> result = spyService.getWeatherForRoute(rg);

        assertFalse(result.isEmpty());
    }
}
