package io.mikovsky.workly.web.v1.tasks;

import io.mikovsky.workly.domain.Task;
import io.mikovsky.workly.domain.User;
import io.mikovsky.workly.services.TaskService;
import io.mikovsky.workly.web.v1.tasks.payload.CreateTaskRequest;
import io.mikovsky.workly.web.v1.tasks.payload.TaskResponse;
import io.mikovsky.workly.web.v1.tasks.payload.UpdateTaskRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
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

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Api(tags = "Tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @ApiOperation(value = "Get all Tasks for currently logged user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TaskResponse> getTasks(Principal principal) {
        User user = User.fromPrincipal(principal);
        return StreamEx.of(taskService.getTasksByUserId(user.getId()))
                .map(TaskResponse::fromTask)
                .toList();
    }

    @PostMapping
    @ApiOperation(value = "Create new Task", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest request, Principal principal) {
        Task task = taskService.saveNewTask(request, User.fromPrincipal(principal));
        return TaskResponse.fromTask(task);
    }

    @PutMapping("/{taskId}")
    @ApiOperation(value = "Update Task with given ID", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskResponse updateTask(@PathVariable Long taskId, @Valid @RequestBody UpdateTaskRequest request, Principal principal) {
        Task updatedTask = taskService.updateTask(taskId, request, User.fromPrincipal(principal));
        return TaskResponse.fromTask(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    @ApiOperation(value = "Delete Task with given ID")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, Principal principal) {
        taskService.deleteTask(taskId, User.fromPrincipal(principal));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
