package com.medialab.control.service;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.medialab.control.model.Docente;
import com.medialab.control.model.Equipo;
import com.medialab.control.model.Prestamo;
import com.medialab.control.repository.DocenteRepository;
import com.medialab.control.repository.EquipoRepository;
import com.medialab.control.repository.PrestamoRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Servicio de Préstamos.
 * Librerías: Google Guava, Apache Commons, Apache POI, Logback.
 */
@Service
@Transactional
public class PrestamoService {

    private static final Logger log = LoggerFactory.getLogger(PrestamoService.class);

    private final PrestamoRepository prestamoRepo;
    private final EquipoRepository equipoRepo;
    private final DocenteRepository docenteRepo;

    // Google Guava LoadingCache — evita queries repetidos en ventanilla
    private final LoadingCache<String, Boolean> cacheDisponibilidad;

    public PrestamoService(PrestamoRepository prestamoRepo,
                           EquipoRepository equipoRepo,
                           DocenteRepository docenteRepo) {
        this.prestamoRepo = prestamoRepo;
        this.equipoRepo   = equipoRepo;
        this.docenteRepo  = docenteRepo;

        this.cacheDisponibilidad = CacheBuilder.newBuilder()
                .expireAfterWrite(3, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Boolean load(String codigo) {
                        return equipoRepo.findByCodigoPatrimonial(codigo)
                                .map(e -> "DISPONIBLE".equals(e.getEstado()))
                                .orElse(false);
                    }
                });
    }

    // -------------------------------------------------------
    // CRUD básico
    // -------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Prestamo> obtenerPrestamosActivos() {
        return prestamoRepo.findPrestamosActivos();
    }

    @Transactional(readOnly = true)
    public List<Prestamo> obtenerPrestamosEnMora() {
        return prestamoRepo.findPrestamosEnMora();
    }

    @Transactional(readOnly = true)
    public List<Prestamo> listarTodos() {
        return prestamoRepo.findAll();
    }

    // -------------------------------------------------------
    // Registrar salida
    // -------------------------------------------------------
    public Prestamo registrarSalida(String dniDocente, String codigoEquipo, LocalDate fechaLimite) {
        String dniLimpio     = StringUtils.trimToEmpty(dniDocente);
        String codigoLimpio  = StringUtils.upperCase(StringUtils.trimToEmpty(codigoEquipo));

        log.info("Registrando préstamo — DNI: {}, Código: {}", dniLimpio, codigoLimpio);

        Preconditions.checkArgument(dniLimpio.length() == 8,       "El DNI debe tener 8 dígitos.");
        Preconditions.checkArgument(StringUtils.isNumeric(dniLimpio), "El DNI debe contener solo números.");
        Preconditions.checkArgument(!codigoLimpio.isEmpty(),        "El código patrimonial es obligatorio.");

        try {
            boolean disponible = cacheDisponibilidad.get(codigoLimpio);
            Preconditions.checkState(disponible, "El equipo no está disponible o no existe.");

            Docente docente = docenteRepo.findByDni(dniLimpio)
                    .orElseThrow(() -> new IllegalArgumentException("Docente con DNI " + dniLimpio + " no figura en el padrón."));

            Equipo equipo = equipoRepo.findByCodigoPatrimonial(codigoLimpio)
                    .orElseThrow(() -> new IllegalArgumentException("Código de equipo inválido."));

            Prestamo p = new Prestamo();
            p.setDocente(docente);
            p.setEquipo(equipo);
            p.setFechaSalida(LocalDate.now());
            p.setFechaLimite(fechaLimite != null ? fechaLimite : LocalDate.now().plusDays(1));

            equipo.setEstado("PRESTADO");
            equipoRepo.save(equipo);
            Prestamo saved = prestamoRepo.save(p);

            cacheDisponibilidad.invalidate(codigoLimpio);
            log.info("Préstamo registrado con ID: {}", saved.getId());
            return saved;

        } catch (Exception e) {
            log.error("Fallo al registrar préstamo: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Registrar devolución
    // -------------------------------------------------------
    public void registrarDevolucion(Long prestamoId) {
        Prestamo p = prestamoRepo.findById(prestamoId)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado."));
        Preconditions.checkState(p.getFechaDevolucion() == null, "Este préstamo ya fue devuelto.");

        p.setFechaDevolucion(LocalDate.now());
        p.getEquipo().setEstado("DISPONIBLE");
        equipoRepo.save(p.getEquipo());
        prestamoRepo.save(p);
        cacheDisponibilidad.invalidate(p.getEquipo().getCodigoPatrimonial());
        log.info("Devolución registrada para préstamo ID: {}", prestamoId);
    }

    // -------------------------------------------------------
    // Reporte Excel — Apache POI
    // -------------------------------------------------------
    public ByteArrayInputStream generarReporteExcelMorosidad() throws IOException {
        List<Prestamo> activos = prestamoRepo.findPrestamosActivos();

        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Alerta Morosidad MediaLab");

            // Estilo encabezado
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] cols = {"ID", "Docente", "DNI", "Equipo", "Código", "Fecha Salida", "Fecha Límite", "Días Mora"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headerStyle);
            }

            // Estilo mora
            CellStyle moraStyle = wb.createCellStyle();
            Font moraFont = wb.createFont();
            moraFont.setColor(IndexedColors.RED.getIndex());
            moraFont.setBold(true);
            moraStyle.setFont(moraFont);

            int row = 1;
            for (Prestamo p : activos) {
                long mora = p.calcularDiasMora();
                Row r = sheet.createRow(row++);
                r.createCell(0).setCellValue(p.getId());
                r.createCell(1).setCellValue(p.getDocente().getNombres());
                r.createCell(2).setCellValue(p.getDocente().getDni());
                r.createCell(3).setCellValue(p.getEquipo().getNombre());
                r.createCell(4).setCellValue(p.getEquipo().getCodigoPatrimonial());
                r.createCell(5).setCellValue(p.getFechaSalida().toString());
                r.createCell(6).setCellValue(p.getFechaLimite().toString());
                Cell moraCell = r.createCell(7);
                moraCell.setCellValue(mora > 0 ? mora + " días" : "—");
                if (mora > 0) moraCell.setCellStyle(moraStyle);
            }

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);
            wb.write(out);
            log.info("Reporte Excel generado con {} filas", row - 1);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // -------------------------------------------------------
    // Stats para dashboard
    // -------------------------------------------------------
    @Transactional(readOnly = true)
    public long countActivos() { return prestamoRepo.countByFechaDevolucionIsNull(); }
}
