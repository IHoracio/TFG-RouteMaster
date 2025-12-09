package es.metrica.sept25.evolutivo.domain.dto.ine;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class INEResponse {

	@JsonProperty("Nombre")
    private String nombre;

    @JsonProperty("Cod")
    private String codigo;

    @JsonProperty("Data")
    private List<INEMunicipio> data;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<INEMunicipio> getData() {
        return data;
    }

    public void setData(List<INEMunicipio> data) {
        this.data = data;
    }
}
