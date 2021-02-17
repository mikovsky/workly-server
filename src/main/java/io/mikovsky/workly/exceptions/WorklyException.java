package io.mikovsky.workly.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor(staticName = "of")
public class WorklyException extends RuntimeException {

    HttpStatus httpStatus;

    ErrorCode errorCode;

    public ErrorResponse toErrorResponse() {
        return ErrorResponse.builder()
                .httpStatus(httpStatus)
                .errorCode(errorCode)
                .errorMessage(errorCode.getMessage())
                .build();
    }

}