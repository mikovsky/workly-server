package com.mikovskycloud.workly.web.v1.projects.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ApiModel(value = "AddMemberRequest")
public class AddMemberRequest {

    @NotNull(message = "userId cannot be null")
    @Positive(message = "userId must be positive")
    @ApiModelProperty(required = true, position = 1)
    Long userId;

}
