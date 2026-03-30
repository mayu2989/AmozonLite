package com.example.amazonlite.repository;


import com.example.amazonlite.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,String> {

    List<Review> findByProductId(String productId);

    List<Review> findByUserId(String userId);

    boolean existsByProductIdAndUserId(String productId, String userId);

    @Query("SELECT AVG(r.individualProductRating) FROM Review r WHERE r.productId = :productId")
    Double findAverageRatingByProductId(@Param("productId") String productId);

}
