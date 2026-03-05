package tfg.service.maps.routes.options;

import java.util.List;
import java.util.Locale;

import tfg.domain.dto.user.preferences.EnumOptionDTO;

public interface RouteOptionsService {

	List<EnumOptionDTO> getFuelTypes(Locale locale);

	List<EnumOptionDTO> getMapViewTypes(Locale locale);

	List<EnumOptionDTO> getEmissionTypes(Locale locale);

}
