package io.mikovsky.workly.security;

import io.mikovsky.workly.domain.User;
import io.mikovsky.workly.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorklyUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userService.findByEmail(email);
        return optionalUser.orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    @Transactional
    public User loadUserById(Long id) {
        Optional<User> optionalUser = userService.findById(id);
        return optionalUser.orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

}
