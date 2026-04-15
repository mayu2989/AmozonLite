package com.example.amazonlite.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateProductRequest {
    @NotBlank(message = "Name of product is required")
    private String nameOfProduct;

    @NotNull(message = "Category is required")
    private String categoryId;

    @NotNull(message = "Price is required")
    @Min(value = 1,message = "Price must be at least 1")
    private Integer price;

    @NotNull(message = "Stock is required")
    @Min(value = 0,message = "Stock cant be negative")
    private Integer stock;


    private String description;
    private LocalDateTime deliveryDate;
}
