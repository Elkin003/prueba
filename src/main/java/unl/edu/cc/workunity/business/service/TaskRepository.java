package unl.edu.cc.workunity.business.service;

import jakarta.ejb.Stateless;
import unl.edu.cc.workunity.domain.Integrante;
import unl.edu.cc.workunity.domain.Proyecto;
import unl.edu.cc.workunity.domain.Tarea;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria para la gestión de Tareas
 */
@Stateless
public class TaskRepository {
    private static final Map<Long, Tarea> tableTaskBD;
    private static Long sequenceId;

    static {
        tableTaskBD = new TreeMap<>();
        sequenceId = 1L;
    }

    public TaskRepository() {
    }

    /**
     * Guarda o actualiza una tarea
     */
    public Tarea save(Tarea tarea) {
        if (tarea.getId() == null) {
            sequenceId++;
            tarea.setId(sequenceId);
            tableTaskBD.put(tarea.getId(), tarea);
        } else {
            tableTaskBD.replace(tarea.getId(), tarea);
        }
        return tableTaskBD.get(tarea.getId());
    }

    /**
     * Busca una tarea por ID
     */
    public Tarea find(Long id) throws EntityNotFoundException {
        Tarea tarea = tableTaskBD.get(id);
        if (tarea == null) {
            throw new EntityNotFoundException("Tarea no encontrada con [" + id + "]");
        }
        return tarea;
    }

    /**
     * Retorna todas las tareas
     */
    public List<Tarea> findAll() {
        return new ArrayList<>(tableTaskBD.values());
    }

    /**
     * Elimina una tarea
     */
    public void delete(Long id) throws EntityNotFoundException {
        Tarea tarea = find(id);
        tableTaskBD.remove(id);
    }

    /**
     * Busca tareas por proyecto
     */
    public List<Tarea> findByProject(Proyecto proyecto) {
        return tableTaskBD.values().stream()
                .filter(t -> Objects.equals(t.getProyecto(), proyecto))
                .collect(Collectors.toList());
    }

    /**
     * Busca tareas asignadas a un integrante
     */
    public List<Tarea> findByIntegrante(Integrante integrante) {
        return tableTaskBD.values().stream()
                .filter(t -> Objects.equals(t.getIntegranteAsignado(), integrante))
                .collect(Collectors.toList());
    }

    public static int getTableTaskSize() {
        return tableTaskBD.size();
    }
}
