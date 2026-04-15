package com.example.amazonlite.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateProductRequest {
    private String nameOfProduct;

    @Min(value = 1,message = "Price must be at least 1")
    private Integer price;

    @Min(value = 0,message = "Stock cannot be negative")
    private Integer stock;

    private String description;
    private LocalDateTime deliveryDate;
}
