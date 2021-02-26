package io.mikovsky.workly.services;

import io.mikovsky.workly.domain.Project;
import io.mikovsky.workly.domain.ProjectMember;
import io.mikovsky.workly.domain.User;
import io.mikovsky.workly.exceptions.WorklyException;
import io.mikovsky.workly.repositories.ProjectMemberRepository;
import io.mikovsky.workly.repositories.ProjectRepository;
import io.mikovsky.workly.web.v1.projects.payload.AddMembersRequest;
import io.mikovsky.workly.web.v1.projects.payload.CreateProjectRequest;
import io.mikovsky.workly.web.v1.projects.payload.ProjectMemberResponse;
import io.mikovsky.workly.web.v1.projects.payload.ProjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final UserService userService;

    private final ProjectRepository projectRepository;

    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public List<ProjectResponse> getProjectsForUser(User user) {
        List<ProjectMember> projectMembers = projectMemberRepository.findAllByUserId(user.getId());
        List<Long> projectIDs = projectMembers.stream().map(ProjectMember::getProjectId).collect(Collectors.toList());
        List<Project> projects = projectRepository.findAllById(projectIDs);
        return projects.stream()
                .map(ProjectResponse::fromProject)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectResponse saveProjectForUser(CreateProjectRequest request, User user) {
        Instant now = Instant.now();

        Project project = Project.builder()
                .name(request.getName())
                .userId(user.getId())
                .createdAt(now)
                .updatedAt(now)
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectMember projectMember = ProjectMember.builder()
                .userId(user.getId())
                .projectId(savedProject.getId())
                .build();
        projectMemberRepository.save(projectMember);

        return ProjectResponse.fromProject(savedProject);
    }

    @Transactional
    public List<ProjectMemberResponse> getMembersForProjectWithId(Long projectId, User user) {
        Project project = findById(projectId);

        List<ProjectMember> projectMembers = projectMemberRepository.findAllByProjectId(project.getId());

        List<Long> membersIds = projectMembers
                .stream()
                .map(ProjectMember::getUserId)
                .collect(Collectors.toList());

        if (!membersIds.contains(user.getId())) {
            throw WorklyException.forbidden();
        }

        return userService.findAllWhereIdIn(membersIds)
                .stream()
                .map(ProjectMemberResponse::fromUser)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ProjectMemberResponse> addMembersToProjectWithId(Long projectId, AddMembersRequest request, User user) {
        Project project = findById(projectId);

        if (!Objects.equals(project.getUserId(), user.getId())) {
            throw WorklyException.forbidden();
        }

        List<ProjectMember> projectMembers = request.getUserIds()
                .stream()
                .map(userId -> ProjectMember.builder()
                        .userId(userId)
                        .projectId(project.getId())
                        .build())
                .collect(Collectors.toList());

        List<ProjectMember> newProjectMembers = projectMemberRepository.saveAll(projectMembers);

        List<Long> newMembersIds = newProjectMembers
                .stream()
                .map(ProjectMember::getUserId)
                .collect(Collectors.toList());

        return userService.findAllWhereIdIn(newMembersIds)
                .stream()
                .map(ProjectMemberResponse::fromUser)
                .collect(Collectors.toList());
    }

    public @NotNull Project findById(Long id) {
        return projectRepository.findById(id).orElseThrow(WorklyException::projectNotFound);
    }

}
