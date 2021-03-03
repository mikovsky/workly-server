package com.mikovskycloud.workly.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public class WorklyException extends RuntimeException {

    HttpStatus httpStatus;

    ErrorCode errorCode;

    private WorklyException(HttpStatus httpStatus, ErrorCode errorCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public ErrorResponse toErrorResponse(UUID requestUUID) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorCode.getMessage())
                .requestUUID(requestUUID)
                .build();
    }

    public static WorklyException sectionAlreadyExists() {
        return new WorklyException(HttpStatus.BAD_REQUEST, ErrorCode.SECTION_ALREADY_EXISTS, ErrorCode.SECTION_ALREADY_EXISTS.getMessage());
    }

    public static WorklyException sectionNotFound() {
        return new WorklyException(HttpStatus.NOT_FOUND, ErrorCode.SECTION_NOT_FOUND, ErrorCode.SECTION_NOT_FOUND.getMessage());
    }

    public static WorklyException projectNotFound() {
        return new WorklyException(HttpStatus.NOT_FOUND, ErrorCode.PROJECT_NOT_FOUND, ErrorCode.PROJECT_NOT_FOUND.getMessage());
    }

    public static WorklyException emailAlreadyExists() {
        return new WorklyException(HttpStatus.BAD_REQUEST, ErrorCode.EMAIL_ALREADY_EXISTS, ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
    }

    public static WorklyException taskNotFound() {
        return new WorklyException(HttpStatus.NOT_FOUND, ErrorCode.TASK_NOT_FOUND, ErrorCode.TASK_NOT_FOUND.getMessage());
    }

    public static WorklyException taskAlreadyExists() {
        return new WorklyException(HttpStatus.BAD_REQUEST, ErrorCode.TASK_ALREADY_EXISTS, ErrorCode.TASK_ALREADY_EXISTS.getMessage());
    }

    public static WorklyException userNotFound() {
        return new WorklyException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage());
    }

    public static WorklyException incorrectCurrentPassword() {
        return new WorklyException(HttpStatus.BAD_REQUEST, ErrorCode.INCORRECT_CURRENT_PASSWORD, ErrorCode.INCORRECT_CURRENT_PASSWORD.getMessage());
    }

    public static WorklyException unauthorized() {
        return new WorklyException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage());
    }

    public static WorklyException forbidden() {
        return new WorklyException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, ErrorCode.FORBIDDEN.getMessage());
    }

    public static WorklyException internalServerError(String message) {
        return new WorklyException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR, message);
    }

}
