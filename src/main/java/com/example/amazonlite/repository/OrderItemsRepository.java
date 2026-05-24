package com.example.amazonlite.repository;

import com.example.amazonlite.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItems,String> {
    List<OrderItems> findByOrderId(String orderId);
    List<OrderItems> findByProductId(String productId);
}
