package org.ibs.productservice.controller;

import lombok.RequiredArgsConstructor;
import org.ibs.productservice.dto.ApiResponse;
import org.ibs.productservice.dto.StockUpdateRequest;
import org.ibs.productservice.entity.Product;
import org.ibs.productservice.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<Product>>> getAll() {

        List<Product> products = inventoryService.getAll();

        if (products.isEmpty()) {
            return ResponseEntity.ok(
                    new ApiResponse<>("No products found", products)
            );
        }

        return ResponseEntity.ok(
                new ApiResponse<>("Products fetched successfully", products)
        );
    }
    @GetMapping("/{productId}")
    public ResponseEntity<?> getStock(@PathVariable Long productId) {
        Product product = inventoryService.getStock(productId);

        if (product == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Product not found with id: " + productId);
        }

        return ResponseEntity.ok(product);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Product>> updateStock(@RequestBody StockUpdateRequest request) {
        Product stock = inventoryService.updateStock(
                request.getProductId(),
                request.quantity()
        );
        return ResponseEntity.ok(
                new ApiResponse<>("Product updated successfully", stock)
        );
    }
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<Product>>> lowStock(
            @RequestParam(defaultValue = "5") Integer threshold) {

        List<Product> lowStock = inventoryService.lowStock(threshold);

        return ResponseEntity.ok(
                new ApiResponse<>("Low stock products fetched successfully", lowStock)
        );

    }
    @PutMapping("/reduce")
    public ResponseEntity<ApiResponse<Product>> reduceStock(
            @RequestBody StockUpdateRequest request) {

        Product updated = inventoryService.reduceStock(
                request.getProductId(),
                request.quantity()
        );

        return ResponseEntity.ok(
                new ApiResponse<>("Stock reduced successfully", updated)
        );
    }
    @PutMapping("/increase")
    public ResponseEntity<?> increaseStock(@RequestBody StockUpdateRequest request) {
        Product updated= inventoryService.increaseStock(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(
                new ApiResponse<>("Stock increased successfully", updated));
    }
}