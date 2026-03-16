package tfg.service.maps.routes.options;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tfg.domain.dto.user.preferences.EnumOptionDTO;
import tfg.enums.FuelType;
import tfg.enums.MapViewType;
import tfg.service.traductor.EnumTranslator;

@Service
public class RouteOptionsServiceImpl implements RouteOptionsService{

	@Autowired
	private EnumTranslator translator;

	@Override
	public List<EnumOptionDTO> getFuelTypes(Locale locale) {
        return Arrays.stream(FuelType.values())
                .map(fuel -> new EnumOptionDTO(
                        fuel.name(),
                        translator.translate("fuel", fuel, locale)))
                .toList();
    }
	
	@Override
    public List<EnumOptionDTO> getMapViewTypes(Locale locale) {
        return Arrays.stream(MapViewType.values())
                .map(map -> new EnumOptionDTO(
                        map.name(),
                        translator.translate("map", map, locale)))
                .toList();
    }


}
