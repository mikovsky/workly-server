package com.mikovskycloud.workly.web.v1.users;

import com.mikovskycloud.workly.domain.User;
import com.mikovskycloud.workly.services.UserService;
import com.mikovskycloud.workly.validation.RequestValidator;
import com.mikovskycloud.workly.web.v1.users.payload.UpdateUserPasswordRequest;
import com.mikovskycloud.workly.web.v1.users.payload.UpdateUserRequest;
import com.mikovskycloud.workly.web.v1.users.payload.UserResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Api(tags = "Users")
public class UserController {

    private final UserService userService;

    private final RequestValidator requestValidator;

    @GetMapping("/{userId}")
    @ApiOperation(value = "Get information about user with provided ID", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse getUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        return UserResponse.fromUser(user);
    }

    @PutMapping
    @ApiOperation(value = "Update user information", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse updateUser(@Valid @RequestBody UpdateUserRequest request,
                                   BindingResult bindingResult,
                                   Principal principal) {
        requestValidator.throwIfRequestIsInvalid(bindingResult);
        User updatedUser = userService.updateUser(request, User.fromPrincipal(principal));
        return UserResponse.fromUser(updatedUser);
    }

    @PutMapping("/password")
    @ApiOperation(value = "Update user password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse updateUserPassword(@Valid @RequestBody UpdateUserPasswordRequest request,
                                           BindingResult bindingResult,
                                           Principal principal) {
        requestValidator.throwIfRequestIsInvalid(bindingResult);
        User updatedUser = userService.updateUserPassword(request, User.fromPrincipal(principal));
        return UserResponse.fromUser(updatedUser);
    }

}
