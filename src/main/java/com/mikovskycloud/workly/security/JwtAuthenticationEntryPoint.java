package com.mikovskycloud.workly.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikovskycloud.workly.exceptions.ErrorResponse;
import com.mikovskycloud.workly.exceptions.WorklyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        UUID requestUUID = UUID.randomUUID();
        log.error("RequestID: {}, Error Message: {}", requestUUID, "Unauthorized");
        ErrorResponse errorResponse = WorklyException.unauthorized().toErrorResponse(requestUUID);
        String json = new ObjectMapper().writeValueAsString(errorResponse);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(json);
    }

}
