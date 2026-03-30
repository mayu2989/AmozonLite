package com.example.amazonlite.repository;


import com.example.amazonlite.entity.Payment;
import com.example.amazonlite.entity.enums.PaymentStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository {
    Optional<Payment> findByOrderId(String orderId);
    List<Payment> findByWalletId(String walletId);
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);
}
