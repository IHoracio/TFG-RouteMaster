package es.metrica.sept25.evolutivo.domain.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather {
	
	@JsonProperty("lat")
	private Double lat;
	
	@JsonProperty("lon")
	private Double lon;
	
	@JsonProperty("timezone")
	private String timezone;
	
	@JsonProperty("timezone_offset")
	private Integer timezoneOffset;
	
	private String direccion;
	
	@JsonProperty("hourly")
	private List<HourlyWeather> hourly;
	
	@JsonProperty("alerts")
	private List<Alerta> alerts;

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public Integer getTimezoneOffset() {
		return timezoneOffset;
	}

	public void setTimezoneOffset(Integer timezoneOffset) {
		this.timezoneOffset = timezoneOffset;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public List<HourlyWeather> getHourly() {
		return hourly;
	}

	public void setHourly(List<HourlyWeather> hourly) {
		this.hourly = hourly;
	}

	public List<Alerta> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Alerta> alerts) {
		this.alerts = alerts;
	}

	@Override
	public String toString() {
		return "Weather [lat=" + lat + ", lon=" + lon + ", timezone=" + timezone 
				+ ", timezoneOffset=" + timezoneOffset + ", direccion=" + direccion 
				+ ", hourly=" + hourly + ", alerts=" + alerts + "]";
	}
}

