package es.metrica.sept25.evolutiva_djlm.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.metrica.sept25.evolutiva_djlm.entity.Gasolinera;
import es.metrica.sept25.evolutiva_djlm.repository.GasolineraRepository;

@Service
public class GasolineraServiceImpl implements GasolineraService {
	@Autowired
	GasolineraRepository personRepo;

	@Override
	public void save(Gasolinera person) {
	personRepo.save(person);
}
	@Override
	public List<Gasolinera> getGasolineraList() {
	return personRepo.findAll();
	}
}