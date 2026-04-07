package tfg.domain.dto.maps.routes;

import java.util.List;

import tfg.entity.gasolinera.Gasolinera;

public class FullRouteData {
	private String totalDistance;
	private String totalDuration;
    private List<Coords> polylineCoords;
    private List<Coords> legCoords;
    private List<Gasolinera> gasStations;
    private List<CoordsWithWeather> weatherData;
	public FullRouteData(String totalDistance, String totalDuration, List<Coords> polylineCoords,
			List<Coords> legCoords, List<Gasolinera> gasStations, List<CoordsWithWeather> weatherData) {
		super();
		this.totalDistance = totalDistance;
		this.totalDuration = totalDuration;
		this.polylineCoords = polylineCoords;
		this.legCoords = legCoords;
		this.gasStations = gasStations;
		this.weatherData = weatherData;
	}
	public String getTotalDistance() {
		return totalDistance;
	}
	public void setTotalDistance(String totalDistance) {
		this.totalDistance = totalDistance;
	}
	public String getTotalDuration() {
		return totalDuration;
	}
	public void setTotalDuration(String totalDuration) {
		this.totalDuration = totalDuration;
	}
	public List<Coords> getPolylineCoords() {
		return polylineCoords;
	}
	public void setPolylineCoords(List<Coords> polylineCoords) {
		this.polylineCoords = polylineCoords;
	}
	public List<Coords> getLegCoords() {
		return legCoords;
	}
	public void setLegCoords(List<Coords> legCoords) {
		this.legCoords = legCoords;
	}
	public List<Gasolinera> getGasStations() {
		return gasStations;
	}
	public void setGasStations(List<Gasolinera> gasStations) {
		this.gasStations = gasStations;
	}
	public List<CoordsWithWeather> getWeatherData() {
		return weatherData;
	}
	public void setWeatherData(List<CoordsWithWeather> weatherData) {
		this.weatherData = weatherData;
	}

}
