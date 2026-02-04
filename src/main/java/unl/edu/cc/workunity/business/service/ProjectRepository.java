package unl.edu.cc.workunity.business.service;

import jakarta.ejb.Stateless;
import unl.edu.cc.workunity.domain.Entidad;
import unl.edu.cc.workunity.domain.Proyecto;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria para la gestión de Proyectos
 */
@Stateless
public class ProjectRepository {
    private static final Map<Long, Proyecto> tableProjectBD;
    private static Long sequenceId;

    static {
        tableProjectBD = new TreeMap<>();
        sequenceId = 1L;
    }

    public ProjectRepository() {
    }

    /**
     * Guarda o actualiza un proyecto
     */
    public Proyecto save(Proyecto proyecto) {
        if (proyecto.getId() == null) {
            sequenceId++;
            proyecto.setId(sequenceId);
            tableProjectBD.put(proyecto.getId(), proyecto);
        } else {
            tableProjectBD.replace(proyecto.getId(), proyecto);
        }
        return tableProjectBD.get(proyecto.getId());
    }

    /**
     * Busca un proyecto por ID
     */
    public Proyecto find(Long id) throws EntityNotFoundException {
        Proyecto proyecto = tableProjectBD.get(id);
        if (proyecto == null) {
            throw new EntityNotFoundException("Proyecto no encontrado con [" + id + "]");
        }
        return proyecto;
    }

    /**
     * Retorna todos los proyectos
     */
    public List<Proyecto> findAll() {
        return new ArrayList<>(tableProjectBD.values());
    }

    /**
     * Elimina un proyecto
     */
    public void delete(Long id) throws EntityNotFoundException {
        Proyecto proyecto = find(id);
        tableProjectBD.remove(id);
    }

    /**
     * Busca proyectos por creador
     */
    public List<Proyecto> findByCreator(Entidad creador) {
        return tableProjectBD.values().stream()
                .filter(p -> Objects.equals(p.getCreador(), creador))
                .collect(Collectors.toList());
    }

    public static int getTableProjectSize() {
        return tableProjectBD.size();
    }
}
