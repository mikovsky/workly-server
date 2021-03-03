package com.mikovskycloud.workly.repositories;

import com.mikovskycloud.workly.domain.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findAllByProjectId(Long projectId);

    boolean existsByProjectIdAndName(Long projectId, String name);

}
