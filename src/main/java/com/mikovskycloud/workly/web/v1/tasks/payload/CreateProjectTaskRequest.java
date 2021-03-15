package com.mikovskycloud.workly.web.v1.tasks.payload;

import com.mikovskycloud.workly.domain.Task;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ApiModel(value = "CreateProjectTaskRequest")
public class CreateProjectTaskRequest {

    @NotNull(message = "task name cannot be null")
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

    @NotNull(message = "projectId cannot be null")
    @Positive(message = "sectionId must be a positive number")
    @ApiModelProperty(required = true, position = 5)
    Long projectId;

    @Positive(message = "sectionId must be a positive number")
    @ApiModelProperty(required = false, position = 6)
    Long sectionId;

    @Positive(message = "assigneeId must be a positive number")
    @ApiModelProperty(required = false, position = 7)
    Long assigneeId;

    public Task toTask() {
        return Task.builder()
                .name(name)
                .description(description)
                .completed(completed)
                .dueDate(dueDate)
                .projectId(projectId)
                .sectionId(sectionId)
                .assigneeId(assigneeId)
                .build();
    }

}
