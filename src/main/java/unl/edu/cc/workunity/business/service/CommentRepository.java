package unl.edu.cc.workunity.business.service;

import jakarta.ejb.Stateless;
import unl.edu.cc.workunity.domain.Comentario;
import unl.edu.cc.workunity.domain.Tarea;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria para la gestión de Comentarios
 */
@Stateless
public class CommentRepository {
    private static final Map<Long, Comentario> tableCommentBD;
    private static Long sequenceId;

    static {
        tableCommentBD = new TreeMap<>();
        sequenceId = 1L;
    }

    public CommentRepository() {
    }

    /**
     * Guarda o actualiza un comentario
     */
    public Comentario save(Comentario comentario) {
        if (comentario.getId() == null) {
            sequenceId++;
            comentario.setId(sequenceId);
            tableCommentBD.put(comentario.getId(), comentario);
        } else {
            tableCommentBD.replace(comentario.getId(), comentario);
        }
        return tableCommentBD.get(comentario.getId());
    }

    /**
     * Busca un comentario por ID
     */
    public Comentario find(Long id) throws EntityNotFoundException {
        Comentario comentario = tableCommentBD.get(id);
        if (comentario == null) {
            throw new EntityNotFoundException("Comentario no encontrado con [" + id + "]");
        }
        return comentario;
    }

    /**
     * Busca comentarios de una tarea
     */
    public List<Comentario> findByTask(Tarea tarea) {
        return tableCommentBD.values().stream()
                .filter(c -> Objects.equals(c.getTarea(), tarea))
                .collect(Collectors.toList());
    }

    /**
     * Elimina un comentario
     */
    public void delete(Long id) throws EntityNotFoundException {
        Comentario comentario = find(id);
        tableCommentBD.remove(id);
    }

    public static int getTableCommentSize() {
        return tableCommentBD.size();
    }
}
