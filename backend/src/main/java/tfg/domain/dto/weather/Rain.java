package tfg.domain.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rain {
	
	@JsonProperty("1h")
	private Double oneHour;

	public Double getOneHour() {
		return oneHour;
	}

	public void setOneHour(Double oneHour) {
		this.oneHour = oneHour;
	}

	@Override
	public String toString() {
		return "Rain [oneHour=" + oneHour + "]";
	}
}
