package tfg.entity.maps.routes;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "rutas_compartidas")
public class SharedRoute {

    @Id
    private String token;

    private String origin;
    private String destination;

    @ElementCollection
    private List<String> waypoints;

    private boolean optimizeWaypoints;
    private boolean optimizeRoute;
    private String language;
    private boolean avoidTolls;
    private Long gasRadius;

    public SharedRoute() {}

    public SharedRoute(String token, String origin, String destination, List<String> waypoints, 
                       boolean optimizeWaypoints, boolean optimizeRoute, String language, 
                       boolean avoidTolls, Long gasRadius) {
        this.token = token;
        this.origin = origin;
        this.destination = destination;
        this.waypoints = waypoints;
        this.optimizeWaypoints = optimizeWaypoints;
        this.optimizeRoute = optimizeRoute;
        this.language = language;
        this.avoidTolls = avoidTolls;
        this.gasRadius = gasRadius;
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public List<String> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(List<String> waypoints) {
		this.waypoints = waypoints;
	}

	public boolean isOptimizeWaypoints() {
		return optimizeWaypoints;
	}

	public void setOptimizeWaypoints(boolean optimizeWaypoints) {
		this.optimizeWaypoints = optimizeWaypoints;
	}

	public boolean isOptimizeRoute() {
		return optimizeRoute;
	}

	public void setOptimizeRoute(boolean optimizeRoute) {
		this.optimizeRoute = optimizeRoute;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isAvoidTolls() {
		return avoidTolls;
	}

	public void setAvoidTolls(boolean avoidTolls) {
		this.avoidTolls = avoidTolls;
	}

	public Long getGasRadius() {
		return gasRadius;
	}

	public void setGasRadius(Long gasRadius) {
		this.gasRadius = gasRadius;
	}

}
