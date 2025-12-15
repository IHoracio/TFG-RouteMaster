package es.metrica.sept25.evolutivo.entity.maps.routes;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class RoutePreferences  {

	public enum MapViewType {
	    SATELLITE,
	    SCHEMATIC
	}
	
	@ElementCollection
    private List<String> preferredBrands;

    private Integer radioKm;

    private String fuelType;

    private Double maxPrice;

    @Enumerated(EnumType.STRING)
    private MapViewType mapView;

	public List<String> getPreferredBrands() {
		return preferredBrands;
	}

	public void setPreferredBrands(List<String> preferredBrands) {
		this.preferredBrands = preferredBrands;
	}

	public Integer getRadioKm() {
		return radioKm;
	}

	public void setRadioKm(int radioKm) {
		this.radioKm = radioKm;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public MapViewType getMapView() {
		return mapView;
	}

	public void setMapView(MapViewType mapView) {
		this.mapView = mapView;
	}
    
    
}
