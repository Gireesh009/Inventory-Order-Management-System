package org.ibs.productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ibs.productservice.dto.ApiResponse;
import org.ibs.productservice.dto.BulkProductResponse;
import org.ibs.productservice.dto.ProductRequestDTO;
import org.ibs.productservice.dto.ProductResponseDTO;
import org.ibs.productservice.entity.Product;
import org.ibs.productservice.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @GetMapping("/list")
    public List<ProductResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody ProductRequestDTO dto) {
        Product p = Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .currency(dto.getCurrency())
                .stock(dto.getStock())
                .build();

        Product savedProduct = service.create(p);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product created successfully");
        response.put("data", savedProduct);

        return ResponseEntity.status(201).body(response);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Product>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO dto) {
        Product updatedProduct = service.update(id, dto);

        return ResponseEntity.ok(
                new ApiResponse<>("Product updated successfully", updatedProduct)
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Product deletedProduct = service.delete(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product deleted successfully");
        response.put("deletedProduct", deletedProduct);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/bulk-add")
    public ResponseEntity<Map<String, Object>> createMultiple(
            @Valid @RequestBody List<ProductRequestDTO> dtos) {

        List<Product> products = dtos.stream()
                .map(dto -> Product.builder()
                        .name(dto.getName())
                        .price(dto.getPrice())
                        .currency(dto.getCurrency())
                        .stock(dto.getStock())
                        .build())
                .toList();

        BulkProductResponse result = service.saveAll(products);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Bulk operation completed");
        response.put("data", result);

        return ResponseEntity.status(201).body(response);
    }

}