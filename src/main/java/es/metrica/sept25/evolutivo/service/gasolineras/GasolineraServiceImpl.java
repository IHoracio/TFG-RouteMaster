package es.metrica.sept25.evolutivo.service.gasolineras;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.repository.GasolineraRepository;

@Service
public class GasolineraServiceImpl implements GasolineraService {

	@Autowired
	GasolineraRepository gasolineraRepository;

	@Override
	public void save(Gasolinera gasolinera) {
		gasolineraRepository.save(gasolinera);
	}

	@Override
	public List<Gasolinera> getGasolineraList() {
		return gasolineraRepository.findAll();
	}
}