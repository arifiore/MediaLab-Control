package com.medialab.control.repository;

import com.medialab.control.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** DAO para Préstamos — Patrón Repository (DAO). */
@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    @Query("SELECT p FROM Prestamo p WHERE p.fechaDevolucion IS NULL ORDER BY p.fechaSalida ASC")
    List<Prestamo> findPrestamosActivos();

    @Query("SELECT p FROM Prestamo p WHERE p.fechaDevolucion IS NULL " +
           "AND p.fechaLimite < CURRENT_DATE ORDER BY p.fechaLimite ASC")
    List<Prestamo> findPrestamosEnMora();

    Optional<Prestamo> findByEquipoIdAndFechaDevolucionIsNull(Long equipoId);

    long countByFechaDevolucionIsNull();
}
