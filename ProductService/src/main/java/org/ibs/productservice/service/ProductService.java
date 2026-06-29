package org.ibs.productservice.service;

import org.ibs.productservice.dto.BulkProductResponse;
import org.ibs.productservice.dto.ProductRequestDTO;
import org.ibs.productservice.dto.ProductResponseDTO;
import org.ibs.productservice.dto.SkippedProduct;
import org.ibs.productservice.entity.Product;
import org.ibs.productservice.exception.InsufficientStockException;
import org.ibs.productservice.exception.ProductAlreadyExistsException;
import org.ibs.productservice.exception.ProductNotFoundException;
import org.ibs.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    public List<ProductResponseDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Product getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
    }

    public Product create(Product p) {

        Optional<Product> existingProduct = repo.findByName(p.getName());

        if (existingProduct.isPresent()) {
            throw new ProductAlreadyExistsException(
                    "Product already exists with name: " + p.getName());
        }
        return repo.save(p);
    }

    public Product update(Long id, ProductRequestDTO dto) {
        Product p = getById(id);

        p.setName(dto.getName());
        p.setPrice(dto.getPrice());
        p.setCurrency(dto.getCurrency());
        p.setStock(dto.getStock());
        return repo.save(p);
    }

    public Product delete(Long id) {
        Product p = getById(id);
        repo.delete(p);
        return p;
    }

    public void reduceStock(Long id, Integer qty) {
        Product p = getById(id);

        if (p.getStock() < qty) {
            throw new InsufficientStockException("Insufficient stock");
        }

        p.setStock(p.getStock() - qty);
        repo.save(p);
    }

    private ProductResponseDTO toDto(Product p) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setPrice(p.getPrice());
        dto.setCurrency(p.getCurrency());
        return dto;
    }

    public BulkProductResponse saveAll(List<Product> products) {


        List<Product> productsToSave = new ArrayList<>();
        List<String> saved = new ArrayList<>();
        List<SkippedProduct> skipped = new ArrayList<>();

        for (Product product : products) {

            if (repo.findByName(product.getName()).isPresent()) {

                skipped.add(
                        new SkippedProduct(
                                product.getName(),
                                "Product already exists"
                        )
                );

            } else {

                productsToSave.add(product);
                saved.add(product.getName());
            }
        }

        repo.saveAll(productsToSave);

        return BulkProductResponse.builder()
                .saved(saved)
                .skipped(skipped)
                .build();
    }
}