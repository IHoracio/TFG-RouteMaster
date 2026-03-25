package tfg.entity.maps.routes;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import tfg.domain.dto.maps.routes.autocomplete.PlaceSelection;

@Entity
public class Point {

	public enum TypePoint {
		ORIGIN, WAYPOINT, DESTINATION
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private TypePoint type;
	
	@JdbcTypeCode(SqlTypes.JSON)
	private PlaceSelection placeSelection;

	@ManyToOne
	@JsonBackReference("route-puntos")
	private SavedRoute savedRoute;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TypePoint getType() {
		return type;
	}

	public void setType(TypePoint type) {
		this.type = type;
	}

	public PlaceSelection getPlaceSelection() {
		return placeSelection;
	}

	public void setPlaceSelection(PlaceSelection placeSelection) {
		this.placeSelection = placeSelection;
	}

	public SavedRoute getSavedRoute() {
		return savedRoute;
	}

	public void setSavedRoute(SavedRoute savedRoute) {
		this.savedRoute = savedRoute;
	}
}
