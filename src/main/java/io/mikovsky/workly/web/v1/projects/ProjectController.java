package io.mikovsky.workly.web.v1.projects;

import io.mikovsky.workly.domain.User;
import io.mikovsky.workly.services.ProjectService;
import io.mikovsky.workly.web.v1.projects.payload.CreateProjectRequest;
import io.mikovsky.workly.web.v1.projects.payload.ProjectResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Api(tags = "Projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @ApiOperation(value = "Get All Projects for User")
    public List<ProjectResponse> getAllProjectsForUser(Principal principal) {
        User user = User.fromPrincipal(principal);
        return projectService.getProjectsForUser(user);
    }

    @PostMapping
    @ApiOperation(value = "Create Projects for User")
    public ProjectResponse createNewProjectForUser(@Valid @RequestBody CreateProjectRequest request, Principal principal) {
        User user = User.fromPrincipal(principal);
        return projectService.saveProjectForUser(request, user);
    }

}
