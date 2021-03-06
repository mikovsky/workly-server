package com.mikovskycloud.workly.repositories;

import com.mikovskycloud.workly.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByUserId(Long userId);

    List<Task> findAllByProjectId(Long projectId);

    Optional<Task> findByIdAndUserId(Long taskId, Long userId);

    Boolean existsByIdAndUserId(Long taskId, Long userId);

    void deleteAllByProjectId(Long projectId);

}
