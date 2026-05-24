package com.example.amazonlite.service;

import com.example.amazonlite.dto.BuyNowRequest;
import com.example.amazonlite.entity.*;
import com.example.amazonlite.entity.enums.OrderStatus;
import com.example.amazonlite.exceptions.InsufficientStockException;
import com.example.amazonlite.exceptions.ResourceNotFoundException;
import com.example.amazonlite.exceptions.UnauthorizedException;
import com.example.amazonlite.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final ProductRepository productRepository;

    // place order from cart
    @Transactional
    public Order placeOrderFromCart(User user) {

        // get active cart
        Cart cart = cartRepository.findByUserIdAndIsDeletedFalse(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart is empty"));

        // get all items in cart
        List<CartItems> cartItems = cartItemsRepository.findByCartId(cart.getCartId());

        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }

        // calculate total + validate stock
        int totalAmount = 0;
        for (CartItems item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (product.getStock() < item.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for: " + product.getNameOfProduct());
            }
            totalAmount += product.getPrice() * item.getQuantity();
        }

        // create order
        Order order = Order.builder()
                .userId(user.getUserId())
                .totalAmount(totalAmount)
                .orderStatus(OrderStatus.PENDING)
                .build();
        orderRepository.save(order);

        // create order items + decrement stock
        for (CartItems item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            // create order item — snapshot price at purchase time
            OrderItems orderItem = OrderItems.builder()
                    .orderId(order.getOrderId())
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .price(product.getPrice())
                    .build();
            orderItemsRepository.save(orderItem);

            // decrement stock
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        // clear cart after order placed
        cartItemsRepository.deleteByCartId(cart.getCartId());

        return order;
    }

    // buy now — skip cart
    @Transactional
    public Order buyNow(BuyNowRequest request, User user) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStock() < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Only " + product.getStock() + " items left in stock");
        }

        // create order
        Order order = Order.builder()
                .userId(user.getUserId())
                .totalAmount(product.getPrice() * request.getQuantity())
                .orderStatus(OrderStatus.PENDING)
                .build();
        orderRepository.save(order);

        // create single order item
        OrderItems orderItem = OrderItems.builder()
                .orderId(order.getOrderId())
                .productId(product.getProductId())
                .quantity(request.getQuantity())
                .price(product.getPrice())
                .build();
        orderItemsRepository.save(orderItem);

        // decrement stock
        product.setStock(product.getStock() - request.getQuantity());
        productRepository.save(product);

        return order;
    }

    // get all orders for buyer
    public List<Order> getMyOrders(User user) {
        return orderRepository.findByUserId(user.getUserId());
    }

    // get order by id
    public Order getOrderById(String orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // buyer can only see their own orders
        if (!order.getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("You can only view your own orders");
        }
        return order;
    }

    // get items inside a specific order
    public List<OrderItems> getOrderItems(String orderId, User user) {
        getOrderById(orderId, user); // validates ownership
        return orderItemsRepository.findByOrderId(orderId);
    }

    // cancel order — only PENDING orders can be cancelled
    @Transactional
    public Order cancelOrder(String orderId, User user) {
        Order order = getOrderById(orderId, user);

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new UnauthorizedException("Only PENDING orders can be cancelled");
        }

        // restore stock
        List<OrderItems> items = orderItemsRepository.findByOrderId(orderId);
        for (OrderItems item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    // update order status — seller/admin use case
    @Transactional
    public Order updateOrderStatus(String orderId, OrderStatus newStatus, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setOrderStatus(newStatus);
        return orderRepository.save(order);
    }
}