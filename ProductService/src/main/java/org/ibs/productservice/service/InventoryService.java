package org.ibs.productservice.service;

import lombok.RequiredArgsConstructor;
import org.ibs.productservice.entity.Product;
import org.ibs.productservice.exception.ProductNotFoundException;
import org.ibs.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository repository;

    public List<Product> getAll() {
        return repository.findAll();
    }
    public Product getStock(Long productId) {
        Product p = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        return p;
    }

    public Product updateStock(Long productId, Integer qty) {
        Product p = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        p.setStock(qty);
        repository.save(p);
        return p;
    }

    public List<Product> lowStock(Integer threshold) {
        return repository.findByStockLessThan(threshold);
    }
    public Product reduceStock(Long productId, Integer qty) {

        Product product = repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < qty) {
            throw new RuntimeException("Insufficient stock");
        }

        product.setStock(product.getStock() - qty);
        return repository.save(product);
    }

    @Transactional
    public Product increaseStock(Long productId, Integer qty) {

        Product product = repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStock(product.getStock() + qty);

        return repository.save(product);
    }
}