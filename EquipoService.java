package com.medialab.control.service;

import com.google.common.base.Preconditions;
import com.medialab.control.model.Equipo;
import com.medialab.control.repository.EquipoRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de Equipos — SRP: solo lógica de inventario.
 * Usa Google Guava (Preconditions) y Apache Commons (StringUtils).
 */
@Service
@Transactional
public class EquipoService {

    private static final Logger log = LoggerFactory.getLogger(EquipoService.class);
    private final EquipoRepository repo;

    public EquipoService(EquipoRepository repo) { this.repo = repo; }

    public Equipo guardar(Equipo equipo) {
        Preconditions.checkNotNull(equipo, "Equipo no puede ser nulo");
        equipo.setCodigoPatrimonial(StringUtils.upperCase(StringUtils.trimToEmpty(equipo.getCodigoPatrimonial())));
        equipo.setNombre(StringUtils.trimToEmpty(equipo.getNombre()));
        Preconditions.checkArgument(StringUtils.isNotBlank(equipo.getCodigoPatrimonial()), "Código obligatorio");
        Preconditions.checkArgument(StringUtils.isNotBlank(equipo.getNombre()), "Nombre obligatorio");
        if (equipo.getEstado() == null || equipo.getEstado().isBlank()) equipo.setEstado("DISPONIBLE");
        Equipo saved = repo.save(equipo);
        log.info("Equipo guardado: {}", saved.getCodigoPatrimonial());
        return saved;
    }

    public void eliminar(Long id) {
        Equipo e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));
        Preconditions.checkState(!"PRESTADO".equals(e.getEstado()), "No se puede eliminar un equipo prestado");
        repo.deleteById(id);
        log.info("Equipo eliminado: {}", e.getCodigoPatrimonial());
    }

    @Transactional(readOnly = true)
    public List<Equipo> listar(String q) {
        return StringUtils.isBlank(q) ? repo.findAll() : repo.buscar(q.trim());
    }

    @Transactional(readOnly = true)
    public Optional<Equipo> buscarPorId(Long id) { return repo.findById(id); }

    @Transactional(readOnly = true)
    public List<Equipo> disponibles() { return repo.findByEstado("DISPONIBLE"); }

    @Transactional(readOnly = true)
    public long total()         { return repo.count(); }
    @Transactional(readOnly = true)
    public long disponiblesN()  { return repo.countByEstado("DISPONIBLE"); }
    @Transactional(readOnly = true)
    public long prestadosN()    { return repo.countByEstado("PRESTADO"); }
    @Transactional(readOnly = true)
    public long mantenimientoN(){ return repo.countByEstado("MANTENIMIENTO"); }
}
