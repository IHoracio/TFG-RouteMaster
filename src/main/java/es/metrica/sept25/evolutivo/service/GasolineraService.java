package es.metrica.sept25.evolutivo.service;

import java.util.List;

import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;

public interface GasolineraService {
	void save(Gasolinera person);
    List<Gasolinera> getGasolineraList();
}