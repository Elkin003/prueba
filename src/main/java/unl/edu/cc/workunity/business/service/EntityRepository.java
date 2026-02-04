package unl.edu.cc.workunity.business.service;

import jakarta.ejb.Stateless;
import unl.edu.cc.workunity.domain.Entidad;
import unl.edu.cc.workunity.domain.security.User;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.*;

/**
 * Repositorio en memoria para la gestión de Entidades (perfiles de usuario)
 */
@Stateless
public class EntityRepository {
    private static final Map<Long, Entidad> tableEntityBD;
    private static Long sequenceId;

    static {
        tableEntityBD = new TreeMap<>();
        sequenceId = 1L;
    }

    public EntityRepository() {
    }

    /**
     * Guarda o actualiza una entidad
     */
    public Entidad save(Entidad entidad) {
        if (entidad.getId() == null) {
            sequenceId++;
            entidad.setId(sequenceId);
            tableEntityBD.put(entidad.getId(), entidad);
        } else {
            tableEntityBD.replace(entidad.getId(), entidad);
        }
        return tableEntityBD.get(entidad.getId());
    }

    /**
     * Busca una entidad por ID
     */
    public Entidad find(Long id) throws EntityNotFoundException {
        Entidad entidad = tableEntityBD.get(id);
        if (entidad == null) {
            throw new EntityNotFoundException("Entidad no encontrada con [" + id + "]");
        }
        return entidad;
    }

    /**
     * Retorna todas las entidades
     */
    public List<Entidad> findAll() {
        return new ArrayList<>(tableEntityBD.values());
    }

    /**
     * Busca una entidad por usuario
     */
    public Entidad findByUser(User usuario) throws EntityNotFoundException {
        Entidad entidadFound = null;
        for (Map.Entry<Long, Entidad> entry : tableEntityBD.entrySet()) {
            Entidad entidad = entry.getValue();
            if (Objects.equals(entidad.getUsuario(), usuario)) {
                entidadFound = entry.getValue();
                break;
            }
        }
        if (entidadFound == null) {
            throw new EntityNotFoundException("Entidad no encontrada para el usuario [" + usuario.getName() + "]");
        }
        return entidadFound;
    }

    public static int getTableEntitySize() {
        return tableEntityBD.size();
    }
}
