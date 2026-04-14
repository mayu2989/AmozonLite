package com.example.amazonlite.service;

import com.example.amazonlite.dto.AuthResponse;
import com.example.amazonlite.dto.LoginRequest;
import com.example.amazonlite.dto.RegisterRequest;
import com.example.amazonlite.entity.User;
import com.example.amazonlite.exceptions.AlreadyExistsException;
import com.example.amazonlite.exceptions.ResourceNotFoundException;
import com.example.amazonlite.repository.UserRepository;
import com.example.amazonlite.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {

        // check email not already taken
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Email already registered");
        }

        // check username not already taken
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AlreadyExistsException("Username already taken");
        }

        // build user — hash password before saving
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .userType(request.getUserType())
                .build();

        userRepository.save(user);

        // generate token and return
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getActualUsername());
    }

    public AuthResponse login(LoginRequest request) {

        // find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid password");
        }

        // generate token and return
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getUsername());
    }
}