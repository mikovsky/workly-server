package io.mikovsky.workly.web.v1.projects.payload;

import io.mikovsky.workly.domain.Project;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

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

    public static ProjectResponse fromProject(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .build();
    }

}
