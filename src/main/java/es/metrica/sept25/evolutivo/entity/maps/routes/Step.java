package es.metrica.sept25.evolutivo.entity.maps.routes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
