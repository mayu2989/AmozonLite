package com.example.amazonlite.controller;

import com.example.amazonlite.dto.ApiResponse;
import com.example.amazonlite.dto.CreateProductRequest;
import com.example.amazonlite.dto.UpdateProductRequest;
import com.example.amazonlite.entity.Product;
import com.example.amazonlite.entity.User;
import com.example.amazonlite.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> create(
            @Valid @RequestBody CreateProductRequest request,
            @AuthenticationPrincipal User seller) {
        Product product = productService.createProduct(request, seller);
        return ResponseEntity.ok(ApiResponse.success(product, "Product created"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAll() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products, "Products fetched"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getById(@PathVariable String id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product, "Product fetched"));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<Product>>> getByCategory(
            @PathVariable String categoryId) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(products, "Products fetched"));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<ApiResponse<List<Product>>> getBySeller(
            @PathVariable String sellerId) {
        List<Product> products = productService.getProductsBySeller(sellerId);
        return ResponseEntity.ok(ApiResponse.success(products, "Products fetched"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Product>>> search(
            @RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(ApiResponse.success(products, "Search results fetched"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request,
            @AuthenticationPrincipal User seller) {
        Product product = productService.updateProduct(id, request, seller);
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String id,
            @AuthenticationPrincipal User seller) {
        productService.deleteProduct(id, seller);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted"));
    }
}