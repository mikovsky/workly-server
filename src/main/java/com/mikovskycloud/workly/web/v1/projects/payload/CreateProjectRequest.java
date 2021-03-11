package com.mikovskycloud.workly.web.v1.projects.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ApiModel(value = "CreateProjectRequest")
public class CreateProjectRequest {

    @NotNull(message = "project name cannot be null")
    @Size(min = 2, max = 64, message = "project name needs to have 2-64 characters")
    @ApiModelProperty(required = true, position = 1)
    String name;

    @NotNull(message = "color cannot be null")
    @Pattern(regexp = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$", message = "color needs to be a valid hex")
    @ApiModelProperty(required = true, position = 2, example = "#000000")
    String color;

}
