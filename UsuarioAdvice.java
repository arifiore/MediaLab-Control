package com.medialab.control.config;

import com.medialab.control.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UsuarioAdvice {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAdvice(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Disponible en TODOS los templates como ${usuarioNombre} y ${usuarioRol}
    @ModelAttribute("usuarioNombre")
    public String usuarioNombre(Authentication auth) {
        if (auth == null) return "Invitado";
        return usuarioRepository.findByUsername(auth.getName())
                .map(u -> u.getNombre())
                .orElse(auth.getName());
    }

    @ModelAttribute("usuarioRol")
    public String usuarioRol(Authentication auth) {
        if (auth == null) return "";
        return usuarioRepository.findByUsername(auth.getName())
                .map(u -> u.getRol())
                .orElse("OPERADOR");
    }
}