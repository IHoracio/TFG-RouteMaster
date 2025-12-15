package es.metrica.sept25.evolutivo.entity.user;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import es.metrica.sept25.evolutivo.entity.maps.routes.SavedRoute;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

	@ElementCollection
	private Map<String, String> preferences = new HashMap<String, String>(Map.of("Tema","Claro"));

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SavedRoute> savedRoutes = new LinkedList<SavedRoute>();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PrioridadGasolineras gasStationPriority = PrioridadGasolineras.PRICE;

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

	public Map<String, String> getPreferences() {
		return new HashMap<String, String>(this.preferences);
	}

	public void setPreferences(Map<String, String> preferencias) {
		this.preferences = preferencias;
	}

	public List<SavedRoute> getSavedRoutes() {
		return new LinkedList<SavedRoute>(this.savedRoutes);
	}

	public void setSavedRoutes(List<SavedRoute> savedRoutes) {
		this.savedRoutes = savedRoutes;
	}

	public PrioridadGasolineras getGasStationPriority() {
		return this.gasStationPriority;
	}

	public void setGasStationPriority(PrioridadGasolineras prioridadGasolineras) {
		this.gasStationPriority = prioridadGasolineras;
	}
}
