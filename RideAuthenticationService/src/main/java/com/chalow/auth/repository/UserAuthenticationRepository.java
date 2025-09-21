package com.chalow.auth.repository;

import com.chalow.auth.entity.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthenticationRepository extends JpaRepository<UserAuthentication, Long> {
    Optional<UserAuthentication> findByEmail(String email);
    Optional<UserAuthentication> findByAuth0Id(String auth0Id);
    Optional<UserAuthentication> findByKeycloakId(String keycloakId);
    boolean existsByEmail(String email);
}
