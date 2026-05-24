package com.example.amazonlite.controller;

import com.example.amazonlite.dto.ApiResponse;
import com.example.amazonlite.dto.CreateReviewRequest;
import com.example.amazonlite.entity.Review;
import com.example.amazonlite.entity.User;
import com.example.amazonlite.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // add review
    @PostMapping
    public ResponseEntity<ApiResponse<Review>> addReview(
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal User user) {
        Review review = reviewService.addReview(request, user);
        return ResponseEntity.ok(ApiResponse.success(review, "Review added successfully"));
    }

    // get all reviews for a product
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<Review>>> getProductReviews(
            @PathVariable String productId) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(ApiResponse.success(reviews, "Reviews fetched"));
    }

    // get my reviews
    @GetMapping("/my-reviews")
    public ResponseEntity<ApiResponse<List<Review>>> getMyReviews(
            @AuthenticationPrincipal User user) {
        List<Review> reviews = reviewService.getMyReviews(user);
        return ResponseEntity.ok(ApiResponse.success(reviews, "My reviews fetched"));
    }

    // delete review
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable String reviewId,
            @AuthenticationPrincipal User user) {
        reviewService.deleteReview(reviewId, user);
        return ResponseEntity.ok(ApiResponse.success(null, "Review deleted"));
    }
}