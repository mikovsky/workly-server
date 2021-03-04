package com.mikovskycloud.workly.web.v1.sections;

import com.mikovskycloud.workly.domain.User;
import com.mikovskycloud.workly.services.SectionService;
import com.mikovskycloud.workly.validation.RequestValidator;
import com.mikovskycloud.workly.web.v1.sections.payload.CreateSectionRequest;
import com.mikovskycloud.workly.web.v1.sections.payload.SectionResponse;
import com.mikovskycloud.workly.web.v1.sections.payload.UpdateSectionRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/sections")
@RequiredArgsConstructor
@Api(tags = "Sections")
public class SectionController {

    private final SectionService sectionService;

    private final RequestValidator requestValidator;

    @GetMapping
    @ApiOperation(value = "Get all Section from Project")
    public List<SectionResponse> getProjectSections(@PathVariable Long projectId, Principal principal) {
        User user = User.fromPrincipal(principal);
        return sectionService.getSectionsForProject(projectId, user);
    }

    @PostMapping
    @ApiOperation(value = "Add Section to Project")
    public SectionResponse addSectionToProject(@PathVariable Long projectId,
                                               @Valid @RequestBody CreateSectionRequest request,
                                               BindingResult bindingResult,
                                               Principal principal) {
        requestValidator.throwIfRequestIsInvalid(bindingResult);
        User user = User.fromPrincipal(principal);
        return sectionService.addSectionToProject(projectId, request, user);
    }

    @PutMapping("/{sectionId}")
    @ApiOperation(value = "Update Section from Project")
    public SectionResponse updateSectionFromProject(@PathVariable Long projectId,
                                                    @PathVariable Long sectionId,
                                                    @Valid @RequestBody UpdateSectionRequest request,
                                                    BindingResult bindingResult,
                                                    Principal principal) {
        requestValidator.throwIfRequestIsInvalid(bindingResult);
        User user = User.fromPrincipal(principal);
        return sectionService.updateSectionFromProject(projectId, sectionId, request, user);
    }

    @DeleteMapping("/{sectionId}")
    @ApiOperation(value = "Delete Section from Project")
    public ResponseEntity<Void> deleteSectionFromProject(@PathVariable Long projectId,
                                                         @PathVariable Long sectionId,
                                                         Principal principal) {
        User user = User.fromPrincipal(principal);
        return sectionService.deleteSectionFromProject(projectId, sectionId, user);
    }

}
