package com.medialab.control.controller;

import com.medialab.control.model.Docente;
import com.medialab.control.model.Equipo;
import com.medialab.control.service.DocenteService;
import com.medialab.control.service.EquipoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WebController {

    private final EquipoService equipoService;
    private final DocenteService docenteService;

    public WebController(EquipoService equipoService, DocenteService docenteService) {
        this.equipoService  = equipoService;
        this.docenteService = docenteService;
    }

    /* ========== EQUIPOS ========== */

    @GetMapping("/equipos")
    public String equipos(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("equipos", equipoService.listar(q));
        model.addAttribute("q", q);
        return "equipos";
    }

    @GetMapping("/equipos/nuevo")
    public String nuevoEquipo(Model model) {
        model.addAttribute("equipo", new Equipo());
        model.addAttribute("modoEdicion", false);
        return "equipo-form";
    }

    @GetMapping("/equipos/editar/{id}")
    public String editarEquipo(@PathVariable Long id, Model model) {
        Equipo e = equipoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));
        model.addAttribute("equipo", e);
        model.addAttribute("modoEdicion", true);
        return "equipo-form";
    }

    @PostMapping("/equipos/guardar")
    public String guardarEquipo(@Valid @ModelAttribute Equipo equipo,
                                BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("modoEdicion", equipo.getId() != null);
            return "equipo-form";
        }
        try {
            equipoService.guardar(equipo);
            ra.addFlashAttribute("exito", "Equipo guardado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/equipos";
    }

    @PostMapping("/equipos/eliminar/{id}")
    public String eliminarEquipo(@PathVariable Long id, RedirectAttributes ra) {
        try {
            equipoService.eliminar(id);
            ra.addFlashAttribute("exito", "Equipo eliminado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/equipos";
    }

    /* ========== DOCENTES ========== */

    @GetMapping("/docentes")
    public String docentes(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("docentes", docenteService.listar(q));
        model.addAttribute("q", q);
        return "docentes";
    }

    @GetMapping("/docentes/nuevo")
    public String nuevoDocente(Model model) {
        model.addAttribute("docente", new Docente());
        model.addAttribute("modoEdicion", false);
        return "docente-form";
    }

    @GetMapping("/docentes/editar/{id}")
    public String editarDocente(@PathVariable Long id, Model model) {
        Docente d = docenteService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Docente no encontrado"));
        model.addAttribute("docente", d);
        model.addAttribute("modoEdicion", true);
        return "docente-form";
    }

    @PostMapping("/docentes/guardar")
    public String guardarDocente(@Valid @ModelAttribute Docente docente,
                                 BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("modoEdicion", docente.getId() != null);
            return "docente-form";
        }
        try {
            docenteService.guardar(docente);
            ra.addFlashAttribute("exito", "Docente guardado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/docentes";
    }

    @PostMapping("/docentes/eliminar/{id}")
    public String eliminarDocente(@PathVariable Long id, RedirectAttributes ra) {
        try {
            docenteService.eliminar(id);
            ra.addFlashAttribute("exito", "Docente eliminado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/docentes";
    }

    /* ========== API JSON (autocompletado) ========== */

    @GetMapping("/docentes/api/dni")
    @ResponseBody
    public Map<String, Object> buscarDocentePorDni(@RequestParam String dni) {
        return docenteService.buscarPorDni(dni).map(d -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", d.getId());
            m.put("nombres", d.getNombres());
            m.put("facultad", d.getFacultad());
            return m;
        }).orElse(null);
    }

    @GetMapping("/equipos/api/codigo")
    @ResponseBody
    public Map<String, Object> buscarEquipoPorCodigo(@RequestParam String codigo) {
        return equipoService.buscarPorId(0L)
                .map(e -> (Map<String, Object>) new HashMap<String, Object>())
                .orElseGet(() -> {
                    // Busca por código directamente
                    return equipoService.listar(codigo).stream()
                            .filter(e -> e.getCodigoPatrimonial().equalsIgnoreCase(codigo.trim()))
                            .findFirst()
                            .map(e -> {
                                Map<String, Object> m = new HashMap<>();
                                m.put("id", e.getId());
                                m.put("nombre", e.getNombre());
                                m.put("estado", e.getEstado());
                                m.put("codigo", e.getCodigoPatrimonial());
                                return m;
                            }).orElse(null);
                });
    }
}
