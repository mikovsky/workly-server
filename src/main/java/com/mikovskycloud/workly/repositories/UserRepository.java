package com.mikovskycloud.workly.repositories;

import com.mikovskycloud.workly.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdIsNot(String email, Long userId);

    List<User> findAllByIdIn(List<Long> ids);

}
