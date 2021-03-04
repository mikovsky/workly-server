package com.mikovskycloud.workly.web.v1.sections.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Size;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ApiModel(value = "UpdateSectionRequest")
public class UpdateSectionRequest {

    @Size(min = 2, max = 32, message = "section name needs to have 2-32 characters")
    @ApiModelProperty(required = true, position = 1)
    String name;

    public boolean isEmpty() {
        return name == null;
    }

}
