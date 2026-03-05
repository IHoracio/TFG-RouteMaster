package tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tfg.entity.gasolinera.Municipio;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Long> {
}
