package com.example.demo.service.keycloak;

import org.springframework.stereotype.Service;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;


public interface KeycloakService {

    String registerUser(RegisterRequest user);

    String loginUser(String username, String password);    
}
