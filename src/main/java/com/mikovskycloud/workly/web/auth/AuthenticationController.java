package com.mikovskycloud.workly.web.auth;

import com.mikovskycloud.workly.web.auth.payload.LoginRequest;
import com.mikovskycloud.workly.web.auth.payload.RegisterRequest;
import com.mikovskycloud.workly.web.auth.payload.RegisterResponse;
import com.mikovskycloud.workly.web.auth.payload.TokenResponse;
import com.mikovskycloud.workly.domain.User;
import com.mikovskycloud.workly.security.JwtTokenProvider;
import com.mikovskycloud.workly.security.SecurityProperties;
import com.mikovskycloud.workly.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Api(tags = "AuthenticationController")
public class AuthenticationController {

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    private final SecurityProperties properties;

    @PostMapping("/register")
    @ApiOperation(
            value = "Register new account",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        User savedUser = userService.save(request.toUser());
        return RegisterResponse.fromUser(savedUser);
    }

    @PostMapping("/login")
    @ApiOperation(
            value = "Login & generate JWT Token",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = properties.getTokenPrefix() + jwtTokenProvider.generateToken(authentication);

        return TokenResponse.of(true, token);
    }

}
