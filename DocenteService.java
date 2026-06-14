package com.medialab.control.service;

import com.google.common.base.Preconditions;
import com.medialab.control.model.Docente;
import com.medialab.control.repository.DocenteRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de Docentes — SRP: solo lógica del padrón docente.
 */
@Service
@Transactional
public class DocenteService {

    private static final Logger log = LoggerFactory.getLogger(DocenteService.class);
    private final DocenteRepository repo;

    public DocenteService(DocenteRepository repo) { this.repo = repo; }

    public Docente guardar(Docente docente) {
        Preconditions.checkNotNull(docente, "Docente no puede ser nulo");
        String dniLimpio = StringUtils.trimToEmpty(docente.getDni());
        Preconditions.checkArgument(dniLimpio.length() == 8, "El DNI debe tener exactamente 8 dígitos");
        Preconditions.checkArgument(StringUtils.isNumeric(dniLimpio), "El DNI debe contener solo números");
        Preconditions.checkArgument(StringUtils.isNotBlank(docente.getNombres()), "El nombre es obligatorio");
        docente.setDni(dniLimpio);
        docente.setNombres(StringUtils.trimToEmpty(docente.getNombres()));
        docente.setFacultad(StringUtils.trimToEmpty(docente.getFacultad()));

        if (docente.getId() == null && repo.existsByDni(dniLimpio)) {
            throw new IllegalStateException("Ya existe un docente con el DNI: " + dniLimpio);
        }
        Docente saved = repo.save(docente);
        log.info("Docente guardado: {} - {}", saved.getDni(), saved.getNombres());
        return saved;
    }

    public void eliminar(Long id) {
        repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Docente no encontrado"));
        repo.deleteById(id);
        log.info("Docente eliminado: {}", id);
    }

    @Transactional(readOnly = true)
    public List<Docente> listar(String q) {
        return StringUtils.isBlank(q) ? repo.findAll() : repo.buscar(q.trim());
    }

    @Transactional(readOnly = true)
    public Optional<Docente> buscarPorId(Long id) { return repo.findById(id); }

    @Transactional(readOnly = true)
    public Optional<Docente> buscarPorDni(String dni) { return repo.findByDni(StringUtils.trimToEmpty(dni)); }

    @Transactional(readOnly = true)
    public List<Docente> listarTodos() { return repo.findAll(); }
}
