package es.metrica.sept25.evolutivo.entity.maps.routes;

import java.util.List;

import es.metrica.sept25.evolutivo.entity.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;

@Entity
public class SavedRoute {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "savedRoute", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "order_index")
    private List<Point> puntos;

    @ManyToOne
    private User user;
    
    private boolean optimizeWaypoints;
    private boolean optimizeRoute;
    private String language;

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
}
