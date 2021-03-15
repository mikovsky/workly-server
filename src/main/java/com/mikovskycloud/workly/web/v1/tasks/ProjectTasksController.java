package com.mikovskycloud.workly.web.v1.tasks;

import com.mikovskycloud.workly.domain.User;
import com.mikovskycloud.workly.services.ProjectTaskService;
import com.mikovskycloud.workly.validation.RequestValidator;
import com.mikovskycloud.workly.web.v1.tasks.payload.CreateProjectTaskRequest;
import com.mikovskycloud.workly.web.v1.tasks.payload.ProjectTaskResponse;
import com.mikovskycloud.workly.web.v1.tasks.payload.UpdateProjectTaskRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
@Api(tags = "Project Tasks")
public class ProjectTasksController {

    private final ProjectTaskService projectTaskService;

    private final RequestValidator requestValidator;

    @GetMapping
    @ApiOperation(value = "Get all Tasks from the Project", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProjectTaskResponse> getTasksFromProject(@PathVariable Long projectId, Principal principal) {
        return projectTaskService.findTasksWithProjectId(projectId, User.fromPrincipal(principal));
    }

    @PostMapping
    @ApiOperation(value = "Add new Task to the Project", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProjectTaskResponse addTaskToProject(@Valid @RequestBody CreateProjectTaskRequest request,
                                                @PathVariable Long projectId,
                                                BindingResult bindingResult,
                                                Principal principal) {
        requestValidator.throwIfRequestIsInvalid(bindingResult);
        return projectTaskService.saveTask(request, projectId, User.fromPrincipal(principal));
    }

    @PutMapping("/{taskId}")
    @ApiOperation(value = "Update Task from the Project", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProjectTaskResponse updateTaskFromProject(@Valid @RequestBody UpdateProjectTaskRequest request,
                                                     @PathVariable Long projectId,
                                                     @PathVariable Long taskId,
                                                     BindingResult bindingResult,
                                                     Principal principal) {
        requestValidator.throwIfRequestIsInvalid(bindingResult);
        return projectTaskService.updateTask(request, projectId, taskId, User.fromPrincipal(principal));
    }

    @DeleteMapping("/{taskId}")
    @ApiOperation(value = "Delete Task from the Project")
    public ResponseEntity<Void> deleteTaskFromProject(@PathVariable Long projectId,
                                                      @PathVariable Long taskId,
                                                      Principal principal) {
        return projectTaskService.deleteTask(projectId, taskId, User.fromPrincipal(principal));
    }

}
