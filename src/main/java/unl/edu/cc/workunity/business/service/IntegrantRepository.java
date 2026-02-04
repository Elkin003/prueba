package unl.edu.cc.workunity.business.service;

import jakarta.ejb.Stateless;
import unl.edu.cc.workunity.domain.Entidad;
import unl.edu.cc.workunity.domain.Integrante;
import unl.edu.cc.workunity.domain.Proyecto;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria para la gestión de Integrantes
 */
@Stateless
public class IntegrantRepository {
    private static final Map<Long, Integrante> tableIntegrantBD;
    private static Long sequenceId;

    static {
        tableIntegrantBD = new TreeMap<>();
        sequenceId = 1L;
    }

    public IntegrantRepository() {
    }

    /**
     * Guarda o actualiza un integrante
     */
    public Integrante save(Integrante integrante) {
        if (integrante.getId() == null) {
            sequenceId++;
            integrante.setId(sequenceId);
            tableIntegrantBD.put(integrante.getId(), integrante);
        } else {
            tableIntegrantBD.replace(integrante.getId(), integrante);
        }
        return tableIntegrantBD.get(integrante.getId());
    }

    /**
     * Busca un integrante por ID
     */
    public Integrante find(Long id) throws EntityNotFoundException {
        Integrante integrante = tableIntegrantBD.get(id);
        if (integrante == null) {
            throw new EntityNotFoundException("Integrante no encontrado con [" + id + "]");
        }
        return integrante;
    }

    /**
     * Retorna todos los integrantes
     */
    public List<Integrante> findAll() {
        return new ArrayList<>(tableIntegrantBD.values());
    }

    /**
     * Elimina un integrante
     */
    public void delete(Long id) throws EntityNotFoundException {
        Integrante integrante = find(id);
        tableIntegrantBD.remove(id);
    }

    /**
     * Busca integrantes de un proyecto
     */
    public List<Integrante> findByProject(Proyecto proyecto) {
        return tableIntegrantBD.values().stream()
                .filter(i -> Objects.equals(i.getProyecto(), proyecto))
                .collect(Collectors.toList());
    }

    /**
     * Busca integrantes asociados a una entidad
     */
    public List<Integrante> findByEntity(Entidad entidad) {
        return tableIntegrantBD.values().stream()
                .filter(i -> Objects.equals(i.getEntidad(), entidad))
                .collect(Collectors.toList());
    }

    /**
     * Busca un integrante específico por proyecto y entidad
     */
    public Integrante findByProjectAndEntity(Proyecto proyecto, Entidad entidad) throws EntityNotFoundException {
        Integrante integranteFound = tableIntegrantBD.values().stream()
                .filter(i -> Objects.equals(i.getProyecto(), proyecto) && Objects.equals(i.getEntidad(), entidad))
                .findFirst()
                .orElse(null);

        if (integranteFound == null) {
            throw new EntityNotFoundException(
                    "Integrante no encontrado para el proyecto [" + proyecto.getNombre() +
                            "] y entidad [" + entidad.getNombre() + "]");
        }
        return integranteFound;
    }

    public static int getTableIntegrantSize() {
        return tableIntegrantBD.size();
    }
}
