package com.mikovskycloud.workly.web.v1.users.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ApiModel(value = "UpdateUserRequest")
public class UpdateUserRequest {

    @Email(message = "email needs to be valid")
    @Size(min = 6, max = 64, message = "email needs to have 6-64 characters")
    @ApiModelProperty(required = true, position = 1)
    String email;

    @Size(min = 2, max = 64, message = "firstName needs to have 2-64 characters")
    @ApiModelProperty(required = true, position = 2)
    String firstName;

    @Size(min = 2, max = 64, message = "lastName needs to have 2-64 characters")
    @ApiModelProperty(required = true, position = 3)
    String lastName;

    @Size(max = 64, message = "max jobTitle length is 64 characters")
    @ApiModelProperty(required = true, position = 4)
    String jobTitle;

    public boolean isEmpty() {
        return email == null && firstName == null && lastName == null && jobTitle == null;
    }

}
