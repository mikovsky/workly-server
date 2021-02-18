package io.mikovsky.workly.web.v1;

import io.mikovsky.workly.domain.Task;
import io.mikovsky.workly.domain.User;
import io.mikovsky.workly.services.TaskService;
import io.mikovsky.workly.web.v1.payload.CreateTaskRequest;
import io.mikovsky.workly.web.v1.payload.TaskResponse;
import io.mikovsky.workly.web.v1.payload.UpdateTaskRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Api(tags = "TaskController")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @ApiOperation(
            value = "Get all Tasks for currently logged user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<TaskResponse> getTasks(Principal principal) {
        User user = User.fromPrincipal(principal);
        return taskService.getTasksByUserId(user.getId())
                .stream()
                .map(TaskResponse::fromTask)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ApiOperation(
            value = "Create new Task",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest request, Principal principal) {
        User user = User.fromPrincipal(principal);
        Task task = taskService.saveTask(request.toTask(user.getId()));
        return TaskResponse.fromTask(task);
    }

    @PutMapping("/{taskId}")
    @ApiOperation(
            value = "Update Task with given ID",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TaskResponse updateTask(@PathVariable Long taskId, @Valid @RequestBody UpdateTaskRequest request, Principal principal) {
        User user = User.fromPrincipal(principal);
        Task task = taskService.getTaskByIdAndUserId(taskId, user.getId());
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setCompleted(request.getCompleted());
        task.setDueDate(request.getDueDate());
        Task updatedTask = taskService.saveTask(task);
        return TaskResponse.fromTask(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    @ApiOperation(value = "Delete Task with given ID")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, Principal principal) {
        User user = User.fromPrincipal(principal);
        Task task = taskService.getTaskByIdAndUserId(taskId, user.getId());
        taskService.deleteTaskById(task.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
