package com.mikovskycloud.workly.web.v1.tasks;

import com.mikovskycloud.workly.domain.Task;
import com.mikovskycloud.workly.domain.User;
import com.mikovskycloud.workly.services.PrivateTaskService;
import com.mikovskycloud.workly.validation.RequestValidator;
import com.mikovskycloud.workly.web.v1.tasks.payload.CreateTaskRequest;
import com.mikovskycloud.workly.web.v1.tasks.payload.PrivateTaskResponse;
import com.mikovskycloud.workly.web.v1.tasks.payload.UpdateTaskRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
@Api(tags = "Private Tasks")
public class PrivateTaskController {

    private final PrivateTaskService privateTaskService;

    private final RequestValidator requestValidator;

    @GetMapping
    @ApiOperation(value = "Get all Tasks for currently logged user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PrivateTaskResponse> getTasks(Principal principal) {
        User user = User.fromPrincipal(principal);
        return StreamEx.of(privateTaskService.getTasksByUserId(user.getId()))
                .map(PrivateTaskResponse::fromTask)
                .toList();
    }

    @PostMapping
    @ApiOperation(value = "Create new Task", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PrivateTaskResponse createTask(@Valid @RequestBody CreateTaskRequest request, BindingResult bindingResult, Principal principal) {
        requestValidator.throwIfRequestIsInvalid(bindingResult);
        Task task = privateTaskService.saveNewTask(request, User.fromPrincipal(principal));
        return PrivateTaskResponse.fromTask(task);
    }

    @PutMapping("/{taskId}")
    @ApiOperation(value = "Update Task with given ID", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PrivateTaskResponse updateTask(@PathVariable Long taskId,
                                          @Valid @RequestBody UpdateTaskRequest request,
                                          BindingResult bindingResult,
                                          Principal principal) {
        requestValidator.throwIfRequestIsInvalid(bindingResult);
        Task updatedTask = privateTaskService.updateTask(taskId, request, User.fromPrincipal(principal));
        return PrivateTaskResponse.fromTask(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    @ApiOperation(value = "Delete Task with given ID")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, Principal principal) {
        privateTaskService.deleteTask(taskId, User.fromPrincipal(principal));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
