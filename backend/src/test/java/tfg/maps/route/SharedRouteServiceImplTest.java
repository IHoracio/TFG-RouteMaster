package tfg.maps.route;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import tfg.domain.dto.maps.routes.FullRouteData;
import tfg.domain.dto.maps.routes.sharedRoutes.ShareRouteRequest;
import tfg.entity.maps.routes.SharedRoute;
import tfg.repository.SharedRouteRepository;
import tfg.service.gasolineras.GasolineraService;
import tfg.service.maps.routes.sharedRoutes.SharedRouteServiceImpl;
import tfg.service.weather.WeatherService;

@ExtendWith(MockitoExtension.class)
public class SharedRouteServiceImplTest {

    @Mock
    private SharedRouteRepository sharedRouteRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private GasolineraService gasolineraService;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private SharedRouteServiceImpl service;

    @Test
    void generateShareToken_ok() throws Exception {
        ShareRouteRequest request = new ShareRouteRequest();
        request.setPolylineCoords(new ArrayList<>());
        request.setLegCoords(new ArrayList<>());
        request.setGasRadius(5L);
        request.setLang("es");

        when(objectMapper.writeValueAsString(any())).thenReturn("[]");

        String token = service.generateShareToken(request);

        assertNotNull(token);
        // Comprobamos que es un UUID válido
        assertTrue(token.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"));
        
        verify(sharedRouteRepository).save(any(SharedRoute.class));
    }

    @Test
    void getSharedRouteData_existingToken_ok() throws Exception {
        SharedRoute sharedRoute = new SharedRoute("uuid-123", "[]", "[]", 5L, "es");

        when(sharedRouteRepository.findById("uuid-123")).thenReturn(Optional.of(sharedRoute));
        when(objectMapper.readValue(anyString(), any(com.fasterxml.jackson.core.type.TypeReference.class))).thenReturn(new ArrayList<>());
        when(gasolineraService.findGasStationsNearRoute(any(), any())).thenReturn(new ArrayList<>());
        when(weatherService.getWeatherForLegs(any(), anyString())).thenReturn(new ArrayList<>());

        Optional<FullRouteData> result = service.getSharedRouteData("uuid-123");

        assertTrue(result.isPresent());
        assertNotNull(result.get().getPolylineCoords());
        assertNotNull(result.get().getLegCoords());
        assertNotNull(result.get().getGasStations());
        assertNotNull(result.get().getWeatherData());
    }

    @Test
    void getSharedRouteData_tokenNotFound_returnsEmpty() {
        when(sharedRouteRepository.findById("invalid-token")).thenReturn(Optional.empty());

        Optional<FullRouteData> result = service.getSharedRouteData("invalid-token");

        assertTrue(result.isEmpty());
    }
}