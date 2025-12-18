package es.metrica.sept25.evolutivo.service.gasolineras;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import es.metrica.sept25.evolutivo.repository.ProvinciaRepository;

@SpringBootTest
@AutoConfigureMockRestServiceServer
@ExtendWith(MockitoExtension.class)
public class ProvinciaServiceTests {

	@Mock
	private ProvinciaRepository provinciaRepository;

	@Mock
	private RestTemplate restTemplate;

	@Autowired
	@InjectMocks
	private ProvinciaServiceImpl provinciaService;

//	private static final String API_URL = "https://api.precioil.es/provincias";

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testProvinciaGetAll() {
		System.out.println(provinciaRepository);
		System.out.println(restTemplate);
		System.out.println(provinciaService);
		assertThat(provinciaRepository.count() == 0).isEqualTo(true);
		provinciaService.getProvincias();
		assertThat(provinciaRepository.count()).isEqualTo(50);
	}
}
