package com.mikovskycloud.workly.services;

import com.mikovskycloud.workly.domain.Project;
import com.mikovskycloud.workly.domain.Section;
import com.mikovskycloud.workly.domain.User;
import com.mikovskycloud.workly.exceptions.WorklyException;
import com.mikovskycloud.workly.repositories.ProjectRepository;
import com.mikovskycloud.workly.repositories.SectionRepository;
import com.mikovskycloud.workly.web.v1.sections.payload.CreateSectionRequest;
import com.mikovskycloud.workly.web.v1.sections.payload.SectionResponse;
import com.mikovskycloud.workly.web.v1.sections.payload.UpdateSectionRequest;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;

    private final ProjectRepository projectRepository;

    private final AuthorizeService authorizeService;

    public List<SectionResponse> getSectionsForProject(Long projectId, User user) {
        Project project = projectRepository.findById(projectId).orElseThrow(WorklyException::projectNotFound);
        authorizeService.throwIfNotProjectMember(project.getId(), user.getId());

        return StreamEx.of(sectionRepository.findAllByProjectId(project.getId()))
                .map(SectionResponse::fromSection)
                .toList();
    }

    public SectionResponse addSectionToProject(Long projectId, CreateSectionRequest request, User user) {
        Project project = projectRepository.findById(projectId).orElseThrow(WorklyException::projectNotFound);
        authorizeService.throwIfNotProjectMember(project.getId(), user.getId());

        Section section = Section.of(project.getId(), request.getName());
        Section savedSection = save(section);

        return SectionResponse.fromSection(savedSection);
    }

    public SectionResponse updateSectionFromProject(Long projectId, Long sectionId, UpdateSectionRequest request, User user) {
        if (request.isEmpty()) throw WorklyException.emptyRequest();

        Project project = projectRepository.findById(projectId).orElseThrow(WorklyException::projectNotFound);
        authorizeService.throwIfNotProjectMember(project.getId(), user.getId());

        Section section = findById(sectionId);
        if (!section.getProjectId().equals(projectId)) throw WorklyException.forbidden();
        if (request.getName() != null) section.setName(request.getName());
        Section updatedSection = update(section);

        return SectionResponse.fromSection(updatedSection);
    }

    public ResponseEntity<Void> deleteSectionFromProject(Long projectId, Long sectionId, User user) {
        Project project = projectRepository.findById(projectId).orElseThrow(WorklyException::projectNotFound);
        authorizeService.throwIfNotProjectMember(project.getId(), user.getId());

        Section section = findById(sectionId);
        if (!section.getProjectId().equals(projectId)) throw WorklyException.forbidden();
        sectionRepository.deleteById(section.getId());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public @NotNull Section findById(Long sectionId) {
        return sectionRepository.findById(sectionId).orElseThrow(WorklyException::sectionNotFound);
    }

    public Section save(Section section) {
        Instant now = Instant.now();
        section.setCreatedAt(now);
        section.setUpdatedAt(now);
        return sectionRepository.save(section);
    }

    public Section update(Section section) {
        section.setUpdatedAt(Instant.now());
        return sectionRepository.save(section);
    }

}
