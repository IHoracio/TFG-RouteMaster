package es.metrica.sept25.evolutivo.entity.maps.routes;

import java.util.List;
import java.util.UUID;

import es.metrica.sept25.evolutivo.entity.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;

@Entity
public class SavedRoute {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "route_id", nullable = false, updatable = false, unique = true)
    private UUID routeId;

    private String name;

    @OneToMany(mappedBy = "savedRoute", cascade = CascadeType.ALL, orphanRemoval = true)
    // TODO: Cambiar a @OrderBy
    @OrderColumn(name = "order_index")
    private List<Point> puntos;

    @ManyToOne
    private User user;
    
    private boolean optimizeWaypoints;
    private boolean optimizeRoute;
    private String language;

	public UUID getRouteId() {
		return routeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Point> getPuntos() {
		return puntos;
	}

	public void setPuntos(List<Point> puntos) {
		this.puntos = puntos;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
	
	@PrePersist
	private void generateRouteId() {
		this.routeId = UUID.randomUUID();
	}
}

