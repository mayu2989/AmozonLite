package com.example.amazonlite.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,Object>> handleRuntimeException(RuntimeException ex){
        Map<String,Object> error = new HashMap<>();
        error.put("Success",false);
        error.put("message",ex.getMessage());
        error.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleException(Exception ex)
    {
        Map<String,Object> error = new HashMap<>();
        error.put("success",false);
        error.put("message","Something went wrong");
        error.put("timestamp",LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
