package es.metrica.sept25.evolutivo.controller.maps.routes.options;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.user.preferences.EnumOptionDTO;
import es.metrica.sept25.evolutivo.service.maps.routes.options.RouteOptionsService;

@RestController
@RequestMapping("/api/route-options")
public class RouteOptionsController {
	@Autowired
    private RouteOptionsService service;
	
	 @GetMapping("/fuels")
	    public List<EnumOptionDTO> getFuels() {
	        return service.getFuelTypes(LocaleContextHolder.getLocale());
	    }

	    @GetMapping("/map-types")
	    public List<EnumOptionDTO> getMapTypes() {
	        return service.getMapViewTypes(LocaleContextHolder.getLocale());
	    }

	    @GetMapping("/emissions")
	    public List<EnumOptionDTO> getEmissions() {
	        return service.getEmissionTypes(LocaleContextHolder.getLocale());
	    }
}
