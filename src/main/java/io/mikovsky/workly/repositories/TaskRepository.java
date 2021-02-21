package io.mikovsky.workly.repositories;

import io.mikovsky.workly.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByUserId(Long userId);

    Optional<Task> findByIdAndUserId(Long taskId, Long userId);

    Boolean existsByNameAndUserId(String name, Long userId);

    Boolean existsByIdAndUserId(Long taskId, Long userId);

}
