package io.mikovsky.workly.web.v1.payload;

import io.mikovsky.workly.domain.Task;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.Instant;
import java.time.LocalDate;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ApiModel(value = "TaskResponse")
public class TaskResponse {

    @ApiModelProperty(required = true, position = 1)
    Long id;

    @ApiModelProperty(required = true, position = 2)
    String name;

    @ApiModelProperty(required = false, position = 3)
    String description;

    @ApiModelProperty(required = true, position = 4)
    Boolean completed;

    @ApiModelProperty(required = false, position = 5)
    LocalDate dueDate;

    @ApiModelProperty(required = true, position = 6)
    Instant createdAt;

    @ApiModelProperty(required = true, position = 7)
    Instant updatedAt;

    public static TaskResponse fromTask(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .completed(task.getCompleted())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

}
