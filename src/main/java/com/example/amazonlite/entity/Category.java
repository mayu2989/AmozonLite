package com.example.amazonlite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String categoryId;

    @Column(nullable = false,unique = true)
    private String categoryName;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
