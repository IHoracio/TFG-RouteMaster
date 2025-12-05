package es.metrica.sept25.evolutivo.service.gasolineras;

import java.util.List;
import java.util.Optional;

import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;

public interface GasolineraService {
	void save(Gasolinera person);

	List<Gasolinera> getGasolinerasForMunicipio(String municipio);

	Optional<Gasolinera> getGasolineraForId(Long idEstacion);
}