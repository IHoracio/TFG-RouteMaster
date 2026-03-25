package tfg.entity.gasolinera;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import tfg.domain.dto.maps.routes.Coords;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceDetailsResponse {
    
    private String status;
    private Result result;

    // Getters y Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Result getResult() { return result; }
    public void setResult(Result result) { this.result = result; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private Geometry geometry;

        public Geometry getGeometry() { return geometry; }
        public void setGeometry(Geometry geometry) { this.geometry = geometry; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Geometry {
        private Coords location;

        public Coords getLocation() { return location; }
        public void setLocation(Coords location) { this.location = location; }
    }


}
