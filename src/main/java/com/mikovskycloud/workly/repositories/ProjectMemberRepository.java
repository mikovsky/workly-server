package com.mikovskycloud.workly.repositories;

import com.mikovskycloud.workly.domain.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    List<ProjectMember> findAllByUserId(Long userId);

    List<ProjectMember> findAllByProjectId(Long projectId);

    void deleteAllByProjectId(Long projectId);

    void deleteByProjectIdAndUserId(Long projectId, Long userId);

}
