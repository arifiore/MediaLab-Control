package com.medialab.control.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    private String rol;  // "ADMINISTRADOR" o "OPERADOR"

    @Column(nullable = false, length = 100)
    private String nombre;

    public Usuario() {}

    public Long getId()               { return id; }
    public void setId(Long id)        { this.id = id; }
    public String getUsername()       { return username; }
    public void setUsername(String u) { this.username = u; }
    public String getPassword()       { return password; }
    public void setPassword(String p) { this.password = p; }
    public String getRol()            { return rol; }
    public void setRol(String r)      { this.rol = r; }
    public String getNombre()         { return nombre; }
    public void setNombre(String n)   { this.nombre = n; }
}