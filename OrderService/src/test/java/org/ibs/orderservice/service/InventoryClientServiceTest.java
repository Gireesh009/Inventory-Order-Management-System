package org.ibs.orderservice.service;

import org.ibs.orderservice.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryClientServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private InventoryClientService service;

    @BeforeEach
    void setUp() {
        // manually set base URL using reflection
        try {
            var field = InventoryClientService.class.getDeclaredField("productServiceUrl");
            field.setAccessible(true);
            field.set(service, "http://product-service");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldReturnProductSuccessfully() {

        ProductResponse response = new ProductResponse();
        response.setId(1L);

        when(restTemplate.getForObject(
                anyString(),
                eq(ProductResponse.class)
        )).thenReturn(response);

        ProductResponse result = service.getProduct(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(restTemplate, times(1))
                .getForObject(anyString(), eq(ProductResponse.class));
    }

    @Test
    void shouldCallReduceStockApiCorrectly() {

        String token = "Bearer abc123";

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(ResponseEntity.ok().build());

        service.reduceStock(1L, 5, token);

        verify(restTemplate, times(1))
                .exchange(
                        anyString(),
                        eq(HttpMethod.PUT),
                        any(HttpEntity.class),
                        eq(Void.class)
                );
    }

    @Test
    void shouldCallIncreaseStockApiCorrectly() {

        String token = "Bearer abc123";

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(ResponseEntity.ok().build());

        service.increaseStock(1L, 5, token);

        verify(restTemplate, times(1))
                .exchange(
                        anyString(),
                        eq(HttpMethod.PUT),
                        any(HttpEntity.class),
                        eq(Void.class)
                );
    }

    @Test
    void shouldThrowExceptionWhenProductServiceFails() {

        Exception ex = new RuntimeException("Service down");

        assertThrows(
                RuntimeException.class,
                () -> service.getProductFallback(1L, ex)
        );
    }
}