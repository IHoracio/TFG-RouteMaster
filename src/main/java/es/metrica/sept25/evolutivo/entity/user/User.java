package es.metrica.sept25.evolutivo.entity.user;

import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import es.metrica.sept25.evolutivo.entity.maps.routes.Coords;
import es.metrica.sept25.evolutivo.entity.maps.routes.Route;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


//@Entity
@Table(name = "users")
public class User {
	
	public enum PrioridadGasolineras {
	    PRECIO,
	    DISTANCIA,
	    AMBAS
	}

	@Id
	private String correo;
	private String contraseña;
	private String nombre;
	private String apellidos;
	
	private Map<String, String> preferencias;
	
	private Map<String, Coords> rutas;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadGasolineras prioridadGasolineras = PrioridadGasolineras.PRECIO;
	
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getConstraseña() {
		return contraseña;
	}
	public void setContraseña(String contraseña) {
	    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	    this.contraseña = encoder.encode(contraseña);
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public Map<String, String> getPreferencias() {
		return preferencias;
	}
	public void setPreferencias(Map<String, String> preferencias) {
		this.preferencias = preferencias;
	}
	public List<Coords> getRutas() {
		return rutas;
	}
	public void setRutas(List<Coords> rutas) {
		this.rutas = rutas;
	}
	public PrioridadGasolineras getPrioridadGasolineras() {
		return prioridadGasolineras;
	}
	public void setPrioridadGasolineras(PrioridadGasolineras prioridadGasolineras) {
		this.prioridadGasolineras = prioridadGasolineras;
	}
	
	
	
}

