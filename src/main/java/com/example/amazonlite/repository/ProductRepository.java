package com.example.amazonlite.repository;

import com.example.amazonlite.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository {
    List<Product> findBySellerId(String sellerId);
    List<Product> findByCategoryId(String categoryId);
    List<Product> findByNameOfProductContainingIgnoreCase(String name);
    Boolean existsByProductIdAndSellerId(String productId,String sellerId);
}
