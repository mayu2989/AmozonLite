package com.example.amazonlite.dto;

import lombok.Data;

@Data
public class PlaceOrderRequest {
    // empty — order is created directly from cart
    // buyer just hits "place order" and backend reads their cart
}