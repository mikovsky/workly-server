package io.mikovsky.workly.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ErrorResponse {

    ErrorCode errorCode;

    String errorMessage;

    String details;

}
