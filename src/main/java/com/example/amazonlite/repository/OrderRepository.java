package com.example.amazonlite.repository;


import com.example.amazonlite.entity.Order;
import com.example.amazonlite.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,String> {
    List<Order> findByUserId(String userId);
    List<Order> findByOrderStatus(OrderStatus orderStatus);
    List<Order> findByUserIdAndOrderStatus(String userId,OrderStatus orderStatus);

}
