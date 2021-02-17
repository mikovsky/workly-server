package io.mikovsky.workly.services;

import io.mikovsky.workly.domain.Task;
import io.mikovsky.workly.exceptions.ErrorCode;
import io.mikovsky.workly.exceptions.WorklyException;
import io.mikovsky.workly.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findAllByUserId(userId);
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public Task getTaskByIdAndUserId(Long taskId, Long userId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> WorklyException.of(HttpStatus.NOT_FOUND, ErrorCode.TASK_NOT_FOUND));
    }

    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }

}
