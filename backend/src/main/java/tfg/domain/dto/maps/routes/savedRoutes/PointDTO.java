package tfg.domain.dto.maps.routes.savedRoutes;

import tfg.domain.dto.maps.routes.autocomplete.PlaceSelection;

public class PointDTO {

	private String type;
	private PlaceSelection placeSelection;

	public PointDTO(String type, PlaceSelection placeSelection) {
		this.type= type;
		this.placeSelection = placeSelection;
	}

	public PointDTO() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PlaceSelection getPlaceSelection() {
		return placeSelection;
	}

	public void setPlaceSelection(PlaceSelection placeSelection) {
		this.placeSelection = placeSelection;
	}

}
