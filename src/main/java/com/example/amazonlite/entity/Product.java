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
@Table(name = "products")
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String productId;

    @Column(nullable = false)
    private String sellerId;

    @Column(nullable = false)
    private String categoryId;

    @Column(nullable = false)
    private Integer price;

    private Double ratingAvg;

    @Column(nullable = false)
    private Integer stock;


    private LocalDateTime deliveryDate;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String nameOfProduct;


    private String description;
}
