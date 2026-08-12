package com.example.demo.service.auth;


import org.springframework.stereotype.Service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.keycloak.KeycloakService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final KeycloakService keycloakService;
    private final UserRepository userRepository;

    @Override
    public UserResponse register(RegisterRequest request) {
        
        // Create User entity with the Keycloak ID
        String keycloakId = keycloakService.registerUser(request);
        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        userRepository.save(user);

        // Return user dto
        UserResponse userResponse = new UserResponse(
            user.getId(),
            user.getKeycloakId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName()
        );
        
        return userResponse;
    }

    @Override
    public String login(LoginRequest request) {
        String token = keycloakService.loginUser(request.getUsername(), request.getPassword());
        return token != null ? token : null;
    }

    @Override
    public UserResponse getWelcomeData(org.springframework.security.oauth2.jwt.Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        
        UserResponse userResponse = new UserResponse(
            user.getId(),
            user.getKeycloakId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName()
        );
        return userResponse;
    }

}
