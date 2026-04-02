package com.example.amazonlite.dto;

import com.example.amazonlite.entity.enums.UserType;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String name;
    private UserType userType;
}