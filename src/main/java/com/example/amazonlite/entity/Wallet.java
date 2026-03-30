package com.example.amazonlite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallets")
@Builder
public class Wallet {
    @Column(nullable = false)
    private Integer availableAmount;

    @Column(nullable = false,unique = true)
    private String userId;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String walletId;
}
