package com.example.amazonlite.controller;

import com.example.amazonlite.dto.ApiResponse;
import com.example.amazonlite.entity.Payment;
import com.example.amazonlite.entity.User;
import com.example.amazonlite.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // pay for an order
    @PostMapping("/pay/{orderId}")
    public ResponseEntity<ApiResponse<Payment>> pay(
            @PathVariable String orderId,
            @AuthenticationPrincipal User user) {
        Payment payment = paymentService.payForOrder(orderId, user);
        return ResponseEntity.ok(ApiResponse.success(payment, "Payment successful"));
    }

    // get payment for an order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Payment>> getPayment(
            @PathVariable String orderId,
            @AuthenticationPrincipal User user) {
        Payment payment = paymentService.getPaymentByOrder(orderId, user);
        return ResponseEntity.ok(ApiResponse.success(payment, "Payment fetched"));
    }

    // get payment history
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<Payment>>> getHistory(
            @AuthenticationPrincipal User user) {
        List<Payment> payments = paymentService.getPaymentHistory(user);
        return ResponseEntity.ok(ApiResponse.success(payments, "Payment history fetched"));
    }
}