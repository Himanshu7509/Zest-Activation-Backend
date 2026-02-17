package com.zest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.zest.model.Role;
import com.zest.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

     List<User> findByRole(Role role);

    List<User> findByRoleAndApproved(Role role, Boolean approved);

    List<User> findByIsActive(Boolean isActive);
}
