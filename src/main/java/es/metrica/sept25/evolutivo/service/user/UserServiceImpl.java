package es.metrica.sept25.evolutivo.service.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.metrica.sept25.evolutivo.domain.dto.gasolineras.SavedGasStationDTO;
import es.metrica.sept25.evolutivo.domain.dto.gasolineras.UserSavedGasStationDto;
import es.metrica.sept25.evolutivo.domain.dto.user.UserDTO;
import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.entity.gasolinera.UserSavedGasStation;
import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences;
import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences.Brands;
import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences;
import es.metrica.sept25.evolutivo.repository.GasolineraRepository;
import es.metrica.sept25.evolutivo.repository.UserRepository;
import es.metrica.sept25.evolutivo.service.gasolineras.GasolineraService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private GasolineraRepository gasolineraRepository;
	
	@Autowired
	private GasolineraService gasolineraService;

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
	public Optional<User> createUser(UserDTO userDTO) {

		if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
	        return Optional.empty();
	    }

	    User user = new User();
		user.setName(userDTO.getName());
		user.setSurname(userDTO.getSurname());
		user.setPassword(userDTO.getPassword());
		user.setEmail(userDTO.getEmail());
	    
	    RoutePreferences prefs = new RoutePreferences();
	    
	    prefs.setPreferredBrands(List.of(Brands.REPSOL, Brands.CEPSA));
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
	        List<RoutePreferences.Brands> preferredBrands,
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
	
	@Override
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
	
	@Override
	@Transactional
	public void removeGasStation(String email, String alias) {

	    Optional<User> user = userRepository.findByEmail(email);
	    
	    user.get().getSavedGasStations().removeIf(sg -> sg.getAlias().equalsIgnoreCase(alias));

	    userRepository.save(user.get());
	}
	
	@Override
	@Transactional
	public List<UserSavedGasStationDto> getSavedGasStations(String email) {

		return userRepository.findByEmail(email)
	            .map(user -> user.getSavedGasStations().stream()
	                    .map(sg -> {
	                        Gasolinera g = sg.getGasolinera();
	                        return new UserSavedGasStationDto(
	                                sg.getAlias(),
	                                g.getIdEstacion(),
	                                g.getNombreEstacion(),
	                                g.getMarca()
	                        );
	                    })
	                    .toList()
	            )
	            .orElse(List.of());
	}
	
	@Override
	@Transactional
	public Optional<String> saveGasStation(String email, String alias, Long idEstacion) {

	    Optional<User> user = userRepository.findByEmail(email);
	    if (user.isEmpty()) {
	        return Optional.of("Usuario no encontrado");
	    }

	    
	    Optional<Gasolinera> gasolinera = gasolineraRepository.findByIdEstacion(idEstacion);
	    
	    if (gasolinera.isEmpty()) {
	        gasolinera = gasolineraService.getGasolineraForId(idEstacion);
	        gasolinera.ifPresent(gasolineraRepository::save);
	    }
	    
	    if (gasolinera.isEmpty()) {
	        return Optional.of("Gasolinera no encontrada");
	    }
	    
	    boolean exists = user.get().getSavedGasStations().stream()
	        .anyMatch(g -> g.getAlias().equalsIgnoreCase(alias));

	    if (exists) {
	        return Optional.of("Alias ya existente");
	    }

	    UserSavedGasStation saved = new UserSavedGasStation();
	    saved.setAlias(alias);
	    saved.setUser(user.get());
	    saved.setGasolinera(gasolinera.get());

	    user.get().getSavedGasStations().add(saved);
	    userRepository.save(user.get());
	    
	    return Optional.empty();
	}
}
