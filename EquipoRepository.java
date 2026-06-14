package com.medialab.control.repository;

import com.medialab.control.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO para Equipos — Patrón Repository (DAO). */
@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    Optional<Equipo> findByCodigoPatrimonial(String codigo);
    List<Equipo> findByEstado(String estado);
    long countByEstado(String estado);

    @Query("SELECT e FROM Equipo e WHERE " +
           "LOWER(e.codigoPatrimonial) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(e.nombre) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(e.tipo) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Equipo> buscar(@Param("q") String q);
}
