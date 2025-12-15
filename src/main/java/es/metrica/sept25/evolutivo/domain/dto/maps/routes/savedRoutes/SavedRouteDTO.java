package es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes;

import java.util.List;

public class SavedRouteDTO {

	private Long id;
    private String name;
    private List<PointDTO> points;
    private RoutePreferencesDTO preferences;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<PointDTO> getPuntos() {
		return points;
	}
	public void setPuntos(List<PointDTO> puntos) {
		this.points = puntos;
	}
	public RoutePreferencesDTO getPreferences() {
		return preferences;
	}
	public void setPreferences(RoutePreferencesDTO preferences) {
		this.preferences = preferences;
	}
    
    
}
