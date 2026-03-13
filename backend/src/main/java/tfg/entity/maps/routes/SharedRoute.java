package tfg.entity.maps.routes;

import jakarta.persistence.*;

@Entity
@Table(name = "rutas_compartidas")
public class SharedRoute {

    @Id
    private String token;

    @Column(columnDefinition = "LONGTEXT")
    private String polylineCoordsJson; 

    @Column(columnDefinition = "LONGTEXT")
    private String legCoordsJson;

    private Long gasRadius;
    
    private String lang;

    public SharedRoute() {}

    public SharedRoute(String token, String polylineCoordsJson, String legCoordsJson, Long gasRadius, String lang) {
        this.token = token;
        this.polylineCoordsJson = polylineCoordsJson;
        this.legCoordsJson = legCoordsJson;
        this.gasRadius = gasRadius;
        this.lang = lang;
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPolylineCoordsJson() {
		return polylineCoordsJson;
	}

	public void setPolylineCoordsJson(String polylineCoordsJson) {
		this.polylineCoordsJson = polylineCoordsJson;
	}

	public String getLegCoordsJson() {
		return legCoordsJson;
	}

	public void setLegCoordsJson(String legCoordsJson) {
		this.legCoordsJson = legCoordsJson;
	}

	public Long getGasRadius() {
		return gasRadius;
	}

	public void setGasRadius(Long gasRadius) {
		this.gasRadius = gasRadius;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

}
