package io.mikovsky.workly.web.v1.projects.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ApiModel(value = "AddMembersRequest")
public class AddMembersRequest {

    @NotNull(message = "userIds list cannot be null")
    @NotEmpty(message = "userIds list cannot be empty")
    @ApiModelProperty(required = true, position = 1)
    List<Long> userIds;

}
