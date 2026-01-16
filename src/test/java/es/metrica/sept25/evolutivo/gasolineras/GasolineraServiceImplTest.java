package es.metrica.sept25.evolutivo.gasolineras;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;
import es.metrica.sept25.evolutivo.entity.gasolinera.Brand;
import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;
import es.metrica.sept25.evolutivo.repository.BrandRepository;
import es.metrica.sept25.evolutivo.repository.GasolineraRepository;
import es.metrica.sept25.evolutivo.service.gasolineras.GasolineraServiceImpl;
import es.metrica.sept25.evolutivo.service.gasolineras.MunicipioService;
import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeService;

@ExtendWith(MockitoExtension.class)
public class GasolineraServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MunicipioService municipioService;

    @Mock
    private GeocodeService geocodeService;

    @Mock
    private GasolineraRepository gasolineraRepository;

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private GasolineraServiceImpl service;

    @Test
    void getGasolineraForId_success_returnsGasolinera() {
        Gasolinera gas = new Gasolinera();
        gas.setIdEstacion(1L);
        gas.setMarca("Repsol");

        when(restTemplate.getForObject(anyString(), eq(Gasolinera.class))).thenReturn(gas);

        Optional<Gasolinera> result = service.getGasolineraForId(1L);

        assertTrue(result.isPresent());
        assertEquals("Repsol", result.get().getMarca());
    }

    @Test
    void getGasolineraForId_notFound_returnsEmpty() {
        when(restTemplate.getForObject(anyString(), eq(Gasolinera.class)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        Optional<Gasolinera> result = service.getGasolineraForId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getGasolinerasForMunicipio_found_returnsList() {
        Municipio mun = new Municipio();
        mun.setIdMunicipio(10L);
        mun.setNombreMunicipio("Madrid");

        when(municipioService.getMunicipioFromString("Madrid")).thenReturn(Optional.of(mun));

        Gasolinera g1 = new Gasolinera();
        g1.setMarca("Repsol");
        Gasolinera g2 = new Gasolinera();
        g2.setMarca("Cepsa");

        when(restTemplate.getForObject(anyString(), eq(Gasolinera[].class)))
                .thenReturn(new Gasolinera[]{g1, g2});

        List<Gasolinera> result = service.getGasolinerasForMunicipio("Madrid");

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(g -> g.getMarca().equals("Repsol")));
    }

    @Test
    void getGasolinerasForMunicipio_notFoundMunicipio_returnsEmpty() {
        when(municipioService.getMunicipioFromString("Unknown")).thenReturn(Optional.empty());

        List<Gasolinera> result = service.getGasolinerasForMunicipio("Unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void getGasolinerasInRadiusCoords_success_returnsList() {
        Gasolinera g = new Gasolinera();
        g.setMarca("Repsol");

        when(restTemplate.getForObject(anyString(), eq(Gasolinera[].class)))
                .thenReturn(new Gasolinera[]{g});

        List<Gasolinera> result = service.getGasolinerasInRadiusCoords(40.0, -3.0, 10L);

        assertEquals(1, result.size());
        assertEquals("Repsol", result.get(0).getMarca());
    }

    @Test
    void getGasolinerasInRadiusCoords_invalidRadius_returnsEmpty() {
        List<Gasolinera> result = service.getGasolinerasInRadiusCoords(40.0, -3.0, 0L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getGasolinerasInRadiusCoords_notFound_returnsEmpty() {
        when(restTemplate.getForObject(anyString(), eq(Gasolinera[].class)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        List<Gasolinera> result = service.getGasolinerasInRadiusCoords(40.0, -3.0, 10L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getGasolinerasInRadiusAddress_success_returnsList() {
        Coords coords = new Coords(40.0, -3.0);
        when(geocodeService.getCoordinates("Calle Falsa")).thenReturn(Optional.of(coords));

        Gasolinera g = new Gasolinera();
        g.setMarca("Repsol");

        when(restTemplate.getForObject(anyString(), eq(Gasolinera[].class)))
                .thenReturn(new Gasolinera[]{g});

        List<Gasolinera> result = service.getGasolinerasInRadiusAddress("Calle Falsa", 10L);

        assertEquals(1, result.size());
        assertEquals("Repsol", result.get(0).getMarca());
    }

    @Test
    void getGasolinerasInRadiusAddress_noCoords_returnsEmpty() {
        when(geocodeService.getCoordinates("Unknown")).thenReturn(Optional.empty());

        List<Gasolinera> result = service.getGasolinerasInRadiusAddress("Unknown", 10L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getGasolinerasInRadiusAddress_invalidRadius_returnsEmpty() {
        Coords coords = new Coords(40.0, -3.0);
        when(geocodeService.getCoordinates("Calle Falsa")).thenReturn(Optional.of(coords));

        List<Gasolinera> result = service.getGasolinerasInRadiusAddress("Calle Falsa", 0L);

        assertTrue(result.isEmpty());
    }
    
    @Test
    void syncBrandsFromGasStations_collectsBrandsAndSavesThem() {

        Municipio mun = new Municipio();
        mun.setNombreMunicipio("Madrid");

        when(municipioService.getMunicipios()).thenReturn(List.of(mun));

        Gasolinera g1 = new Gasolinera();
        g1.setMarca("Repsol");

        Gasolinera g2 = new Gasolinera();
        g2.setMarca("Cepsa");

        GasolineraServiceImpl spyService = spy(service);

        doReturn(List.of(g1, g2))
                .when(spyService)
                .getGasolinerasForMunicipio("Madrid");

        when(brandRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        when(brandRepository.save(any(Brand.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        spyService.syncBrandsFromGasStations();

        verify(brandRepository, times(2)).save(any(Brand.class));
    }

    @Test
    void getMarcasFromAllGasolineras_returnsSortedNames() {
        Brand b1 = new Brand();
        b1.setName("Cepsa");
        Brand b2 = new Brand();
        b2.setName("Repsol");

        when(brandRepository.findAll()).thenReturn(List.of(b1, b2));

        List<String> marcas = service.getMarcasFromAllGasolineras();

        assertEquals(2, marcas.size());
        assertEquals("Cepsa", marcas.get(0));
        assertEquals("Repsol", marcas.get(1));
    }

    @Test
    void syncBrands_savesNewBrand() {
        Brand savedBrand = new Brand();
        savedBrand.setName("Shell");

        when(brandRepository.findByName("Shell")).thenReturn(Optional.empty());
        when(brandRepository.save(any(Brand.class))).thenReturn(savedBrand);

        service.syncBrands(Set.of("Shell"));

        verify(brandRepository).save(any(Brand.class));
    }
    
    @Test
    void initBrandsAtStartup_whenNoBrands_triggersSyncAndFetch() {

        // Spy para interceptar llamadas internas
        GasolineraServiceImpl spyService = spy(service);

        when(brandRepository.count()).thenReturn(0L);

        doNothing().when(spyService).syncBrandsFromGasStations();
        doReturn(List.of("Repsol", "Cepsa"))
                .when(spyService)
                .getMarcasFromAllGasolineras();

        // Ejecutar
        spyService.initBrandsAtStartup();

        // Verificar
        verify(spyService).syncBrandsFromGasStations();
        verify(spyService).getMarcasFromAllGasolineras();
    }
    
    @Test
    void initBrandsAtStartup_whenBrandsExist_doesNothing() {

        GasolineraServiceImpl spyService = spy(service);

        when(brandRepository.count()).thenReturn(5L);

        // Ejecutar
        spyService.initBrandsAtStartup();

        // Verificar que NO se llaman
        verify(spyService, never()).syncBrandsFromGasStations();
        verify(spyService, never()).getMarcasFromAllGasolineras();
    }


}
