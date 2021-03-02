package io.mikovsky.workly.services;

import io.mikovsky.workly.domain.User;
import io.mikovsky.workly.exceptions.WorklyException;
import io.mikovsky.workly.repositories.UserRepository;
import io.mikovsky.workly.web.v1.users.payload.UpdateUserPasswordRequest;
import io.mikovsky.workly.web.v1.users.payload.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public User updateUser(UpdateUserRequest request, User principal) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw WorklyException.emailAlreadyExists();
        }

        User user = findById(principal.getId());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setJobTitle(request.getJobTitle());
        return update(user);
    }

    public User updateUserPassword(UpdateUserPasswordRequest request, User principal) {
        User user = findById(principal.getId());
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw WorklyException.incorrectCurrentPassword();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return update(user);
    }

    public @NotNull User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw WorklyException.userNotFound();
        }

        return user.get();
    }

    public @NotNull User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw WorklyException.userNotFound();
        }

        return user.get();
    }

    public List<User> findAllWhereIdIn(List<Long> ids) {
        return userRepository.findAllByIdIn(ids);
    }

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

    public User update(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw WorklyException.userNotFound();
        }

        user.setUpdatedAt(Instant.now());

        return userRepository.save(user);
    }

}
