package com.example.demo.service.auth;

import org.springframework.security.oauth2.jwt.Jwt;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserResponse;


public interface AuthService {

    
    UserResponse register(RegisterRequest request);

    
    String login(LoginRequest request);

    
    UserResponse getWelcomeData(Jwt jwt);


    
}
