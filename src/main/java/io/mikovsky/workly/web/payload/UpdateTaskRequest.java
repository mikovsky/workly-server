package io.mikovsky.workly.web.payload;

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
public class UpdateTaskRequest {

    @NotBlank(message = "task name is required")
    @Size(min = 2, max = 64, message = "task name needs to have 2-64 characters")
    String name;

    @Size(max = 255, message = "max description length is 255 characters")
    String description;

    Boolean completed;

    @Future(message = "dueDate must be a future date")
    Instant dueDate;

}
