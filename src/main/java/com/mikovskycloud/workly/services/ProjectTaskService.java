package com.mikovskycloud.workly.services;

import com.mikovskycloud.workly.domain.Task;
import com.mikovskycloud.workly.domain.User;
import com.mikovskycloud.workly.exceptions.WorklyException;
import com.mikovskycloud.workly.repositories.ProjectRepository;
import com.mikovskycloud.workly.repositories.TaskRepository;
import com.mikovskycloud.workly.web.v1.tasks.payload.CreateProjectTaskRequest;
import com.mikovskycloud.workly.web.v1.tasks.payload.TaskResponse;
import com.mikovskycloud.workly.web.v1.tasks.payload.UpdateProjectTaskRequest;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectTaskService {

    private final ProjectRepository projectRepository;

    private final AuthorizeService authorizeService;

    private final TaskRepository taskRepository;

    public List<TaskResponse> findTasksWithProjectId(Long projectId, User user) {
        authorize(projectId, user.getId());

        List<Task> tasks = taskRepository.findAllByProjectId(projectId);
        return StreamEx.of(tasks)
                .map(TaskResponse::fromTask)
                .toList();
    }

    public TaskResponse saveTask(CreateProjectTaskRequest request, Long projectId, User user) {
        authorize(projectId, user.getId());

        Task savedTask = save(request.toTask());
        return TaskResponse.fromTask(savedTask);
    }

    public TaskResponse updateTask(UpdateProjectTaskRequest request, Long projectId, Long taskId, User user) {
        authorize(projectId, user.getId());
        if (request.isEmpty()) throw WorklyException.emptyRequest();

        Task task = findTaskById(taskId);
        if (request.getName() != null) task.setName(request.getName());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getCompleted() != null) task.setCompleted(request.getCompleted());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getSectionId() != null) task.setSectionId(request.getSectionId());
        if (request.getAssigneeId() != null) task.setAssigneeId(request.getAssigneeId());

        Task updatedTask = update(task);
        return TaskResponse.fromTask(updatedTask);
    }

    public ResponseEntity<Void> deleteTask(Long projectId, Long taskId, User user) {
        authorize(projectId, user.getId());

        if (!taskRepository.existsById(taskId)) {
            throw WorklyException.taskNotFound();
        }

        taskRepository.deleteById(taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public Task save(Task task) {
        Instant now = Instant.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        return taskRepository.save(task);
    }

    public Task update(Task task) {
        task.setUpdatedAt(Instant.now());
        return taskRepository.save(task);
    }

    private void authorize(Long projectId, Long userId) {
        if (!projectRepository.existsById(projectId)) {
            throw WorklyException.projectNotFound();
        }

        authorizeService.throwIfNotProjectMember(projectId, userId);
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(WorklyException::taskNotFound);
    }

}
