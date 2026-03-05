package tfg.service.gasolineras;

import java.util.List;
import java.util.Optional;

import tfg.entity.gasolinera.Municipio;
import tfg.entity.gasolinera.Provincia;

public interface ProvinciaService {
	void save(Provincia provincia);

	List<Provincia> getProvincias();

	Optional<Provincia> getProvinciaById(Long id);
	
	

	Optional<Provincia> getProvinciaForMunicipio(Municipio mun);
}
