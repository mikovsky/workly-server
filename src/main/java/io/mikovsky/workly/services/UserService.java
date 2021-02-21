package io.mikovsky.workly.services;

import io.mikovsky.workly.domain.User;
import io.mikovsky.workly.exceptions.ErrorCode;
import io.mikovsky.workly.exceptions.WorklyException;
import io.mikovsky.workly.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public User save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw WorklyException.of(HttpStatus.BAD_REQUEST, ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public @NotNull User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw WorklyException.of(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
        }

        return user.get();
    }

    public @NotNull User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw WorklyException.of(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
        }

        return user.get();
    }

}
