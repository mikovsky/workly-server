package com.mikovskycloud.workly.services;

import com.mikovskycloud.workly.domain.Task;
import com.mikovskycloud.workly.domain.User;
import com.mikovskycloud.workly.exceptions.WorklyException;
import com.mikovskycloud.workly.repositories.TaskRepository;
import com.mikovskycloud.workly.web.v1.tasks.payload.CreateTaskRequest;
import com.mikovskycloud.workly.web.v1.tasks.payload.UpdateTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateTaskService {

    private final TaskRepository taskRepository;

    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findAllByUserId(userId);
    }

    public Task saveNewTask(CreateTaskRequest request, User principal) {
        Task task = request.toTask(principal.getId());
        return save(task);
    }

    public Task updateTask(Long taskId, UpdateTaskRequest request, User principal) {
        if (request.isEmpty()) throw WorklyException.emptyRequest();

        Task task = getTaskByIdAndUserId(taskId, principal.getId());
        if (request.getName() != null) task.setName(request.getName());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getCompleted() != null) task.setCompleted(request.getCompleted());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
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
