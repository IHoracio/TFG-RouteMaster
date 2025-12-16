package es.metrica.sept25.evolutivo.service.maps.routes.savedRoutes;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes.PointDTO;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes.SavedRouteDTO;
import es.metrica.sept25.evolutivo.entity.maps.routes.Point;
import es.metrica.sept25.evolutivo.entity.maps.routes.SavedRoute;
import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.repository.SavedRouteRepository;

@Service
public class SavedRouteServiceImpl implements SavedRouteService {

	@Autowired
	private SavedRouteRepository repository;

	@Override
	@Transactional
	public SavedRouteDTO saveRoute(String name, List<PointDTO> puntosDTO, User user) {
		SavedRoute route = new SavedRoute();
		route.setName(name);
		route.setUser(user);

		List<Point> puntos = puntosDTO.stream().map(dto -> {
			Point point = new Point();
			point.setAddress(dto.getAddress());
			point.setType(Point.TypePoint.valueOf(dto.getType().toUpperCase()));
			point.setSavedRoute(route);
			return point;
		}).toList();

		route.setPuntos(puntos);

		SavedRoute saved = repository.save(route);

		SavedRouteDTO dto = new SavedRouteDTO();
		dto.setId(saved.getId());
		dto.setName(saved.getName());
		dto.setPuntos(puntosDTO);
		return dto;
	}

	@Override
	@Transactional
	public void deleteRoute(Long id, User user) {

		SavedRoute route = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

		if (!route.getUser().getId().equals(user.getId())) {
			throw new RuntimeException("No puedes borrar una ruta que no es tuya");
		}

		repository.delete(route);
	}

	public Optional<SavedRouteDTO> getSavedRoute(Long id) {
		Optional<SavedRoute> route = repository.findById(id);

		return route.isPresent() ? Optional.of(mapToDTO(route.get())) : Optional.empty();
	}

	private SavedRouteDTO mapToDTO(SavedRoute route) {
		SavedRouteDTO dto = new SavedRouteDTO();
		dto.setId(route.getId());
		dto.setName(route.getName());

		dto.setPuntos(route.getPuntos().stream().map(p -> {
			PointDTO pdto = new PointDTO();
			pdto.setType(p.getType().name());
			pdto.setAddress(p.getAddress());
			return pdto;
		}).toList());

		return dto;
	}
}
