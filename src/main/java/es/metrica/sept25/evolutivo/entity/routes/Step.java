package es.metrica.sept25.evolutivo.entity.routes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Step {
	
	@JsonProperty("start_location")
	private Cords startLocation;

	public Cords getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(Cords startLocation) {
		this.startLocation = startLocation;
	}
	
}
