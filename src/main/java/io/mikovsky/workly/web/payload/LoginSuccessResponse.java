package io.mikovsky.workly.web.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class LoginSuccessResponse {

    Boolean success;

    String token;

}
