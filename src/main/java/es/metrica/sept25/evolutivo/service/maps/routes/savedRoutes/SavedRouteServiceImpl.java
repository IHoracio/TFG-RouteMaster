package es.metrica.sept25.evolutivo.service.maps.routes.savedRoutes;

import java.time.LocalDateTime;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

		log.info("[route-save-service] [" + LocalDateTime.now().toString() + "] "
				+ "Successfully saved route with name: " + name + ".");
		return dto;
	}

	@Override
	@Transactional
	public void deleteRoute(Long id, User user) {
		log.info("[route-save-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to delete saved route with ID: " + id + 
				" for user with email: " + user.getEmail() + " .");
		Optional<SavedRoute> route = repository.findById(id);
		
		if (route.isEmpty()) {
			log.error("[route-save-service] [" + LocalDateTime.now().toString() + "] "
				+ "Couldn't find route with ID: " + id + ".");
			return;
		}

		if (!route.get().getUser().getId().equals(user.getId())) {
			log.error("[route-save-service] [" + LocalDateTime.now().toString() + "] "
				+ "The user " + user.getEmail() + " tried to delete the route "
						+ "with ID: " + id + " , which isn't theirs.");
			return;
		}

		repository.delete(route.get());
	}

	public Optional<SavedRouteDTO> getSavedRoute(Long id) {
		log.info("[route-save-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to retrieve saved route with ID: " + id + ".");
		Optional<SavedRoute> route = repository.findById(id);
		
		if (route.isPresent()) {
			log.info("[route-save-service] [" + LocalDateTime.now().toString() + "] "
					+ "Retrieved saved route with ID: " + id + ".");
			return Optional.of(mapToDTO(route.get()))	;
		} else {
			log.warn("[route-save-service] [" + LocalDateTime.now().toString() + "] "
					+ "Couldn't retrieve a saved route for ID: " + id + ".");
			return Optional.empty();
		}
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
