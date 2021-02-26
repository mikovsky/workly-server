package io.mikovsky.workly.web.v1.projects.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ApiModel(value = "CreateProjectRequest")
public class CreateProjectRequest {

    @NotBlank(message = "project name is required")
    @Size(min = 2, max = 64, message = "project name needs to have 2-64 characters")
    @ApiModelProperty(required = true, position = 1)
    String name;

}
