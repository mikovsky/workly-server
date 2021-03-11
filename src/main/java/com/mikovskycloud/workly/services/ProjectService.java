package com.mikovskycloud.workly.services;

import com.mikovskycloud.workly.domain.Project;
import com.mikovskycloud.workly.domain.ProjectMember;
import com.mikovskycloud.workly.domain.User;
import com.mikovskycloud.workly.exceptions.WorklyException;
import com.mikovskycloud.workly.repositories.ProjectMemberRepository;
import com.mikovskycloud.workly.repositories.ProjectRepository;
import com.mikovskycloud.workly.repositories.SectionRepository;
import com.mikovskycloud.workly.repositories.UserRepository;
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

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final UserRepository userRepository;

    private final ProjectMemberRepository projectMemberRepository;

    private final SectionRepository sectionRepository;

    private final AuthorizeService authorizeService;

    @Transactional
    public List<ProjectResponse> getProjectsForUser(User user) {
        List<Long> projectIDs = StreamEx.of(projectMemberRepository.findAllByUserId(user.getId()))
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
        projectMemberRepository.save(projectMember);
        return ProjectResponse.fromProject(savedProject);
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, UpdateProjectRequest request, User user) {
        if (request.isEmpty()) throw WorklyException.emptyRequest();

        Project project = findById(projectId);
        authorizeService.throwIfNotProjectOwner(project.getId(), user.getId());

        if (request.getName() != null) project.setName(request.getName());
        Project updatedProject = update(project);
        return ProjectResponse.fromProject(updatedProject);
    }

    @Transactional
    public ResponseEntity<Void> deleteProject(Long projectId, User user) {
        Project project = findById(projectId);
        authorizeService.throwIfNotProjectOwner(project.getId(), user.getId());

        sectionRepository.deleteAllByProjectId(projectId);
        projectMemberRepository.deleteAllByProjectId(projectId);
        projectRepository.deleteById(projectId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Transactional
    public List<ProjectMemberResponse> getMembersForProjectWithId(Long projectId, User user) {
        Project project = findById(projectId);
        authorizeService.throwIfNotProjectMember(project.getId(), user.getId());

        List<Long> membersIDs = StreamEx.of(projectMemberRepository.findAllByProjectId(project.getId()))
                .map(ProjectMember::getUserId)
                .toList();

        return StreamEx.of(userRepository.findAllByIdIn(membersIDs))
                .map(ProjectMemberResponse::fromUser)
                .toList();
    }

    @Transactional
    public ProjectMemberResponse addMemberToProjectWithId(Long projectId, AddMemberRequest request, User user) {
        Project project = findById(projectId);
        authorizeService.throwIfNotProjectOwner(project.getId(), user.getId());

        User member = userRepository.findByEmail(request.getEmail()).orElseThrow(WorklyException::userNotFound);
        projectMemberRepository.save(ProjectMember.of(project.getId(), member.getId()));
        return ProjectMemberResponse.fromUser(member);
    }

    @Transactional
    public ResponseEntity<Void> deleteMemberFromProject(Long projectId, Long memberId, User user) {
        Project project = findById(projectId);
        authorizeService.throwIfNotProjectOwner(project.getId(), user.getId());

        User member = userRepository.findById(memberId).orElseThrow(WorklyException::userNotFound);
        authorizeService.throwIfNotProjectMember(project.getId(), member.getId());

        projectMemberRepository.deleteByProjectIdAndUserId(project.getId(), memberId);
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

}
