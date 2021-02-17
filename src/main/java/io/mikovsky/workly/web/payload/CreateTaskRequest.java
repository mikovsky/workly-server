package io.mikovsky.workly.web.payload;

import io.mikovsky.workly.domain.Task;
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
public class CreateTaskRequest {

    @NotBlank(message = "task name is required")
    @Size(min = 2, max = 64, message = "task name needs to have 2-64 characters")
    String name;

    @Size(max = 255, message = "max description length is 255 characters")
    String description;

    Boolean completed;

    @Future(message = "dueDate must be a future date")
    Instant dueDate;

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
