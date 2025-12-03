package es.metrica.sept25.evolutivo.entity.routes;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Leg {
	
	private Distance distance;
	private Duration duration;
	
	@JsonProperty("end_address")
	private String endAddress;
	
	@JsonProperty("start_address")
	private String startAddress;
	
	@JsonProperty("end_location")
	private Cords endLocation;
	
	@JsonProperty("start_location")
	private Cords startLocation;
	List<Step> steps;
	public Distance getDistance() {
		return distance;
	}
	public void setDistance(Distance distance) {
		this.distance = distance;
	}
	public Duration getDuration() {
		return duration;
	}
	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	public String getEndAddress() {
		return endAddress;
	}
	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}
	public String getStartAddress() {
		return startAddress;
	}
	public void setStartAddress(String startAddress) {
		this.startAddress = startAddress;
	}
	public Cords getEndLocation() {
		return endLocation;
	}
	public void setEndLocation(Cords endLocation) {
		this.endLocation = endLocation;
	}
	public Cords getStartLocation() {
		return startLocation;
	}
	public void setStartLocation(Cords startLocation) {
		this.startLocation = startLocation;
	}
	public List<Step> getSteps() {
		return steps;
	}
	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}
	
}
