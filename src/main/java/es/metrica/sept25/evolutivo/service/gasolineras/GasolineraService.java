package es.metrica.sept25.evolutivo.service.gasolineras;

import java.util.List;
import java.util.Optional;

import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;

public interface GasolineraService {
	List<Gasolinera> getGasolinerasForMunicipio(String municipio);

	Optional<Gasolinera> getGasolineraForId(Long idEstacion);

	List<Gasolinera> getGasolinerasInRadius(Double latitud, Double longitud, Long radio);
}