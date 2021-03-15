package com.mikovskycloud.workly.web.v1.tasks.payload;

import com.mikovskycloud.workly.domain.Task;
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
@ApiModel(value = "ProjectTaskResponse")
public class ProjectTaskResponse {

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
    Long projectId;

    @ApiModelProperty(required = false, position = 7)
    Long sectionId;

    @ApiModelProperty(required = false, position = 8)
    Long assigneeId;

    @ApiModelProperty(required = true, position = 9)
    Instant createdAt;

    @ApiModelProperty(required = true, position = 10)
    Instant updatedAt;

    public static ProjectTaskResponse fromTask(Task task) {
        return ProjectTaskResponse.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .completed(task.getCompleted())
                .dueDate(task.getDueDate())
                .projectId(task.getProjectId())
                .sectionId(task.getSectionId())
                .assigneeId(task.getAssigneeId())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

}
