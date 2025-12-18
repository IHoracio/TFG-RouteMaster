package es.metrica.sept25.evolutivo.domain.dto.maps.routes.executionRoutes;

public class RouteExecutionDTO {

	private String polyline;
    private long distanceMeters;
    private long durationSeconds;
	public String getPolyline() {
		return polyline;
	}
	public void setPolyline(String polyline) {
		this.polyline = polyline;
	}
	public long getDistanceMeters() {
		return distanceMeters;
	}
	public void setDistanceMeters(Long distanceMeters) {
		this.distanceMeters = distanceMeters;
	}
	public long getDurationSeconds() {
		return durationSeconds;
	}
	public void setDurationSeconds(Long durationSeconds) {
		this.durationSeconds = durationSeconds;
	}
    
    
}
