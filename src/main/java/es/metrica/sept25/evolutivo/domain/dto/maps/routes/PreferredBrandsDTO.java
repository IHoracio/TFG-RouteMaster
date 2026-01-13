package es.metrica.sept25.evolutivo.domain.dto.maps.routes;

import java.util.List;

import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences;
import io.swagger.v3.oas.annotations.media.Schema;

public class PreferredBrandsDTO {
	
	public List<String> preferredBrands;

	public List<String> getPreferredBrands() {
		return preferredBrands;
	}

	public void setPreferredBrands(List<String> preferredBrands) {
		this.preferredBrands = preferredBrands;
	}

	
}
