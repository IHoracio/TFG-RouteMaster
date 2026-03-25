package tfg.domain.dto.maps.routes.autocomplete;

import tfg.domain.dto.maps.routes.Coords;

public record PlaceSelection(
	    String placeId,
	    String address,
	    String name,
	    Coords coords
	) {}
