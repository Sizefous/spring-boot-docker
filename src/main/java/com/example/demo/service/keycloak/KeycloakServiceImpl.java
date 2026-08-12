package com.example.demo.service.keycloak;

import java.util.Collections;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties.Jwt;
import org.springframework.stereotype.Service;

import com.example.demo.config.KeycloakConfiguration;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserResponse;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    private final KeycloakConfiguration keycloakConfiguration;

    @Override
    public String registerUser(RegisterRequest user) {
        
        // Create a new user representation
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setEnabled(true);
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        
        // Set the password for the user
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(user.getPassword());
        credential.setTemporary(false);
        userRepresentation.setCredentials(Collections.singletonList(credential));

        // Build the Keycloak client
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakConfiguration.getServerUrl())
                .realm(keycloakConfiguration.getRealm())
                .username(keycloakConfiguration.getUsername())
                .password(keycloakConfiguration.getPassword())
                .clientId(keycloakConfiguration.getClientId())
                .clientSecret(keycloakConfiguration.getClientSecret())
                .build();

        // Create the user in Keycloak
        Response response = keycloak.realm(keycloakConfiguration.getRealm()).users().create(userRepresentation);
        if (response.getStatus() == 201) {
            String location = response.getHeaderString("Location");
            String keycloakId = location.substring(location.lastIndexOf('/') + 1);
            return keycloakId;
        } else {
            throw new RuntimeException("Failed to register user: " + response.getStatusInfo().getReasonPhrase());
        }
        
    }

    @Override
    public String loginUser(String username, String password) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakConfiguration.getServerUrl())
                .realm(keycloakConfiguration.getRealm())
                .username(username)
                .password(password)
                .clientId(keycloakConfiguration.getClientId())
                .clientSecret(keycloakConfiguration.getClientSecret())
                .build();

        // This call forces token generation and authenticates credentials 
        String token = keycloak.tokenManager().getAccessTokenString();
        keycloak.close();
        return token;

    }

    
}
