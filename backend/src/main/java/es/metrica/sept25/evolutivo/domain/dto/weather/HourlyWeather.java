package es.metrica.sept25.evolutivo.domain.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HourlyWeather {
	
	@JsonProperty("dt")
	private Long dt;
	
	@JsonProperty("temp")
	private Double temp;
	
	@JsonProperty("feels_like")
	private Double feelsLike;
	
	@JsonProperty("pressure")
	private Integer pressure;
	
	@JsonProperty("humidity")
	private Integer humidity;
	
	@JsonProperty("dew_point")
	private Double dewPoint;
	
	@JsonProperty("uvi")
	private Double uvi;
	
	@JsonProperty("clouds")
	private Integer clouds;
	
	@JsonProperty("visibility")
	private Integer visibility;
	
	@JsonProperty("wind_speed")
	private Double windSpeed;
	
	@JsonProperty("wind_deg")
	private Integer windDeg;
	
	@JsonProperty("wind_gust")
	private Double windGust;
	
	@JsonProperty("weather")
	private List<EstadoCielo> weather;
	
	@JsonProperty("pop")
	private Double pop;
	
	@JsonProperty("rain")
	private Rain rain;
	
	@JsonProperty("snow")
	private Rain snow;

	public Long getDt() {
		return dt;
	}

	public void setDt(Long dt) {
		this.dt = dt;
	}

	public Double getTemp() {
		return temp;
	}

	public void setTemp(Double temp) {
		this.temp = temp;
	}

	public Double getFeelsLike() {
		return feelsLike;
	}

	public void setFeelsLike(Double feelsLike) {
		this.feelsLike = feelsLike;
	}

	public Integer getPressure() {
		return pressure;
	}

	public void setPressure(Integer pressure) {
		this.pressure = pressure;
	}

	public Integer getHumidity() {
		return humidity;
	}

	public void setHumidity(Integer humidity) {
		this.humidity = humidity;
	}

	public Double getDewPoint() {
		return dewPoint;
	}

	public void setDewPoint(Double dewPoint) {
		this.dewPoint = dewPoint;
	}

	public Double getUvi() {
		return uvi;
	}

	public void setUvi(Double uvi) {
		this.uvi = uvi;
	}

	public Integer getClouds() {
		return clouds;
	}

	public void setClouds(Integer clouds) {
		this.clouds = clouds;
	}

	public Integer getVisibility() {
		return visibility;
	}

	public void setVisibility(Integer visibility) {
		this.visibility = visibility;
	}

	public Double getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(Double windSpeed) {
		this.windSpeed = windSpeed;
	}

	public Integer getWindDeg() {
		return windDeg;
	}

	public void setWindDeg(Integer windDeg) {
		this.windDeg = windDeg;
	}

	public Double getWindGust() {
		return windGust;
	}

	public void setWindGust(Double windGust) {
		this.windGust = windGust;
	}

	public List<EstadoCielo> getWeather() {
		return weather;
	}

	public void setWeather(List<EstadoCielo> weather) {
		this.weather = weather;
	}

	public Double getPop() {
		return pop;
	}

	public void setPop(Double pop) {
		this.pop = pop;
	}

	public Rain getRain() {
		return rain;
	}

	public void setRain(Rain rain) {
		this.rain = rain;
	}

	public Rain getSnow() {
		return snow;
	}

	public void setSnow(Rain snow) {
		this.snow = snow;
	}

	@Override
	public String toString() {
		return "HourlyWeather [dt=" + dt + ", temp=" + temp + ", feelsLike=" + feelsLike 
				+ ", pressure=" + pressure + ", humidity=" + humidity + ", dewPoint=" + dewPoint 
				+ ", uvi=" + uvi + ", clouds=" + clouds + ", visibility=" + visibility 
				+ ", windSpeed=" + windSpeed + ", windDeg=" + windDeg + ", windGust=" + windGust 
				+ ", weather=" + weather + ", pop=" + pop + ", rain=" + rain + ", snow=" + snow + "]";
	}
}
