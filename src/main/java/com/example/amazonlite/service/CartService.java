package com.example.amazonlite.service;

import com.example.amazonlite.dto.AddToCartRequest;
import com.example.amazonlite.entity.Cart;
import com.example.amazonlite.entity.CartItems;
import com.example.amazonlite.entity.Product;
import com.example.amazonlite.entity.User;
import com.example.amazonlite.exceptions.InsufficientStockException;
import com.example.amazonlite.exceptions.ResourceNotFoundException;
import com.example.amazonlite.repository.CartItemsRepository;
import com.example.amazonlite.repository.CartRepository;
import com.example.amazonlite.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final ProductRepository productRepository;

    // get or create active cart for user
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserIdAndIsDeletedFalse(user.getUserId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(user.getUserId())
                            .isDeleted(false)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    // add item to cart
    @Transactional
    public Cart addToCart(AddToCartRequest request, User user) {

        // check product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // check stock is available
        if (product.getStock() < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Only " + product.getStock() + " items left in stock");
        }

        Cart cart = getOrCreateCart(user);

        // if product already in cart — update quantity
        if (cartItemsRepository.existsByCartIdAndProductId(
                cart.getCartId(), request.getProductId())) {

            CartItems existingItem = cartItemsRepository
                    .findByCartIdAndProductId(cart.getCartId(), request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

            // check combined quantity doesn't exceed stock
            int newQty = existingItem.getQuantity() + request.getQuantity();
            if (newQty > product.getStock()) {
                throw new InsufficientStockException(
                        "Cannot add more. Only " + product.getStock() + " items available");
            }

            existingItem.setQuantity(newQty);
            cartItemsRepository.save(existingItem);

        } else {
            // product not in cart — add new item
            CartItems newItem = CartItems.builder()
                    .cartId(cart.getCartId())
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .build();
            cartItemsRepository.save(newItem);
        }

        return cart;
    }

    // get all items in cart
    public List<CartItems> getCartItems(User user) {
        Cart cart = getOrCreateCart(user);
        return cartItemsRepository.findByCartId(cart.getCartId());
    }

    // update quantity of a specific item
    @Transactional
    public CartItems updateCartItem(String productId, Integer quantity, User user) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (quantity > product.getStock()) {
            throw new InsufficientStockException(
                    "Only " + product.getStock() + " items available");
        }

        Cart cart = getOrCreateCart(user);

        CartItems item = cartItemsRepository
                .findByCartIdAndProductId(cart.getCartId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not in cart"));

        item.setQuantity(quantity);
        return cartItemsRepository.save(item);
    }

    // remove specific item from cart
    @Transactional
    public void removeFromCart(String productId, User user) {
        Cart cart = getOrCreateCart(user);

        if (!cartItemsRepository.existsByCartIdAndProductId(
                cart.getCartId(), productId)) {
            throw new ResourceNotFoundException("Item not found in cart");
        }

        cartItemsRepository.deleteByCartIdAndProductId(cart.getCartId(), productId);
    }

    // clear entire cart
    @Transactional
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cartItemsRepository.deleteByCartId(cart.getCartId());
    }
}