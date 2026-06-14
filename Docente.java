package com.medialab.control.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "docentes")
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 dígitos")
    @Column(unique = true, nullable = false, length = 8)
    private String dni;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombres;

    @NotBlank(message = "La facultad es obligatoria")
    @Column(nullable = false, length = 100)
    private String facultad;

    public Docente() {}

    public Long getId()                    { return id; }
    public void setId(Long id)             { this.id = id; }
    public String getDni()                 { return dni; }
    public void setDni(String dni)         { this.dni = dni; }
    public String getNombres()             { return nombres; }
    public void setNombres(String n)       { this.nombres = n; }
    public String getFacultad()            { return facultad; }
    public void setFacultad(String f)      { this.facultad = f; }
}
