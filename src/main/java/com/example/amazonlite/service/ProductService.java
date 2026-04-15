package com.example.amazonlite.service;

import com.example.amazonlite.dto.CreateProductRequest;
import com.example.amazonlite.dto.UpdateProductRequest;
import com.example.amazonlite.entity.Product;
import com.example.amazonlite.entity.User;
import com.example.amazonlite.exceptions.ResourceNotFoundException;
import com.example.amazonlite.exceptions.UnauthorizedException;
import com.example.amazonlite.repository.CategoryRepository;
import com.example.amazonlite.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // create product — only sellers can do this
    public Product createProduct(CreateProductRequest request, User seller) {

        // verify category exists
        categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = Product.builder()
                .nameOfProduct(request.getNameOfProduct())
                .price(request.getPrice())
                .stock(request.getStock())
                .categoryId(request.getCategoryId())
                .sellerId(seller.getUserId())
                .description(request.getDescription())
                .deliveryDate(request.getDeliveryDate())
                .ratingAvg(0.0)
                .build();

        return productRepository.save(product);
    }

    // get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // get product by id
    public Product getProductById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    // get products by category
    public List<Product> getProductsByCategory(String categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    // get products by seller
    public List<Product> getProductsBySeller(String sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    // search products by name
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameOfProductContainingIgnoreCase(keyword);
    }

    // update product — only the seller who owns it
    public Product updateProduct(String productId, UpdateProductRequest request, User seller) {

        Product product = getProductById(productId);

        // ownership check — seller can only edit their own products
        if (!product.getSellerId().equals(seller.getUserId())) {
            throw new UnauthorizedException("You can only update your own products");
        }

        // only update fields that are not null
        if (request.getNameOfProduct() != null) product.setNameOfProduct(request.getNameOfProduct());
        if (request.getPrice() != null)         product.setPrice(request.getPrice());
        if (request.getStock() != null)         product.setStock(request.getStock());
        if (request.getDescription() != null)   product.setDescription(request.getDescription());
        if (request.getDeliveryDate() != null)  product.setDeliveryDate(request.getDeliveryDate());

        return productRepository.save(product);
    }

    // delete product — only the seller who owns it
    public void deleteProduct(String productId, User seller) {

        Product product = getProductById(productId);

        // ownership check
        if (!product.getSellerId().equals(seller.getUserId())) {
            throw new UnauthorizedException("You can only delete your own products");
        }

        productRepository.delete(product);
    }
}