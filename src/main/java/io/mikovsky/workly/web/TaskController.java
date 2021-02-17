package io.mikovsky.workly.web;

import io.mikovsky.workly.domain.Task;
import io.mikovsky.workly.domain.User;
import io.mikovsky.workly.services.TaskService;
import io.mikovsky.workly.web.payload.CreateTaskRequest;
import io.mikovsky.workly.web.payload.UpdateTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<Task> getTasks(Principal principal) {
        User user = User.fromPrincipal(principal);
        return taskService.getTasksByUserId(user.getId());
    }

    @PostMapping
    public Task createTask(@Valid @RequestBody CreateTaskRequest request, Principal principal) {
        User user = User.fromPrincipal(principal);
        return taskService.saveTask(request.toTask(user.getId()));
    }

    @PutMapping("/{taskId}")
    public Task updateTask(@PathVariable Long taskId, @Valid @RequestBody UpdateTaskRequest request, Principal principal) {
        User user = User.fromPrincipal(principal);
        Task task = taskService.getTaskByIdAndUserId(taskId, user.getId());
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setCompleted(request.getCompleted());
        task.setDueDate(request.getDueDate());
        return taskService.saveTask(task);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId, Principal principal) {
        User user = User.fromPrincipal(principal);
        Task task = taskService.getTaskByIdAndUserId(taskId, user.getId());
        taskService.deleteTaskById(task.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
