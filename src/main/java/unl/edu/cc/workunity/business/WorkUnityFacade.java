package unl.edu.cc.workunity.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.workunity.business.service.*;
import unl.edu.cc.workunity.domain.*;
import unl.edu.cc.workunity.business.service.*;
import unl.edu.cc.workunity.domain.*;
import unl.edu.cc.workunity.domain.enums.EstadoTarea;
import unl.edu.cc.workunity.exception.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;

@Stateless
public class WorkUnityFacade {

    @Inject
    private EntityRepository entityRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private TaskRepository taskRepository;

    @Inject
    private IntegrantRepository integrantRepository;

    @Inject
    private CommentRepository commentRepository;

    /**
     * Crea un nuevo proyecto usando la lógica de negocio de Entidad
     * Automáticamente crea el integrante líder
     */
    public Proyecto createProject(Entidad creador, String nombre, String descripcion, LocalDate fechaLimite) {
        creador.crearProyecto(nombre, descripcion, fechaLimite);

        List<Proyecto> proyectos = creador.getProyectos();
        Proyecto proyecto = proyectos.get(proyectos.size() - 1);

        proyecto = projectRepository.save(proyecto);

        List<Integrante> miembros = proyecto.getMiembros();
        if (!miembros.isEmpty()) {
            Integrante lider = miembros.get(miembros.size() - 1);
            integrantRepository.save(lider);
        }

        entityRepository.save(creador);

        return proyecto;
    }

    /**
     * Actualiza la información de un proyecto
     */
    public Proyecto updateProject(Long projectId, String nombre, String descripcion, LocalDate fechaLimite)
            throws EntityNotFoundException {
        Proyecto proyecto = projectRepository.find(projectId);
        proyecto.setNombre(nombre);
        proyecto.setDescripcion(descripcion);
        proyecto.setFechaLimite(fechaLimite);
        return projectRepository.save(proyecto);
    }

    /**
     * Elimina un proyecto y todas sus dependencias (tareas, integrantes,
     * comentarios)
     */
    public void deleteProject(Long projectId) throws EntityNotFoundException {
        Proyecto proyecto = projectRepository.find(projectId);

        List<Tarea> tareas = taskRepository.findByProject(proyecto);
        for (Tarea tarea : tareas) {
            List<Comentario> comentarios = commentRepository.findByTask(tarea);
            for (Comentario comentario : comentarios) {
                commentRepository.delete(comentario.getId());
            }
            taskRepository.delete(tarea.getId());
        }

        List<Integrante> integrantes = integrantRepository.findByProject(proyecto);
        for (Integrante integrante : integrantes) {
            integrantRepository.delete(integrante.getId());
        }

        projectRepository.delete(projectId);
    }

    /**
     * Encuentra todos los proyectos de una entidad
     */
    public List<Proyecto> findAllProjectsByEntity(Entidad entidad) {
        return entidad.listarProyectos();
    }

    /**
     * Encuentra un proyecto por ID
     */
    public Proyecto findProject(Long id) throws EntityNotFoundException {
        return projectRepository.find(id);
    }

    /**
     * Crea una nueva tarea usando la lógica de negocio de Integrante
     * Solo el líder puede crear tareas
     */
    public Tarea createTask(Integrante lider, String titulo, String descripcion, LocalDate fechaLimite) {
        Tarea tarea = lider.crearTarea(titulo, descripcion, fechaLimite);

        tarea = taskRepository.save(tarea);

        projectRepository.save(lider.getProyecto());

        return tarea;
    }

    /**
     * Asigna una tarea a un integrante
     */
    public Tarea assignTask(Integrante lider, Tarea tarea, Integrante asignado) {
        lider.asignarTarea(tarea, asignado);

        taskRepository.save(tarea);
        integrantRepository.save(asignado);

        return tarea;
    }

    /**
     * Marca una tarea como entregada
     */
    public void deliverTask(Tarea tarea) {
        tarea.entregar();
        taskRepository.save(tarea);
    }

