package tfg.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tfg.entity.maps.routes.SavedRoute;
import tfg.entity.user.User;

@Repository
public interface SavedRouteRepository extends JpaRepository<SavedRoute, Long> {

	List<SavedRoute> findByUser(User user);

	Optional<SavedRoute> findByRouteId(Long routeId);
}
