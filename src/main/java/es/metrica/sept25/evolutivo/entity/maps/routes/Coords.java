package es.metrica.sept25.evolutivo.entity.maps.routes;

import jakarta.persistence.Embeddable;

@Embeddable
public class Coords {

	private Double lat;
	private Double lng;

	public Coords() {
	}

	public Coords(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

}