    /**
     * Cambia el estado de una tarea
     */
    public Tarea changeTaskStatus(Long taskId, EstadoTarea nuevoEstado) throws EntityNotFoundException {
        Tarea tarea = taskRepository.find(taskId);
        tarea.setEstado(nuevoEstado);
        return taskRepository.save(tarea);
    }

    /**
     * Actualiza una tarea
     */
    public Tarea updateTask(Long taskId, String titulo, String descripcion, LocalDate fechaLimite)
            throws EntityNotFoundException {
        Tarea tarea = taskRepository.find(taskId);
        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);
        tarea.setFechaLimite(fechaLimite);
        return taskRepository.save(tarea);
    }

    /**
     * Elimina una tarea y sus comentarios
     */
    public void deleteTask(Long taskId) throws EntityNotFoundException {
        Tarea tarea = taskRepository.find(taskId);

        List<Comentario> comentarios = commentRepository.findByTask(tarea);
        for (Comentario comentario : comentarios) {
            commentRepository.delete(comentario.getId());
        }

        taskRepository.delete(taskId);
    }

    /**
     * Encuentra todas las tareas de un proyecto
     */
    public List<Tarea> findTasksByProject(Proyecto proyecto) {
        return taskRepository.findByProject(proyecto);
    }

    /**
     * Encuentra una tarea por ID
     */
    public Tarea findTask(Long id) throws EntityNotFoundException {
        return taskRepository.find(id);
    }

    /**
     * Agrega un nuevo miembro a un proyecto
     */
    public Integrante addMemberToProject(Integrante lider, Proyecto proyecto, Entidad nuevaEntidad) {
        lider.agregarIntegrante(nuevaEntidad);

        List<Integrante> miembros = proyecto.getMiembros();
        Integrante nuevoIntegrante = miembros.get(miembros.size() - 1);

        nuevoIntegrante = integrantRepository.save(nuevoIntegrante);

        entityRepository.save(nuevaEntidad);
        projectRepository.save(proyecto);

        return nuevoIntegrante;
    }

    /**
     * Elimina un miembro de un proyecto
     * Las tareas asignadas quedan sin asignar
     */
    public void removeMemberFromProject(Long integranteId) throws EntityNotFoundException {
        Integrante integrante = integrantRepository.find(integranteId);

        List<Tarea> tareas = taskRepository.findByIntegrante(integrante);
        for (Tarea tarea : tareas) {
            tarea.setIntegranteAsignado(null);
            taskRepository.save(tarea);
        }

        integrantRepository.delete(integranteId);
    }

    /**
     * Encuentra todos los miembros de un proyecto
     */
    public List<Integrante> findMembersByProject(Proyecto proyecto) {
        return integrantRepository.findByProject(proyecto);
    }

    /**
     * Encuentra un integrante por proyecto y entidad
     */
    public Integrante findIntegrantByProjectAndEntity(Proyecto proyecto, Entidad entidad)
            throws EntityNotFoundException {
        return integrantRepository.findByProjectAndEntity(proyecto, entidad);
    }

    /**
     * Agrega un comentario a una tarea
     */
    public Comentario addCommentToTask(Tarea tarea, Integrante autor, String texto) {
        Comentario comentario = new Comentario(texto, autor);

        tarea.agregar(comentario);

        comentario = commentRepository.save(comentario);

        taskRepository.save(tarea);

        return comentario;
    }

    /**
     * Encuentra todos los comentarios de una tarea
     */
    public List<Comentario> findCommentsByTask(Tarea tarea) {
        return commentRepository.findByTask(tarea);
    }

    /**
     * Elimina un comentario
     */
    public void deleteComment(Long comentarioId) throws EntityNotFoundException {
        commentRepository.delete(comentarioId);
    }

    /**
     * Encuentra una entidad por ID
     */
    public Entidad findEntity(Long id) throws EntityNotFoundException {
        return entityRepository.find(id);
    }

    /**
     * Guarda o actualiza una entidad
     */
    public Entidad saveEntity(Entidad entidad) {
        return entityRepository.save(entidad);
    }
}
