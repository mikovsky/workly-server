package io.mikovsky.workly.repositories;

import io.mikovsky.workly.domain.ProjectMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMembersRepository extends JpaRepository<ProjectMembers, Long> {
}
