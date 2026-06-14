package com.medialab.control.controller;

import com.medialab.control.service.DocenteService;
import com.medialab.control.service.EquipoService;
import com.medialab.control.service.PrestamoService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

@Controller
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;
    private final EquipoService equipoService;
    private final DocenteService docenteService;

    public PrestamoController(PrestamoService ps, EquipoService es, DocenteService ds) {
        this.prestamoService = ps;
        this.equipoService   = es;
        this.docenteService  = ds;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("prestamosActivos", prestamoService.obtenerPrestamosActivos());
        model.addAttribute("prestamosEnMora",  prestamoService.obtenerPrestamosEnMora());
        model.addAttribute("totalEquipos",     equipoService.total());
        model.addAttribute("disponibles",      equipoService.disponiblesN());
        model.addAttribute("prestados",        prestamoService.countActivos());
        model.addAttribute("mantenimiento",    equipoService.mantenimientoN());
        return "dashboard";
    }

    @GetMapping("/registrar")
    public String mostrarFormulario(Model model) {
        model.addAttribute("equiposDisponibles", equipoService.disponibles());
        model.addAttribute("docentes",           docenteService.listarTodos());
        return "formulario-prestamo";
    }

    @PostMapping("/registrar")
    public String procesarPrestamo(
            @RequestParam String dniDocente,
            @RequestParam String codigoEquipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaLimite,
            RedirectAttributes ra) {
        try {
            prestamoService.registrarSalida(dniDocente, codigoEquipo, fechaLimite);
            ra.addFlashAttribute("exito", "Préstamo registrado correctamente.");
            return "redirect:/prestamos/dashboard";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/prestamos/registrar";
        }
    }

    @PostMapping("/devolver/{id}")
    public String registrarDevolucion(@PathVariable Long id, RedirectAttributes ra) {
        try {
            prestamoService.registrarDevolucion(id);
            ra.addFlashAttribute("exito", "Devolución registrada correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/prestamos/dashboard";
    }

    @GetMapping("/reporte/excel")
    public ResponseEntity<InputStreamResource> descargarExcel() {
        try {
            ByteArrayInputStream stream = prestamoService.generarReporteExcelMorosidad();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=AlertasMorosidad_MediaLab.xlsx");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(stream));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
