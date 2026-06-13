package com.medialab.control.repository;

import com.medialab.control.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO para Docentes — Patrón Repository (DAO). */
@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {
    Optional<Docente> findByDni(String dni);
    boolean existsByDni(String dni);

    @Query("SELECT d FROM Docente d WHERE " +
           "LOWER(d.dni) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(d.nombres) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(d.facultad) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Docente> buscar(@Param("q") String q);
}
