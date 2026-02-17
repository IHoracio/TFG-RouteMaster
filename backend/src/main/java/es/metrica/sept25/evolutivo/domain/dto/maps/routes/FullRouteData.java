package es.metrica.sept25.evolutivo.domain.dto.maps.routes;

import java.util.List;
import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;

public class FullRouteData {
    private List<Coords> polylineCoords;
    private List<Coords> legCoords;
    private List<Gasolinera> gasStations;
    private List<CoordsWithWeather> weatherData;

    public FullRouteData(List<Coords> polylineCoords, List<Coords> legCoords, 
                         List<Gasolinera> gasStations, List<CoordsWithWeather> weatherData) {
        this.polylineCoords = polylineCoords;
        this.legCoords = legCoords;
        this.gasStations = gasStations;
        this.weatherData = weatherData;
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
