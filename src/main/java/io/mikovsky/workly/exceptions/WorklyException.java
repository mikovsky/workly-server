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

    public static WorklyException emailAlreadyExists() {
        return WorklyException.of(HttpStatus.BAD_REQUEST, ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    public static WorklyException taskNotFound() {
        return WorklyException.of(HttpStatus.NOT_FOUND, ErrorCode.TASK_NOT_FOUND);
    }

    public static WorklyException taskAlreadyExists() {
        return WorklyException.of(HttpStatus.BAD_REQUEST, ErrorCode.TASK_ALREADY_EXISTS);
    }

    public static WorklyException userNotFound() {
        return WorklyException.of(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }

    public static WorklyException incorrectCurrentPassword() {
        return WorklyException.of(HttpStatus.BAD_REQUEST, ErrorCode.INCORRECT_CURRENT_PASSWORD);
    }

    public static WorklyException unauthorized() {
        return WorklyException.of(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
    }

    public static WorklyException internalServerError() {
        return WorklyException.of(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR);
    }

}
