package io.mikovsky.workly.services;

import io.mikovsky.workly.domain.ProjectMember;
import io.mikovsky.workly.repositories.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

    public List<ProjectMember> findAllByUserId(Long userId) {
        return projectMemberRepository.findAllByUserId(userId);
    }

    public List<ProjectMember> findAllByProjectId(Long projectId) {
        return projectMemberRepository.findAllByProjectId(projectId);
    }

    public List<Long> findAllMembersIdsForProjectWithId(Long projectId) {
        return StreamEx.of(findAllByProjectId(projectId))
                .map(ProjectMember::getUserId)
                .toList();
    }

    public void deleteAllByProjectId(Long projectId) {
        projectMemberRepository.deleteAllByProjectId(projectId);
    }

    public ProjectMember save(ProjectMember projectMember) {
        return projectMemberRepository.save(projectMember);
    }

    public void deleteByProjectIdAndUserId(Long projectId, Long userId) {
        projectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

}
