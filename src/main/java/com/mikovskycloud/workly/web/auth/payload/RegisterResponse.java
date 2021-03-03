package com.mikovskycloud.workly.web.auth.payload;

import com.mikovskycloud.workly.domain.User;
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
@ApiModel(value = "RegisterResponse")
public class RegisterResponse {

    @ApiModelProperty(required = true, position = 1)
    Long id;

    @ApiModelProperty(required = true, position = 2)
    String email;

    @ApiModelProperty(required = true, position = 3)
    String firstName;

    @ApiModelProperty(required = true, position = 4)
    String lastName;

    public static RegisterResponse fromUser(User user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

}
