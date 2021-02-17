package io.mikovsky.workly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(WorklyException.class)
    public ResponseEntity<ErrorResponse> handleWorklyException(WorklyException e, WebRequest webRequest) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(e.toErrorResponse());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e, WebRequest webRequest) {
        return ErrorResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
                .errorMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                .build();
    }

}
