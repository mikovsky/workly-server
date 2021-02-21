package io.mikovsky.workly.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    EMAIL_ALREADY_EXISTS("given email address is already taken"),
    TASK_NOT_FOUND("requested task not found"),
    USER_NOT_FOUND("requested user not found"),
    UNAUTHORIZED("request unauthorized"),
    INTERNAL_SERVER_ERROR("something went wrong - please contact Majkelo");

    private final String message;

}
