package com.example.amazonlite.repository;


import com.example.amazonlite.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,String> {
    Optional<Wallet> findByUserId(String userId);
    Boolean existsByUserId(String userId);
}
