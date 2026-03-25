package tfg.domain.dto.maps.routes;

import java.util.List;

public class RouteGroup {
    private String status;
    private List<Route> routes;

    // Getters y Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
