package es.metrica.sept25.evolutivo.service.user;

import java.util.List;
import java.util.Optional;

import es.metrica.sept25.evolutivo.domain.dto.gasolineras.UserSavedGasStationDto;
import es.metrica.sept25.evolutivo.domain.dto.user.UserDTO;
import es.metrica.sept25.evolutivo.entity.gasolinera.UserSavedGasStation;
import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences;
import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences.MapViewType;
import es.metrica.sept25.evolutivo.entity.user.User;

public interface UserService {
    User save(User user);

    Optional<User> getByEmail(String mail);

    List<User> getAll();

    void deleteByEmail(String mail);

//	Optional<User> createUser(String name, String surname, String password, String email);

	Optional<User> createUser(UserDTO userDTO);

	void updateRoutePreferences(User user, List<RoutePreferences.Brands> preferredBrands, int radioKm, String fuelType, double maxPrice,
			MapViewType mapView);

	void removeGasStation(String email, String alias);

	List<UserSavedGasStationDto> getSavedGasStations(String email);

	Optional<String> saveGasStation(String email, String alias, Long idEstacion);

	void updateUserPreferences(User user, String theme, String language);
}
