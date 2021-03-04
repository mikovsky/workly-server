package com.mikovskycloud.workly.services;

import com.mikovskycloud.workly.domain.Project;
import com.mikovskycloud.workly.domain.ProjectMember;
import com.mikovskycloud.workly.exceptions.WorklyException;
import com.mikovskycloud.workly.repositories.ProjectMemberRepository;
import com.mikovskycloud.workly.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthorizeService {

    private final ProjectRepository projectRepository;

    private final ProjectMemberRepository projectMemberRepository;

    public void throwIfNotProjectOwner(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId).orElseThrow(WorklyException::projectNotFound);

        if (!Objects.equals(project.getUserId(), userId)) {
            throw WorklyException.forbidden();
        }
    }

    public void throwIfNotProjectMember(Long projectId, Long userId) {
        List<ProjectMember> projectMembers = projectMemberRepository.findAllByProjectId(projectId);
        List<Long> projectMembersIDs = StreamEx.of(projectMembers)
                .map(ProjectMember::getUserId)
                .toList();

        if (!projectMembersIDs.contains(userId)) {
            throw WorklyException.forbidden();
        }

    }

}
