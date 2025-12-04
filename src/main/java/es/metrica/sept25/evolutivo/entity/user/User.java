package es.metrica.sept25.evolutivo.entity.user;


import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import es.metrica.sept25.evolutivo.entity.maps.routes.Route;
import es.metrica.sept25.evolutivo.entity.maps.routes.SavedRoute;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


//@Entity
@Table(name = "users")
public class User {
	
	public enum PrioridadGasolineras {
	    PRICE,
	    DISTANCE,
	    BOTH
	}

	@Id
	private String mail;
	private String password;
	private String name;
	private String surname;
	
	private Map<String, String> preferences;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SavedRoute> savedRoutes;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadGasolineras priorityGasstations = PrioridadGasolineras.PRICE;

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	    this.password = encoder.encode(password);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Map<String, String> getPreferences() {
		return preferences;
	}

	public void setPreferences(Map<String, String> preferencias) {
		this.preferences = preferencias;
	}


	public List<SavedRoute> getSavedRoutes() {
		return savedRoutes;
	}

	public void setSavedRoutes(List<SavedRoute> savedRoutes) {
		this.savedRoutes = savedRoutes;
	}

	public PrioridadGasolineras getPriorityGasstations() {
		return priorityGasstations;
	}

	public void setPriorityGasstations(PrioridadGasolineras prioridadGasolineras) {
		this.priorityGasstations = prioridadGasolineras;
	}
	
	
	
	
	
}

