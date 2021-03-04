package com.mikovskycloud.workly.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SECTION_NOT_FOUND("section with given id not found"),
    PROJECT_NOT_FOUND("project with given id not found"),
    TASK_NOT_FOUND("requested task not found"),
    USER_NOT_FOUND("requested user not found"),
    EMAIL_ALREADY_EXISTS("given email address is already taken"),
    INCORRECT_CURRENT_PASSWORD("current password is incorrect"),
    BAD_REQUEST("request is invalid, check details for more information"),
    EMPTY_REQUEST("at least one field must be present"),
    UNAUTHORIZED("request unauthorized"),
    FORBIDDEN("operation forbidden"),
    INTERNAL_SERVER_ERROR("something went wrong - please contact Majkelo");

    private final String message;

}
