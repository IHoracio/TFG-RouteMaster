package tfg.domain.dto.maps.routes.sharedRoutes;

import java.util.List;

import tfg.domain.dto.maps.routes.Coords;

public class ShareRouteRequest {
	private List<Coords> polylineCoords;
    private List<Coords> legCoords;
    private Long gasRadius;
    private String lang;
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
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}

}
