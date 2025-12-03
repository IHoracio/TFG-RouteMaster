package es.metrica.sept25.evolutivo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;

public interface GasolineraRepository extends JpaRepository<Gasolinera, Long> {
}