package tfg.domain.dto.maps.routes.savedRoutes;

import java.util.List;

public class SavedRouteDTO {

		private String routeId; 
		
		private String name;
		
		private List<PointDTO> points;

		public String getRouteId() {
			return routeId;
		}

		public void setRouteId(String routeId) {
			this.routeId = routeId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<PointDTO> getPoints() {
			return points;
		}

		public void setPoints(List<PointDTO> points) {
			this.points = points;
		}
		
}
