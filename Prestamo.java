package com.medialab.control.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDate fechaLimite;

    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    public Prestamo() {}

    /** Lógica de negocio: días de mora. Usado por TDD y dashboard. */
    public long calcularDiasMora() {
        LocalDate referencia = (fechaDevolucion != null) ? fechaDevolucion : LocalDate.now();
        if (referencia.isAfter(fechaLimite)) {
            return ChronoUnit.DAYS.between(fechaLimite, referencia);
        }
        return 0;
    }

    public boolean isEnMora()      { return fechaDevolucion == null && calcularDiasMora() > 0; }
    public boolean isDevuelto()    { return fechaDevolucion != null; }

    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }
    public Equipo getEquipo()                  { return equipo; }
    public void setEquipo(Equipo e)            { this.equipo = e; }
    public Docente getDocente()                { return docente; }
    public void setDocente(Docente d)          { this.docente = d; }
    public LocalDate getFechaSalida()          { return fechaSalida; }
    public void setFechaSalida(LocalDate f)    { this.fechaSalida = f; }
    public LocalDate getFechaLimite()          { return fechaLimite; }
    public void setFechaLimite(LocalDate f)    { this.fechaLimite = f; }
    public LocalDate getFechaDevolucion()      { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate f){ this.fechaDevolucion = f; }
}
