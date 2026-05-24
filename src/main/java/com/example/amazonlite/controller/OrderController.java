package com.example.amazonlite.controller;

import com.example.amazonlite.dto.ApiResponse;
import com.example.amazonlite.dto.BuyNowRequest;
import com.example.amazonlite.entity.Order;
import com.example.amazonlite.entity.OrderItems;
import com.example.amazonlite.entity.User;
import com.example.amazonlite.entity.enums.OrderStatus;
import com.example.amazonlite.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // place order from cart
    @PostMapping("/place")
    public ResponseEntity<ApiResponse<Order>> placeOrder(
            @AuthenticationPrincipal User user) {
        Order order = orderService.placeOrderFromCart(user);
        return ResponseEntity.ok(ApiResponse.success(order, "Order placed successfully"));
    }

    // buy now — skip cart
    @PostMapping("/buy-now")
    public ResponseEntity<ApiResponse<Order>> buyNow(
            @Valid @RequestBody BuyNowRequest request,
            @AuthenticationPrincipal User user) {
        Order order = orderService.buyNow(request, user);
        return ResponseEntity.ok(ApiResponse.success(order, "Order placed successfully"));
    }

    // get my orders
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<Order>>> getMyOrders(
            @AuthenticationPrincipal User user) {
        List<Order> orders = orderService.getMyOrders(user);
        return ResponseEntity.ok(ApiResponse.success(orders, "Orders fetched"));
    }

    // get specific order
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrder(
            @PathVariable String orderId,
            @AuthenticationPrincipal User user) {
        Order order = orderService.getOrderById(orderId, user);
        return ResponseEntity.ok(ApiResponse.success(order, "Order fetched"));
    }

    // get items inside an order
    @GetMapping("/{orderId}/items")
    public ResponseEntity<ApiResponse<List<OrderItems>>> getOrderItems(
            @PathVariable String orderId,
            @AuthenticationPrincipal User user) {
        List<OrderItems> items = orderService.getOrderItems(orderId, user);
        return ResponseEntity.ok(ApiResponse.success(items, "Order items fetched"));
    }

    // cancel order
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(
            @PathVariable String orderId,
            @AuthenticationPrincipal User user) {
        Order order = orderService.cancelOrder(orderId, user);
        return ResponseEntity.ok(ApiResponse.success(order, "Order cancelled"));
    }

    // update order status (seller/admin)
    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<Order>> updateStatus(
            @PathVariable String orderId,
            @RequestParam OrderStatus status,
            @AuthenticationPrincipal User user) {
        Order order = orderService.updateOrderStatus(orderId, status, user);
        return ResponseEntity.ok(ApiResponse.success(order, "Order status updated"));
    }
}