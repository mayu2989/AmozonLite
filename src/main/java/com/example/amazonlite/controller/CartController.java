package com.example.amazonlite.controller;

import com.example.amazonlite.dto.AddToCartRequest;
import com.example.amazonlite.dto.ApiResponse;
import com.example.amazonlite.entity.Cart;
import com.example.amazonlite.entity.CartItems;
import com.example.amazonlite.entity.User;
import com.example.amazonlite.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // add item to cart
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Cart>> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            @AuthenticationPrincipal User user) {
        Cart cart = cartService.addToCart(request, user);
        return ResponseEntity.ok(ApiResponse.success(cart, "Item added to cart"));
    }

    // get all cart items
    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItems>>> getCart(
            @AuthenticationPrincipal User user) {
        List<CartItems> items = cartService.getCartItems(user);
        return ResponseEntity.ok(ApiResponse.success(items, "Cart fetched"));
    }

    // update quantity of item
    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponse<CartItems>> updateItem(
            @PathVariable String productId,
            @RequestParam Integer quantity,
            @AuthenticationPrincipal User user) {
        CartItems item = cartService.updateCartItem(productId, quantity, user);
        return ResponseEntity.ok(ApiResponse.success(item, "Cart item updated"));
    }

    // remove specific item
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(
            @PathVariable String productId,
            @AuthenticationPrincipal User user) {
        cartService.removeFromCart(productId, user);
        return ResponseEntity.ok(ApiResponse.success(null, "Item removed from cart"));
    }

    // clear entire cart
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ResponseEntity.ok(ApiResponse.success(null, "Cart cleared"));
    }
}