package com.chalow.auth.service;

import com.chalow.auth.entity.UserAuthentication;
import com.chalow.auth.repository.UserAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserAuthenticationRepository userAuthenticationRepository;
    private final Keycloak keycloak;

    @Transactional
    public UserAuthentication createOrUpdateUser(Authentication authentication) {
        String userId = getUserId(authentication);
        String email = getEmail(authentication);
        String authProvider = determineAuthProvider(authentication);

        return userAuthenticationRepository.findByEmail(email)
                .map(existing -> updateExistingUser(existing, userId, authProvider))
                .orElseGet(() -> createNewUser(email, userId, authProvider));
    }

    private UserAuthentication createNewUser(String email, String userId, String authProvider) {
        UserAuthentication newUser = new UserAuthentication();
        newUser.setEmail(email);
        setAuthenticationIds(newUser, userId, authProvider);
        return userAuthenticationRepository.save(newUser);
    }

    private UserAuthentication updateExistingUser(UserAuthentication existing, String userId, String authProvider) {
        setAuthenticationIds(existing, userId, authProvider);
        return userAuthenticationRepository.save(existing);
    }

    private void setAuthenticationIds(UserAuthentication user, String userId, String authProvider) {
        user.setAuthProvider(authProvider);
        if ("auth0".equals(authProvider)) {
            user.setAuth0Id(userId);
        } else if ("keycloak".equals(authProvider)) {
            user.setKeycloakId(userId);
        }
    }

    private String getUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return authentication.getName();
    }

    private String getEmail(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("email");
        }
        return authentication.getName();
    }

    private String determineAuthProvider(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String issuer = jwt.getIssuer() != null ? jwt.getIssuer().toString() : "";
            if (issuer.contains("auth0.com")) {
                return "auth0";
            } else if (issuer.contains("keycloak") || issuer.contains("localhost:8081")) {
                return "keycloak";
            }
        }
        return "auth0"; // Default fallback
    }
}
