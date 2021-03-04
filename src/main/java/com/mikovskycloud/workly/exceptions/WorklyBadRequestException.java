package com.mikovskycloud.workly.exceptions;

import lombok.Getter;
import one.util.streamex.StreamEx;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.UUID;

@Getter
public class WorklyBadRequestException extends RuntimeException {

    ErrorCode errorCode;

    String errorMessage;

    BindingResult bindingResult;

    public WorklyBadRequestException(ErrorCode errorCode, String errorMessage, BindingResult bindingResult) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.bindingResult = bindingResult;
    }

    public WorklyBadRequestResponse toResponse(UUID requestUUID) {
        Map<String, String> details = StreamEx.of(bindingResult.getFieldErrors())
                .toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage);
        return WorklyBadRequestResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .requestUUID(requestUUID)
                .details(details)
                .build();
    }

    public static WorklyBadRequestException of(BindingResult bindingResult) {
        return new WorklyBadRequestException(ErrorCode.BAD_REQUEST, ErrorCode.BAD_REQUEST.getMessage(), bindingResult);
    }

}
