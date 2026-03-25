package tfg.domain.dto.maps.routes;

import java.util.List;

import tfg.domain.dto.maps.routes.autocomplete.PlaceSelection;

public record RouteRequestDto(
	    PlaceSelection origin,
	    PlaceSelection destination,
	    List<PlaceSelection> waypoints,
	    boolean optimizeWaypoints,
	    boolean optimizeRoute,
	    boolean avoidTolls,
	    String language,
	    Long gasRadius
	) {}
