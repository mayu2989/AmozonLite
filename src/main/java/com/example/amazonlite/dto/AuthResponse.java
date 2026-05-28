package com.example.amazonlite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String username;
    private String userId;   // needed by frontend for /products/seller/{sellerId}
    private String role;     // userType as string e.g. "BUYER" / "SELLER"
}