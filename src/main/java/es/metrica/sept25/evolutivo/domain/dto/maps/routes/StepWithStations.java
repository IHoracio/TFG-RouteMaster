package es.metrica.sept25.evolutivo.domain.dto.maps.routes;

import java.util.LinkedList;
import java.util.List;

import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;

public class StepWithStations {
	private double lat;
	private double lng;
	private List<Gasolinera> stations;

	public StepWithStations(double lat, double lng, List<Gasolinera> stations) {
		this.lat = lat;
		this.lng = lng;
		this.stations = new LinkedList<Gasolinera>(stations);
	}

	public double getLat() {
		return this.lat;
	}

	public double getLng() {
		return this.lng;
	}

	public List<Gasolinera> getStations() {
		return new LinkedList<Gasolinera>(this.stations);
	}
}
