package tfg.service.gasolineras;

import java.util.List;
import java.util.Optional;

import tfg.domain.dto.maps.routes.Coords;
import tfg.entity.gasolinera.Gasolinera;

public interface GasolineraService {
	List<Gasolinera> getGasolinerasForMunicipio(String municipio);

	Optional<Gasolinera> getGasolineraForId(Long idEstacion);

	List<Gasolinera> getGasolinerasInRadiusCoords(Double latitud, Double longitud, Long radio);

	List<Gasolinera> getGasolinerasInRadiusPlace(String place, Long radius);
	
	List<Gasolinera> findGasStationsNearRoute(List<Coords> polylineCoords, Long radius);

	List<String> getMarcasFromAllGasolineras();

}