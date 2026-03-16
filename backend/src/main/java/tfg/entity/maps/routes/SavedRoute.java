package tfg.entity.maps.routes;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import tfg.entity.user.User;
import tfg.enums.EmissionType;

@Entity
public class SavedRoute {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// identificador secundario, único, que es el que se expone en la URL pública o al frontend
	@Column(name = "route_id", nullable = false, updatable = false, unique = true)
	private String routeId;

	private String name;

	// Mantenemos los puntos visuales (Origen, Waypoints, Destino) para la interfaz
	@OneToMany(mappedBy = "savedRoute", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderColumn(name = "order_index")
	@JsonManagedReference("route-puntos")
	private List<Point> puntos;

	@ManyToOne
	@JsonBackReference("user-routes")
	private User user;
	
	@Column(columnDefinition = "LONGTEXT")
	private String polylineCoordsJson;

	@Column(columnDefinition = "LONGTEXT")
	private String legCoordsJson;

	private Long gasRadius;

	private String language;



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
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

	public String getPolylineCoordsJson() {
		return polylineCoordsJson;
	}

	public void setPolylineCoordsJson(String polylineCoordsJson) {
		this.polylineCoordsJson = polylineCoordsJson;
	}

	public String getLegCoordsJson() {
		return legCoordsJson;
	}

	public void setLegCoordsJson(String legCoordsJson) {
		this.legCoordsJson = legCoordsJson;
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

	@PrePersist
	private void generateRouteId() {
		if (this.routeId == null) {
			this.routeId = java.util.UUID.randomUUID().toString();
		}
	}
}
