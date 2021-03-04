package com.mikovskycloud.workly.services;

import com.mikovskycloud.workly.domain.User;
import com.mikovskycloud.workly.exceptions.WorklyException;
import com.mikovskycloud.workly.repositories.UserRepository;
import com.mikovskycloud.workly.web.v1.users.payload.UpdateUserPasswordRequest;
import com.mikovskycloud.workly.web.v1.users.payload.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User updateUser(UpdateUserRequest request, User principal) {
        if (request.isEmpty()) throw WorklyException.emptyRequest();

        if (userRepository.existsByEmail(request.getEmail())) {
            throw WorklyException.emailAlreadyExists();
        }

        User user = findById(principal.getId());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getJobTitle() != null) user.setJobTitle(request.getJobTitle());
        return update(user);
    }

    @Transactional
    public User updateUserPassword(UpdateUserPasswordRequest request, User principal) {
        User user = findById(principal.getId());
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw WorklyException.incorrectCurrentPassword();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return update(user);
    }

    @Transactional
    public @NotNull User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw WorklyException.userNotFound();
        }

        return user.get();
    }

    @Transactional
    public @NotNull User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw WorklyException.userNotFound();
        }

        return user.get();
    }

    @Transactional
    public User save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw WorklyException.emailAlreadyExists();
        }

        Instant now = Instant.now();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return userRepository.save(user);
    }

    @Transactional
    public User update(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw WorklyException.userNotFound();
        }

        user.setUpdatedAt(Instant.now());

        return userRepository.save(user);
    }

}
