package com.mikovskycloud.workly.exceptions;

import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class WorklyBadRequestException extends RuntimeException {

    ErrorCode errorCode;

    String errorMessage;

    Map<String, String> validationResults;

    public WorklyBadRequestException(ErrorCode errorCode, String errorMessage, Map<String, String> validationResults) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.validationResults = validationResults;
    }

    public WorklyBadRequestResponse toResponse(UUID requestUUID) {
        return WorklyBadRequestResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .requestUUID(requestUUID)
                .details(validationResults)
                .build();
    }

    public static WorklyBadRequestException of(Map<String, String> validationResults) {
        return new WorklyBadRequestException(ErrorCode.BAD_REQUEST, ErrorCode.BAD_REQUEST.getMessage(), validationResults);
    }

}
