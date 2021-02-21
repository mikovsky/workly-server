package io.mikovsky.workly.web.v1;

import io.mikovsky.workly.domain.User;
import io.mikovsky.workly.services.UserService;
import io.mikovsky.workly.web.v1.payload.UpdateUserPasswordRequest;
import io.mikovsky.workly.web.v1.payload.UpdateUserRequest;
import io.mikovsky.workly.web.v1.payload.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        return UserResponse.fromUser(user);
    }

    @PutMapping
    public UserResponse updateUser(@Valid @RequestBody UpdateUserRequest request, Principal principal) {
        User user = User.fromPrincipal(principal);
        User toUpdate = userService.findById(user.getId());
        toUpdate.setEmail(request.getEmail());
        toUpdate.setFirstName(request.getFirstName());
        toUpdate.setLastName(request.getLastName());
        User updatedUser = userService.save(toUpdate);
        return UserResponse.fromUser(updatedUser);
    }

    @PutMapping("/password")
    public UserResponse updateUserPassword(@Valid @RequestBody UpdateUserPasswordRequest request, Principal principal) {
        User user = User.fromPrincipal(principal);
        User toUpdate = userService.findById(user.getId());
        toUpdate.setPassword(request.getPassword());
        User updatedUser = userService.save(toUpdate);
        return UserResponse.fromUser(updatedUser);
    }

}
