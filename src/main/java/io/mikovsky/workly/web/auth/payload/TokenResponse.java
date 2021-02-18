package io.mikovsky.workly.web.auth.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor(staticName = "of")
@ApiModel(value = "TokenResponse")
public class TokenResponse {

    @ApiModelProperty(required = true, position = 1)
    Boolean success;

    @ApiModelProperty(required = true, position = 2)
    String token;

}
