package com.mikovskycloud.workly.web.v1.projects.payload;

import com.mikovskycloud.workly.domain.Project;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ApiModel(value = "ProjectResponse")
public class ProjectResponse {

    @ApiModelProperty(required = true, position = 1)
    Long id;

    @ApiModelProperty(required = true, position = 2)
    String name;

    @ApiModelProperty(required = true, position = 3)
    Long ownerId;

    @ApiModelProperty(required = true, position = 4)
    Instant createdAt;

    @ApiModelProperty(required = true, position = 5)
    Instant updatedAt;

    public static ProjectResponse fromProject(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .ownerId(project.getUserId())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

}
