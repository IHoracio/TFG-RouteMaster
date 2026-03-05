package tfg.service.user;

import java.util.List;
import java.util.Optional;

import tfg.domain.dto.gasolineras.UserSavedGasStationDto;
import tfg.domain.dto.user.UserBasicInfoDTO;
import tfg.domain.dto.user.UserDTO;
import tfg.domain.dto.user.UserResponseDTO;
import tfg.entity.maps.routes.RoutePreferences;
import tfg.entity.user.User;
import tfg.entity.user.UserPreferences.Language;
import tfg.entity.user.UserPreferences.Theme;
import tfg.enums.EmissionType;
import tfg.enums.FuelType;
import tfg.enums.MapViewType;

public interface UserService {
    User save(User user);
    
    Optional<UserBasicInfoDTO> getSimpleInfo(String email);

    Optional<UserResponseDTO> getByEmail(String mail);

    List<UserResponseDTO> getAll();

	Optional<User> createUser(UserDTO userDTO);

	void removeGasStation(String email, String alias);

	List<UserSavedGasStationDto> getSavedGasStations(String email);

	Optional<String> saveGasStation(String email, String alias, Long idEstacion);

	boolean renameGasStation(String email, String oldAlias, String newAlias);

	Optional<User> getEntityByEmail(String email);

	void updateUserPreferences(User user, Theme theme, Language language);

	void updateRoutePreferences(User user, List<String> preferredBrands, int radioKm, FuelType fuelType,
			double maxPrice, MapViewType mapView, boolean avoidTolls, EmissionType vehicleEmissionType);

	Optional<RoutePreferences> getDefaultPreferences();
}
