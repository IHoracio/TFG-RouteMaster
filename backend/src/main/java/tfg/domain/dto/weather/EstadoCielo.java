package tfg.domain.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EstadoCielo {
	
	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("main")
	private String main;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("icon")
	private String icon;
	
	public EstadoCielo() {
		
	}

	public EstadoCielo(Integer periodo, String descripcion) {
		super();
		this.id = periodo;
		this.description = descripcion;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMain() {
		return main;
	}

	public void setMain(String main) {
		this.main = main;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	// Helper methods for backward compatibility
	public String getValue() {
		return main;
	}

	public void setValue(String value) {
		this.main = value;
	}

	public Integer getPeriodo() {
		return id != null ? id : 0;
	}

	public void setPeriodo(Integer periodo) {
		this.id = periodo;
	}

	public String getDescripcion() {
		return description;
	}

	public void setDescripcion(String descripcion) {
		this.description = descripcion;
	}
}