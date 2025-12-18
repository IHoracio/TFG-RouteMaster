package es.metrica.sept25.evolutivo.service.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences;
import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences;
import es.metrica.sept25.evolutivo.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public User save(User user) {

		if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		return userRepository.save(user);
	}

	@Override
	public Optional<User> getByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public List<User> getAll() {
		return userRepository.findAll();
	}

	@Override
	@Transactional
	public void deleteByEmail(String email) {
		Optional<User> user = getByEmail(email);
		if (user.isPresent()) {
			userRepository.deleteByEmail(email);
		}
	}
	
	@Override
	@Transactional
	public Optional<User> createUser(String nombre, String apellido, String password, String email) {

	    if (userRepository.findByEmail(email).isPresent()) {
	        return Optional.empty();
	    }

	    User user = new User();
	    user.setName(nombre);
	    user.setSurname(apellido);
	    user.setPassword(password);
	    user.setEmail(email);
	    
	    RoutePreferences prefs = new RoutePreferences();
        prefs.setPreferredBrands(List.of("REPSOL", "CEPSA"));
        prefs.setRadioKm(5);
        prefs.setFuelType("GASOLINE");
        prefs.setMaxPrice(1.50);
        prefs.setMapView(RoutePreferences.MapViewType.SCHEMATIC);

        user.setRoutePreferences(prefs);
        
        UserPreferences defaultPrefs = new UserPreferences();
        defaultPrefs.setTheme("Claro");
        defaultPrefs.setLanguage("es");

        user.setUserPreferences(defaultPrefs);

	    return Optional.of(save(user));
	}
	
	@Override
	@Transactional
	public void updateRoutePreferences(
	        User user,
	        List<String> preferredBrands,
	        int radioKm,
	        String fuelType,
	        double maxPrice,
	        RoutePreferences.MapViewType mapView
	) {

        RoutePreferences prefs = new RoutePreferences();
        prefs.setPreferredBrands(preferredBrands);
        prefs.setRadioKm(radioKm);
        prefs.setFuelType(fuelType);
        prefs.setMaxPrice(maxPrice);
        prefs.setMapView(mapView);

        user.setRoutePreferences(prefs);
        userRepository.save(user);
    }
	
	@Transactional
	public void updateUserPreferences(
	        User user,
	        String theme,
	        String language
	) {
	    UserPreferences prefs = new UserPreferences();
	    prefs.setTheme(theme);
	    prefs.setLanguage(language);

	    user.setUserPreferences(prefs);
	    userRepository.save(user);
	}
}
