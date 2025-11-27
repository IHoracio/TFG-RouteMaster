package es.metrica.sept25.evolutiva_djlm.service;

import java.util.List;

import es.metrica.sept25.evolutiva_djlm.entity.Gasolinera;

public interface GasolineraService {
	void save(Gasolinera person);
    List<Gasolinera> getGasolineraList();
}