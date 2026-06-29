package org.ibs.productservice.exception;

import org.ibs.productservice.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(ProductNotFoundException ex) {

        ApiResponse error = new ApiResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error);
    }
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<?> handleStock(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleProductAlreadyExistsException(
            ProductAlreadyExistsException ex) {

        ApiResponse error = new ApiResponse(
                ex.getMessage(),
                HttpStatus.CONFLICT
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(error);
    }
}