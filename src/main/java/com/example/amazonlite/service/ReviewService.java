package com.example.amazonlite.service;

import com.example.amazonlite.dto.CreateReviewRequest;
import com.example.amazonlite.entity.Product;
import com.example.amazonlite.entity.Review;
import com.example.amazonlite.entity.User;
import com.example.amazonlite.exceptions.AlreadyExistsException;
import com.example.amazonlite.exceptions.ResourceNotFoundException;
import com.example.amazonlite.exceptions.UnauthorizedException;
import com.example.amazonlite.repository.ProductRepository;
import com.example.amazonlite.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    // add review
    @Transactional
    public Review addReview(CreateReviewRequest request, User user) {

        // check product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // check user hasn't already reviewed this product
        if (reviewRepository.existsByProductIdAndUserId(
                request.getProductId(), user.getUserId())) {
            throw new AlreadyExistsException("You have already reviewed this product");
        }

        // validate rating is between 1 and 5
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // save review
        Review review = Review.builder()
                .productId(request.getProductId())
                .userId(user.getUserId())
                .comment(request.getComment())
                .individualProductRating(request.getRating())
                .build();
        reviewRepository.save(review);

        // update product average rating
        updateProductRating(product);

        return review;
    }

    // get all reviews for a product
    public List<Review> getProductReviews(String productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return reviewRepository.findByProductId(productId);
    }

    // get all reviews by a user
    public List<Review> getMyReviews(User user) {
        return reviewRepository.findByUserId(user.getUserId());
    }

    // delete review — only the user who wrote it
    @Transactional
    public void deleteReview(String reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        // ownership check
        if (!review.getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);

        // recalculate product rating after deletion
        Product product = productRepository.findById(review.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        updateProductRating(product);
    }

    // recalculate and update average rating on product
    private void updateProductRating(Product product) {
        Double avgRating = reviewRepository
                .findAverageRatingByProductId(product.getProductId());

        // if no reviews left, set to 0.0
        product.setRatingAvg(avgRating != null ? avgRating : 0.0);
        productRepository.save(product);
    }
}