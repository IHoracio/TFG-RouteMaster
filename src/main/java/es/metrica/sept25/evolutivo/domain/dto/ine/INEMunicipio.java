package es.metrica.sept25.evolutivo.domain.dto.ine;

import com.fasterxml.jackson.annotation.JsonProperty;

public class INEMunicipio {
	
	@JsonProperty("Nombre")
    private String nombre;

    @JsonProperty("Codigo")
    private String codigoINE;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoINE() {
        return codigoINE;
    }

    public void setCodigoINE(String codigoINE) {
        this.codigoINE = codigoINE;
    }
}

