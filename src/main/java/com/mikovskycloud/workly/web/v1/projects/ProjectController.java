package com.mikovskycloud.workly.web.v1.projects;

import com.mikovskycloud.workly.domain.User;
import com.mikovskycloud.workly.services.ProjectService;
import com.mikovskycloud.workly.web.v1.projects.payload.AddMemberRequest;
import com.mikovskycloud.workly.web.v1.projects.payload.CreateProjectRequest;
import com.mikovskycloud.workly.web.v1.projects.payload.ProjectMemberResponse;
import com.mikovskycloud.workly.web.v1.projects.payload.ProjectResponse;
import com.mikovskycloud.workly.web.v1.projects.payload.UpdateProjectRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Api(tags = "Projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @ApiOperation(value = "Get All Projects for User", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProjectResponse> getAllProjectsForUser(Principal principal) {
        User user = User.fromPrincipal(principal);
        return projectService.getProjectsForUser(user);
    }

    @PostMapping
    @ApiOperation(value = "Create Projects for User", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectResponse createNewProjectForUser(@Valid @RequestBody CreateProjectRequest request,
                                                   Principal principal) {
        User user = User.fromPrincipal(principal);
        return projectService.saveProjectForUser(request, user);
    }

    @PutMapping("/{projectId}")
    @ApiOperation(value = "Update Project for User", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectResponse updateProject(@PathVariable Long projectId,
                                         @Valid @RequestBody UpdateProjectRequest request,
                                         Principal principal) {
        User user = User.fromPrincipal(principal);
        return projectService.updateProject(projectId, request, user);
    }

    @DeleteMapping("/{projectId}")
    @ApiOperation(value = "Delete Project for User")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId, Principal principal) {
        User user = User.fromPrincipal(principal);
        return projectService.deleteProject(projectId, user);
    }

    @GetMapping("/{projectId}/members")
    @ApiOperation(value = "Get All Project Members", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProjectMemberResponse> getProjectMembers(@PathVariable Long projectId, Principal principal) {
        User user = User.fromPrincipal(principal);
        return projectService.getMembersForProjectWithId(projectId, user);
    }

    @PostMapping("/{projectId}/members")
    @ApiOperation(value = "Add Member to Project", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectMemberResponse addMemberToProject(@PathVariable Long projectId,
                                                    @Valid @RequestBody AddMemberRequest request,
                                                    Principal principal) {
        User user = User.fromPrincipal(principal);
        return projectService.addMemberToProjectWithId(projectId, request, user);
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    @ApiOperation(value = "Delete Member from Project")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId,
                                              @PathVariable Long memberId,
                                              Principal principal) {
        User user = User.fromPrincipal(principal);
        return projectService.deleteMemberFromProject(projectId, memberId, user);
    }

}
