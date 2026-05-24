package com.example.amazonlite.service;

import com.example.amazonlite.entity.User;
import com.example.amazonlite.entity.Wallet;
import com.example.amazonlite.exceptions.AlreadyExistsException;
import com.example.amazonlite.exceptions.ResourceNotFoundException;
import com.example.amazonlite.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    // create wallet for user — called after registration
    public Wallet createWallet(User user) {
        if (walletRepository.existsByUserId(user.getUserId())) {
            throw new AlreadyExistsException("Wallet already exists for this user");
        }
        Wallet wallet = Wallet.builder()
                .userId(user.getUserId())
                .availableAmount(0)
                .build();
        return walletRepository.save(wallet);
    }

    // get wallet for user
    public Wallet getWallet(User user) {
        return walletRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }

    // add money to wallet
    @Transactional
    public Wallet addMoney(Integer amount, User user) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        Wallet wallet = getWallet(user);
        wallet.setAvailableAmount(wallet.getAvailableAmount() + amount);
        return walletRepository.save(wallet);
    }
}