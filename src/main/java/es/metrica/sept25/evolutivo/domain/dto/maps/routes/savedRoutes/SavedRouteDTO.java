package es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes;

import java.util.List;
import java.util.UUID;

public class SavedRouteDTO {

	private UUID routeId;
	private String name;
	private List<PointDTO> points;
	private RoutePreferencesDTO preferences;

	public UUID getRouteId() {
		return routeId;
	}

	public void setRouteId(UUID routeId) {
		this.routeId = routeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PointDTO> getPoints() {
		return points;
	}

	public void setPoints(List<PointDTO> points) {
		this.points = points;
	}

	public RoutePreferencesDTO getPreferences() {
		return preferences;
	}

	public void setPreferences(RoutePreferencesDTO preferences) {
		this.preferences = preferences;
	}
}
