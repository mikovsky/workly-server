package com.mikovskycloud.workly.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Map;
import java.util.UUID;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class WorklyBadRequestResponse {

    ErrorCode errorCode;

    String errorMessage;

    UUID requestUUID;

    Map<String, String> details;

}
