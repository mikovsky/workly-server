package io.mikovsky.workly.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    PROJECT_NOT_FOUND("project with given id not found"),
    EMAIL_ALREADY_EXISTS("given email address is already taken"),
    TASK_NOT_FOUND("requested task not found"),
    TASK_ALREADY_EXISTS("task with given name already exists"),
    USER_NOT_FOUND("requested user not found"),
    INCORRECT_CURRENT_PASSWORD("current password is incorrect"),
    UNAUTHORIZED("request unauthorized"),
    FORBIDDEN("operation forbidden"),
    INTERNAL_SERVER_ERROR("something went wrong - please contact Majkelo");

    private final String message;

}
