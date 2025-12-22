package es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes;

import java.util.List;

import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences;

public class RoutePreferencesDTO {

	private List<String> preferredBrands;
    private Integer radioKm;
    private String fuelType;
    private Double maxPrice;
    private RoutePreferences.MapViewType mapView;
	public List<String> getPreferredBrands() {
		return preferredBrands;
	}
	public void setPreferredBrands(List<String> preferredBrands) {
		this.preferredBrands = preferredBrands;
	}
	public Integer getRadioKm() {
		return radioKm;
	}
	public void setRadioKm(Integer radioKm) {
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
	public RoutePreferences.MapViewType getMapView() {
		return mapView;
	}
	public void setMapView(RoutePreferences.MapViewType mapView) {
		this.mapView = mapView;
	}
    
    
}
