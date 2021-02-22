package io.mikovsky.workly.web.v1.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ApiModel(value = "UpdateTaskRequest")
public class UpdateTaskRequest {

    @NotBlank(message = "task name is required")
    @Size(min = 2, max = 64, message = "task name needs to have 2-64 characters")
    @ApiModelProperty(required = true, position = 1)
    String name;

    @Size(max = 255, message = "max description length is 255 characters")
    @ApiModelProperty(required = false, position = 2)
    String description;

    @ApiModelProperty(required = false, position = 3)
    Boolean completed;

    @Future(message = "dueDate must be a future date")
    @ApiModelProperty(required = false, position = 4)
    Instant dueDate;

}