package io.mikovsky.workly.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(WorklyException.class)
    public ResponseEntity<ErrorResponse> handleWorklyException(WorklyException e, WebRequest webRequest) {
        UUID requestUUID = UUID.randomUUID();
        log.error("RequestID: {}, Error Message: {}, Throwable: {}", requestUUID, e.getMessage(), e);
        return ResponseEntity
                .status(e.getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(e.toErrorResponse(requestUUID));
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e, WebRequest webRequest) {
        UUID requestUUID = UUID.randomUUID();
        log.error("RequestID: {}, Error Message: {}, Throwable: {}", requestUUID, e.getMessage(), e);
        return ErrorResponse.builder()
                .errorCode(ErrorCode.UNAUTHORIZED)
                .errorMessage(ErrorCode.UNAUTHORIZED.getMessage())
                .requestUUID(requestUUID)
                .details(e.getMessage())
                .build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException e, WebRequest webRequest) {
        UUID requestUUID = UUID.randomUUID();
        log.error("RequestID: {}, Error Message: {}, Throwable: {}", requestUUID, e.getMessage(), e);
        return ErrorResponse.builder()
                .errorCode(ErrorCode.UNAUTHORIZED)
                .errorMessage(ErrorCode.UNAUTHORIZED.getMessage())
                .requestUUID(requestUUID)
                .details(e.getMessage())
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e, WebRequest webRequest) {
        UUID requestUUID = UUID.randomUUID();
        log.error("RequestID: {}, Error Message: {}, Throwable: {}", requestUUID, e.getMessage(), e);
        return ErrorResponse.builder()
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
                .errorMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                .requestUUID(requestUUID)
                .details(e.getMessage())
                .build();
    }

}
