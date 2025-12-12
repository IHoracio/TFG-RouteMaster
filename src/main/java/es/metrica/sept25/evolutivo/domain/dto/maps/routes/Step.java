package es.metrica.sept25.evolutivo.domain.dto.maps.routes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Step {

	@JsonProperty("start_location")
	private Coords startLocation;

	public Coords getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(Coords startLocation) {
		this.startLocation = startLocation;
	}

}
