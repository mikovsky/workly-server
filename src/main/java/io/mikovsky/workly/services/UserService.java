package io.mikovsky.workly.services;

import io.mikovsky.workly.domain.User;
import io.mikovsky.workly.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public User save(User newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        // email has to be unique (throw exception)

        return userRepository.save(newUser);
    }

}
