package es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes;

import java.util.List;

public class RoutePreferencesDTO {

	private List<String> preferredBrands;
    private int radioKm;
    private String fuelType;
    private Double maxPrice;
    private String mapView;
	public List<String> getPreferredBrands() {
		return preferredBrands;
	}
	public void setPreferredBrands(List<String> preferredBrands) {
		this.preferredBrands = preferredBrands;
	}
	public int getRadioKm() {
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
	public String getMapView() {
		return mapView;
	}
	public void setMapView(String mapView) {
		this.mapView = mapView;
	}
    
    
}
