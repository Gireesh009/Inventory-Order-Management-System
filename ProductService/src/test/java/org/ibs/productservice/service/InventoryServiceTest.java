package org.ibs.productservice.service;

import org.ibs.productservice.entity.Product;
import org.ibs.productservice.exception.ProductNotFoundException;
import org.ibs.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private InventoryService service;

    @Test
    void shouldReturnStockSuccessfully() {

        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(10);

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Product result = service.getStock(1L);

        assertNotNull(result);
        assertEquals(10, result.getStockQuantity());

        verify(repository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> service.getStock(1L));
    }

    @Test
    void shouldUpdateStockSuccessfully() {

        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(5);

        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(any(Product.class))).thenReturn(product);

        Product result = service.updateStock(1L, 20);

        assertEquals(20, result.getStockQuantity());

        verify(repository).save(product);
    }

    @Test
    void shouldReduceStockSuccessfully() {

        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(10);

        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(any(Product.class))).thenReturn(product);

        Product result = service.reduceStock(1L, 3);

        assertEquals(7, result.getStockQuantity());

        verify(repository).save(product);
    }

    @Test
    void shouldThrowExceptionWhenInsufficientStock() {

        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(2);

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class,
                () -> service.reduceStock(1L, 5));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldIncreaseStockSuccessfully() {

        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(5);

        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(any(Product.class))).thenReturn(product);

        Product result = service.increaseStock(1L, 10);

        assertEquals(15, result.getStockQuantity());

        verify(repository).save(product);
    }
}