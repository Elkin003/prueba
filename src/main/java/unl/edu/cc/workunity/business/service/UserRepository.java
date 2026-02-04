package unl.edu.cc.workunity.business.service;

import jakarta.ejb.Stateless;
import unl.edu.cc.workunity.domain.security.User;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.*;

/**
 * Repositorio en memoria para la gestión de usuarios (credenciales)
 */
@Stateless
public class UserRepository {
    private static final Map<Long, User> tableUserBD;
    private static Long sequenceId;

    static {
        tableUserBD = new TreeMap<>();
        sequenceId = 1L;
    }

    public UserRepository() {
    }

    /**
     * Guarda o actualiza un usuario
     */
    public User save(User user) {
        if (user.getId() == null) {
            sequenceId++;
            user.setId(sequenceId);
            tableUserBD.put(user.getId(), user);
        } else {
            tableUserBD.replace(user.getId(), user);
        }
        return tableUserBD.get(user.getId());
    }

    /**
     * Busca un usuario por ID
     */
    public User find(Long id) throws EntityNotFoundException {
        User user = tableUserBD.get(id);
        if (user == null) {
            throw new EntityNotFoundException("Usuario no encontrado con ID [" + id + "]");
        }
        return user;
    }

    /**
     * Busca un usuario por nombre de usuario
     */
    public User find(String name) throws EntityNotFoundException {
        User userFound = null;
        for (Map.Entry<Long, User> entry : tableUserBD.entrySet()) {
            User user = entry.getValue();
            if (Objects.equals(user.getName(), name)) {
                userFound = entry.getValue();
                break;
            }
        }
        if (userFound == null) {
            throw new EntityNotFoundException("Usuario no encontrado con nombre [" + name + "]");
        }
        return userFound;
    }

    /**
     * Retorna todos los usuarios
     */
    public List<User> findAll() {
        return new ArrayList<>(tableUserBD.values());
    }

    public static int getTableUserSize() {
        return tableUserBD.size();
    }
}
