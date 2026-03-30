package com.example.amazonlite.entity;

import com.example.amazonlite.entity.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false,unique = true)
    private String username;

    @Column(unique = true,nullable = false)
    private String email;

    private String name;
    @Column(nullable = false)
    private String password;
}
