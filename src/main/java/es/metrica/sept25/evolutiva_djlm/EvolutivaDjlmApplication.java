package es.metrica.sept25.evolutiva_djlm;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import es.metrica.sept25.evolutiva_djlm.entity.Gasolinera;
import es.metrica.sept25.evolutiva_djlm.service.GasolineraService;

@SpringBootApplication
public class EvolutivaDjlmApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvolutivaDjlmApplication.class, args);
	}
	@Bean
	public CommandLineRunner demo(GasolineraService personService) {
		return (args) -> {
			// save few person
			Gasolinera gasoli1 = new Gasolinera();
			Gasolinera gasoli2 = new Gasolinera();

			personService.save(gasoli1);
			personService.save(gasoli2);

			// fetch all person
			System.out.println("-----List of Gasolineras------");
			for (Gasolinera person : personService.getGasolineraList()) {
				System.out.println("Gasolinera Detail:" + person);
			}
		};
	}

}
