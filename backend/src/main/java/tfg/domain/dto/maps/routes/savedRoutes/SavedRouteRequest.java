package tfg.domain.dto.maps.routes.savedRoutes;

import java.util.List;

import tfg.domain.dto.maps.routes.Coords;

public class SavedRouteRequest {
	
	private String name;
    private List<PointDTO> puntosDTO;
    
    private List<Coords> polylineCoords;
    private List<Coords> legCoords;
    
    private Long gasRadius;
    private String language;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<PointDTO> getPuntosDTO() {
		return puntosDTO;
	}
	public void setPuntosDTO(List<PointDTO> puntosDTO) {
		this.puntosDTO = puntosDTO;
	}
	public List<Coords> getPolylineCoords() {
		return polylineCoords;
	}
	public void setPolylineCoords(List<Coords> polylineCoords) {
		this.polylineCoords = polylineCoords;
	}
	public List<Coords> getLegCoords() {
		return legCoords;
	}
	public void setLegCoords(List<Coords> legCoords) {
		this.legCoords = legCoords;
	}
	public Long getGasRadius() {
		return gasRadius;
	}
	public void setGasRadius(Long gasRadius) {
		this.gasRadius = gasRadius;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}

}
