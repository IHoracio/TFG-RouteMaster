package es.metrica.sept25.evolutivo.service;

import java.util.List;
import java.util.Optional;

import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;
import es.metrica.sept25.evolutivo.entity.gasolinera.Provincia;

public interface MunicipioService {
	List<Municipio> getMunicipios();
	Optional<Provincia> getProvinciaForMunicipio(Municipio mun);
}
