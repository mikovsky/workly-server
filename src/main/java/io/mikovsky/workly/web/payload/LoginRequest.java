package io.mikovsky.workly.web.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class LoginRequest {

    @Email(message = "email needs to be valid")
    @NotBlank(message = "email is required")
    @Size(min = 6, max = 64, message = "email needs to have 6-64 characters")
    String email;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 64, message = "password needs to have 8-64 characters")
    String password;

}
