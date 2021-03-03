package com.mikovskycloud.workly.services;

import com.mikovskycloud.workly.domain.Project;
import com.mikovskycloud.workly.domain.ProjectMember;
import com.mikovskycloud.workly.domain.User;
import com.mikovskycloud.workly.exceptions.WorklyException;
import com.mikovskycloud.workly.repositories.ProjectRepository;
import com.mikovskycloud.workly.web.v1.projects.payload.AddMemberRequest;
import com.mikovskycloud.workly.web.v1.projects.payload.CreateProjectRequest;
import com.mikovskycloud.workly.web.v1.projects.payload.ProjectMemberResponse;
import com.mikovskycloud.workly.web.v1.projects.payload.ProjectResponse;
import com.mikovskycloud.workly.web.v1.projects.payload.UpdateProjectRequest;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final UserService userService;

    private final ProjectMemberService projectMemberService;

    @Transactional
    public List<ProjectResponse> getProjectsForUser(User user) {
        List<Long> projectIDs = StreamEx.of(projectMemberService.findAllByUserId(user.getId()))
                .map(ProjectMember::getProjectId)
                .toList();

        return StreamEx.of(projectRepository.findAllById(projectIDs))
                .map(ProjectResponse::fromProject)
                .toList();
    }

    @Transactional
    public ProjectResponse saveProjectForUser(CreateProjectRequest request, User user) {
        Project project = Project.of(user.getId(), request.getName());
        Project savedProject = save(project);
        ProjectMember projectMember = ProjectMember.of(savedProject.getId(), user.getId());
        projectMemberService.save(projectMember);

        return ProjectResponse.fromProject(savedProject);
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, UpdateProjectRequest request, User user) {
        Project project = findById(projectId);
        throwIfNotProjectOwner(project, user.getId());

        project.setName(request.getName());
        Project updatedProject = update(project);

        return ProjectResponse.fromProject(updatedProject);
    }

    @Transactional
    public ResponseEntity<Void> deleteProject(Long projectId, User user) {
        Project project = findById(projectId);
        throwIfNotProjectOwner(project, user.getId());

        projectMemberService.deleteAllByProjectId(projectId);
        projectRepository.deleteById(projectId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Transactional
    public List<ProjectMemberResponse> getMembersForProjectWithId(Long projectId, User user) {
        Project project = findById(projectId);
        throwIfNotProjectMember(project.getId(), user.getId());

        List<Long> membersIds = projectMemberService.findAllMembersIdsForProjectWithId(project.getId());

        return StreamEx.of(userService.findAllWhereIdIn(membersIds))
                .map(ProjectMemberResponse::fromUser)
                .toList();
    }

    @Transactional
    public ProjectMemberResponse addMemberToProjectWithId(Long projectId, AddMemberRequest request, User user) {
        Project project = findById(projectId);
        throwIfNotProjectOwner(project, user.getId());

        User member = userService.findById(request.getUserId());
        projectMemberService.save(ProjectMember.of(project.getId(), member.getId()));

        return ProjectMemberResponse.fromUser(member);
    }

    @Transactional
    public ResponseEntity<Void> deleteMemberFromProject(Long projectId, Long memberId, User user) {
        Project project = findById(projectId);
        throwIfNotProjectOwner(project, user.getId());

        User member = userService.findById(memberId);
        throwIfNotProjectMember(project.getId(), member.getId());

        projectMemberService.deleteByProjectIdAndUserId(project.getId(), memberId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public @NotNull Project findById(Long id) {
        return projectRepository.findById(id).orElseThrow(WorklyException::projectNotFound);
    }

    public Project save(Project project) {
        Instant now = Instant.now();
        project.setCreatedAt(now);
        project.setUpdatedAt(now);
        return projectRepository.save(project);
    }

    public Project update(Project project) {
        project.setUpdatedAt(Instant.now());
        return projectRepository.save(project);
    }

    public void throwIfNotProjectOwner(Project project, Long userId) {
        if (!Objects.equals(project.getUserId(), userId)) {
            throw WorklyException.forbidden();
        }
    }

    public void throwIfNotProjectMember(Long projectId, Long userId) {
        List<Long> membersIds = projectMemberService.findAllMembersIdsForProjectWithId(projectId);
        if (!membersIds.contains(userId)) {
            throw WorklyException.forbidden();
        }
    }

}
