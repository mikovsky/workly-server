package com.mikovskycloud.workly.web.v1.tasks.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ApiModel(value = "UpdateProjectTaskRequest")
public class UpdateProjectTaskRequest {

    @Size(min = 2, max = 64, message = "task name needs to have 2-64 characters")
    @ApiModelProperty(required = false, position = 1)
    String name;

    @Size(max = 255, message = "max description length is 255 characters")
    @ApiModelProperty(required = false, position = 2)
    String description;

    @ApiModelProperty(required = false, position = 3)
    Boolean completed;

    @FutureOrPresent(message = "dueDate must be a future date")
    @ApiModelProperty(required = false, position = 4)
    LocalDate dueDate;

    @Positive(message = "sectionId must be a positive number")
    @ApiModelProperty(required = false, position = 6)
    Long sectionId;

    @Positive(message = "assigneeId must be a positive number")
    @ApiModelProperty(required = false, position = 7)
    Long assigneeId;

    public boolean isEmpty() {
        return name == null
                && description == null
                && completed == null
                && dueDate == null
                && sectionId == null
                && assigneeId == null;
    }

}
