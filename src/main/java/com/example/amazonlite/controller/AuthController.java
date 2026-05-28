package com.example.amazonlite.controller;

import com.example.amazonlite.dto.ApiResponse;
import com.example.amazonlite.dto.AuthResponse;
import com.example.amazonlite.dto.LoginRequest;
import com.example.amazonlite.dto.RegisterRequest;
import com.example.amazonlite.entity.User;
import com.example.amazonlite.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    // NEW: Returns the authenticated user's profile (userId, role, etc.)
    // Frontend calls this after login to get userId for seller-specific API calls.
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> me(@AuthenticationPrincipal User user) {
        AuthResponse profile = new AuthResponse(
                null, // don't re-issue token
                user.getEmail(),
                user.getActualUsername(),
                user.getUserId(),
                user.getUserType().name()
        );
        return ResponseEntity.ok(ApiResponse.success(profile, "User profile fetched"));
    }
}