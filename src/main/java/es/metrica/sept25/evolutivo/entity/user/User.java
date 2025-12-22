package es.metrica.sept25.evolutivo.entity.user;

import java.util.LinkedList;
import java.util.List;

import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences;
import es.metrica.sept25.evolutivo.entity.maps.routes.SavedRoute;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "users")
public class User {

	public enum PrioridadGasolineras {
		PRICE, 
		DISTANCE, 
		BOTH
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "email", unique = true)
	private String email;
	
	private String password;
	
	@Transient
	private String passwordConfirmation;
	
	private String name;
	private String surname;

	@Embedded
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private UserPreferences userPreferences;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SavedRoute> savedRoutes = new LinkedList<SavedRoute>();
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Gasolinera> favouriteGasStations = new LinkedList<Gasolinera>();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PrioridadGasolineras gasStationPriority = PrioridadGasolineras.PRICE;
	
	@Embedded
    private RoutePreferences routePreferences;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirmation() {
		return this.passwordConfirmation;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public UserPreferences getUserPreferences() {
		return userPreferences;
	}

	public void setUserPreferences(UserPreferences userPreferences) {
		this.userPreferences = userPreferences;
	}

	public List<SavedRoute> getSavedRoutes() {
		return new LinkedList<SavedRoute>(this.savedRoutes);
	}

	public void setSavedRoutes(List<SavedRoute> savedRoutes) {
		this.savedRoutes = savedRoutes;
	}

	public List<Gasolinera> getFavouriteGasStations() {
		return new LinkedList<Gasolinera>(this.favouriteGasStations);
	}

	public void setFavouriteGasStations(List<Gasolinera> favouriteGasStations) {
		this.favouriteGasStations = favouriteGasStations;
	}

	public PrioridadGasolineras getGasStationPriority() {
		return this.gasStationPriority;
	}

	public void setGasStationPriority(PrioridadGasolineras prioridadGasolineras) {
		this.gasStationPriority = prioridadGasolineras;
	}

	public RoutePreferences getRoutePreferences() {
		return routePreferences;
	}

	public void setRoutePreferences(RoutePreferences routePreferences) {
		this.routePreferences = routePreferences;
	}
	
}
