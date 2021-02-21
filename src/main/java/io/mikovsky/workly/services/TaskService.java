package io.mikovsky.workly.services;

import io.mikovsky.workly.domain.Task;
import io.mikovsky.workly.domain.User;
import io.mikovsky.workly.exceptions.WorklyException;
import io.mikovsky.workly.repositories.TaskRepository;
import io.mikovsky.workly.web.v1.payload.CreateTaskRequest;
import io.mikovsky.workly.web.v1.payload.UpdateTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findAllByUserId(userId);
    }

    public Task saveNewTask(CreateTaskRequest request, User principal) {
        if (taskRepository.existsByNameAndUserId(request.getName(), principal.getId())) {
            throw WorklyException.taskAlreadyExists();
        }

        Task task = request.toTask(principal.getId());
        return save(task);
    }

    public Task updateTask(Long taskId, UpdateTaskRequest request, User principal) {
        if (taskRepository.existsByNameAndUserId(request.getName(), principal.getId())) {
            throw WorklyException.taskAlreadyExists();
        }

        Task task = getTaskByIdAndUserId(taskId, principal.getId());
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setCompleted(request.getCompleted());
        task.setDueDate(request.getDueDate());
        return update(task);
    }

    public void deleteTask(Long taskId, User principal) {
        if (!taskRepository.existsByIdAndUserId(taskId, principal.getId())) {
            throw WorklyException.taskNotFound();
        }

        taskRepository.deleteById(taskId);
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

    public Task getTaskByIdAndUserId(Long taskId, Long userId) {
        return taskRepository.findByIdAndUserId(taskId, userId).orElseThrow(WorklyException::taskNotFound);
    }

}
