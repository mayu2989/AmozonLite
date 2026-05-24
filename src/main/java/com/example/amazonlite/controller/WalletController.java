package com.example.amazonlite.controller;

import com.example.amazonlite.dto.ApiResponse;
import com.example.amazonlite.entity.User;
import com.example.amazonlite.entity.Wallet;
import com.example.amazonlite.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // create wallet
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Wallet>> createWallet(
            @AuthenticationPrincipal User user) {
        Wallet wallet = walletService.createWallet(user);
        return ResponseEntity.ok(ApiResponse.success(wallet, "Wallet created"));
    }

    // get wallet balance
    @GetMapping
    public ResponseEntity<ApiResponse<Wallet>> getWallet(
            @AuthenticationPrincipal User user) {
        Wallet wallet = walletService.getWallet(user);
        return ResponseEntity.ok(ApiResponse.success(wallet, "Wallet fetched"));
    }

    // add money to wallet
    @PutMapping("/add-money")
    public ResponseEntity<ApiResponse<Wallet>> addMoney(
            @RequestParam Integer amount,
            @AuthenticationPrincipal User user) {
        Wallet wallet = walletService.addMoney(amount, user);
        return ResponseEntity.ok(ApiResponse.success(wallet, "Money added to wallet"));
    }
}