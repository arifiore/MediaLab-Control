package com.medialab.control.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "equipos")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código patrimonial es obligatorio")
    @Column(name = "codigo_patrimonial", unique = true, nullable = false, length = 50)
    private String codigoPatrimonial;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El tipo es obligatorio")
    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, length = 20)
    private String estado = "DISPONIBLE";

    public Equipo() {}

    public Long getId()                              { return id; }
    public void setId(Long id)                       { this.id = id; }
    public String getCodigoPatrimonial()             { return codigoPatrimonial; }
    public void setCodigoPatrimonial(String c)       { this.codigoPatrimonial = c; }
    public String getNombre()                        { return nombre; }
    public void setNombre(String n)                  { this.nombre = n; }
    public String getTipo()                          { return tipo; }
    public void setTipo(String t)                    { this.tipo = t; }
    public String getEstado()                        { return estado; }
    public void setEstado(String e)                  { this.estado = e; }
}
