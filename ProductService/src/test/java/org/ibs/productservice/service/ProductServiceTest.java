package org.ibs.productservice.service;

import org.ibs.productservice.dto.ProductRequestDTO;
import org.ibs.productservice.dto.ProductResponseDTO;
import org.ibs.productservice.entity.Product;
import org.ibs.productservice.exception.InsufficientStockException;
import org.ibs.productservice.exception.ProductNotFoundException;
import org.ibs.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repo;

    @InjectMocks
    private ProductService service;

    @Test
    void shouldReturnAllProductsAsDTO() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Phone");
        product.setPrice(1000.0);

        when(repo.findAll()).thenReturn(List.of(product));

        List<ProductResponseDTO> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("Phone", result.get(0).getName());

        verify(repo).findAll();
    }

    @Test
    void shouldReturnProductById() {

        Product product = new Product();
        product.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(product));

        Product result = service.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowWhenProductNotFound() {

        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> service.getById(1L));
    }

    @Test
    void shouldCreateProduct() {

        Product product = new Product();
        product.setName("Laptop");

        when(repo.save(product)).thenReturn(product);

        Product result = service.create(product);

        assertEquals("Laptop", result.getName());

        verify(repo).save(product);
    }

    @Test
    void shouldUpdateProduct() {

        Product existing = new Product();
        existing.setId(1L);
        existing.setName("Old");

        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("New");
        dto.setPrice(2000.0);

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Product.class))).thenReturn(existing);

        Product result = service.update(1L, dto);

        assertEquals("New", result.getName());
        assertEquals(2000.0, result.getPrice());
    }

    @Test
    void shouldDeleteProduct() {

        Product product = new Product();
        product.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(product));

        Product result = service.delete(1L);

        assertEquals(1L, result.getId());

        verify(repo).delete(product);
    }

    @Test
    void shouldReduceStockSuccessfully() {

        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(10);

        when(repo.findById(1L)).thenReturn(Optional.of(product));
        when(repo.save(any(Product.class))).thenReturn(product);

        service.reduceStock(1L, 3);

        assertEquals(7, product.getStockQuantity());

        verify(repo).save(product);
    }

    @Test
    void shouldThrowWhenStockIsInsufficient() {

        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(2);

        when(repo.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class,
                () -> service.reduceStock(1L, 5));

        verify(repo, never()).save(any());
    }
}