package com.example.amazonlite.service;

import com.example.amazonlite.entity.Order;
import com.example.amazonlite.entity.Payment;
import com.example.amazonlite.entity.User;
import com.example.amazonlite.entity.Wallet;
import com.example.amazonlite.entity.enums.OrderStatus;
import com.example.amazonlite.entity.enums.PaymentStatus;
import com.example.amazonlite.exceptions.InsufficientBalanceException;
import com.example.amazonlite.exceptions.ResourceNotFoundException;
import com.example.amazonlite.exceptions.UnauthorizedException;
import com.example.amazonlite.repository.OrderRepository;
import com.example.amazonlite.repository.PaymentRepository;
import com.example.amazonlite.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final WalletRepository walletRepository;

    // pay for an order using wallet
    @Transactional
    public Payment payForOrder(String orderId, User user) {

        // get the order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // verify order belongs to this user
        if (!order.getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("This is not your order");
        }

        // check order is in PENDING state
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new UnauthorizedException("Order is already paid or cancelled");
        }

        // check if payment already exists for this order
        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            throw new UnauthorizedException("Payment already done for this order");
        }

        // get wallet
        Wallet wallet = walletRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        // check sufficient balance
        if (wallet.getAvailableAmount() < order.getTotalAmount()) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Required: " + order.getTotalAmount()
                            + ", Available: " + wallet.getAvailableAmount());
        }

        // deduct amount from wallet
        wallet.setAvailableAmount(wallet.getAvailableAmount() - order.getTotalAmount());
        walletRepository.save(wallet);

        // update order status to CONFIRMED
        order.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        // create payment record
        Payment payment = Payment.builder()
                .orderId(orderId)
                .walletId(wallet.getWalletId())
                .amount(order.getTotalAmount())
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();

        return paymentRepository.save(payment);
    }

    // get payment for an order
    public Payment getPaymentByOrder(String orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("This is not your order");
        }

        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    // get all payments from wallet (transaction history)
    public List<Payment> getPaymentHistory(User user) {
        Wallet wallet = walletRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        return paymentRepository.findByWalletId(wallet.getWalletId());
    }
}