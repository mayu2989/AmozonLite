package com.example.amazonlite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_cart_product",
                        columnNames = {"cart_id", "product_id"}  // ← snake_case, DB column names
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItems {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String itemId;

    @Column(nullable = false)
    private String cartId;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private Integer quantity;
}