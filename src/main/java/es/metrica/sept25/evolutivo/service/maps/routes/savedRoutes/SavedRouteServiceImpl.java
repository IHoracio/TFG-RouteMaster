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
	public SavedRouteDTO saveRoute(String name, 
	        List<PointDTO> puntosDTO, 
	        User user,
	        boolean optimizeWaypoints,
	        boolean optimizeRoute,
	        String language) {
		SavedRoute route = new SavedRoute();
		route.setName(name);
		route.setUser(user);
		route.setOptimizeWaypoints(optimizeWaypoints);
	    route.setOptimizeRoute(optimizeRoute);
	    route.setLanguage(language);
		
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
		dto.setRouteId(saved.getRouteId());
		dto.setName(saved.getName());
		dto.setPoints(puntosDTO);
		return dto;
	}

	@Override
	@Transactional
	public void deleteRoute(Long id, User user) {

		Optional<SavedRoute> route = repository.findById(id);
		
		if (route.isEmpty()) {
			System.err.println("No existe una ruta con el ID " + id.toString());
			return;
		}

		if (!route.get().getUser().getId().equals(user.getId())) {
			System.err.println("El usuario " + user.toString() + 
					"intentó borrar una ruta con ID: " + id.toString()
					+ " que no era suya.");
			return;
		}

		repository.delete(route.get());
	}

	public Optional<SavedRouteDTO> getSavedRoute(Long id) {
		Optional<SavedRoute> route = repository.findById(id);

		return route.isPresent() ? Optional.of(mapToDTO(route.get())) : Optional.empty();
	}

	private SavedRouteDTO mapToDTO(SavedRoute route) {
		SavedRouteDTO dto = new SavedRouteDTO();
		dto.setRouteId(route.getRouteId());
		dto.setName(route.getName());

		dto.setPoints(route.getPuntos().stream().map(p -> {
			PointDTO pdto = new PointDTO();
			pdto.setType(p.getType().name());
			pdto.setAddress(p.getAddress());
			return pdto;
		}).toList());

		return dto;
	}
}
