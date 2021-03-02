package io.mikovsky.workly.web.v1.tasks.payload;

import io.mikovsky.workly.domain.Task;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ApiModel(value = "CreateTaskRequest")
public class CreateTaskRequest {

    @NotBlank(message = "task name is required")
    @Size(min = 2, max = 64, message = "task name needs to have 2-64 characters")
    @ApiModelProperty(required = true, position = 1)
    String name;

    @Size(max = 255, message = "max description length is 255 characters")
    @ApiModelProperty(required = false, position = 2)
    String description;

    @ApiModelProperty(required = false, position = 3)
    Boolean completed;

    @FutureOrPresent(message = "dueDate must be a future date")
    @ApiModelProperty(required = false, position = 4)
    LocalDate dueDate;

    public Task toTask(Long userId) {
        return Task.builder()
                .userId(userId)
                .name(name)
                .description(description)
                .completed(completed != null ? completed : false)
                .dueDate(dueDate)
                .build();
    }

}