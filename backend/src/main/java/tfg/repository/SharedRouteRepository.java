package tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tfg.entity.maps.routes.SharedRoute;

@Repository
public interface SharedRouteRepository extends JpaRepository<SharedRoute, String> {

}
