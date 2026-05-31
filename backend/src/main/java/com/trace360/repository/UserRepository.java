package com.trace360.repository;

import com.trace360.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    List<User> findByRole(User.Role role);
    List<User> findByRoleAndActiveTrue(User.Role role);
}
