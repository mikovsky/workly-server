package io.mikovsky.workly.web.v1.payload;

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
@ApiModel(value = "UpdateUserPasswordRequest")
public class UpdateUserPasswordRequest {

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 64, message = "password needs to have 8-64 characters")
    @ApiModelProperty(required = true, position = 1)
    String password;

}
