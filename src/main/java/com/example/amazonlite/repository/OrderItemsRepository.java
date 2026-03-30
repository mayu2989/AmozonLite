package com.example.amazonlite.repository;

import com.example.amazonlite.entity.OrderItems;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemsRepository {
    List<OrderItems> findByOrderId(String orderId);
    List<OrderItems> findByProductId(String productId);
}
