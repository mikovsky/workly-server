package io.mikovsky.workly.web.v1.projects.payload;

import io.mikovsky.workly.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ApiModel(value = "ProjectResponse")
public class ProjectMemberResponse {

    @ApiModelProperty(required = true, position = 1)
    Long id;

    @ApiModelProperty(required = true, position = 2)
    String firstName;

    @ApiModelProperty(required = true, position = 3)
    String lastName;

    @ApiModelProperty(required = true, position = 4)
    String jobTitle;

    public static ProjectMemberResponse fromUser(User user) {
        return ProjectMemberResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .jobTitle(user.getJobTitle())
                .build();
    }

}
