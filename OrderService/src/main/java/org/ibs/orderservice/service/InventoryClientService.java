package org.ibs.orderservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.ibs.orderservice.Exceptions.InsufficientStockException;
import org.ibs.orderservice.Exceptions.ProductServiceUnavailableException;
import org.ibs.orderservice.dto.ProductResponse;
import org.ibs.orderservice.dto.StockUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;



@Service
public class InventoryClientService {

    private final RestTemplate restTemplate;

    @Value("${productService.base-url}")
    private String productServiceUrl;

    public InventoryClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(
            name = "productService",
            fallbackMethod = "getProductFallback"
    )
    public ProductResponse getProduct(Long productId) {
        return restTemplate.getForObject(
                productServiceUrl + "/products/" + productId,
                ProductResponse.class
        );

    }
    @CircuitBreaker(
            name = "productService",
            fallbackMethod = "reduceStockFallback"
    )
    public void reduceStock(Long productId, Integer qty, String token) {

        StockUpdateRequest request = new StockUpdateRequest();
        request.setProductId(productId);
        request.setQuantity(qty);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // forward JWT token for product reduce APi authentication
        headers.setBearerAuth(token.replace("Bearer ", ""));

        HttpEntity<StockUpdateRequest> entity =
                new HttpEntity<>(request, headers);

        restTemplate.exchange(
                productServiceUrl + "/inventory/reduce",
                HttpMethod.PUT,
                entity,
                Void.class
        );
    }
    @CircuitBreaker(
            name = "productService",
            fallbackMethod = "increaseStockFallback"
    )
    public void increaseStock(Long productId, Integer qty, String token) {

        StockUpdateRequest request = new StockUpdateRequest();
        request.setProductId(productId);
        request.setQuantity(qty);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token.replace("Bearer ", ""));

        HttpEntity<StockUpdateRequest> entity =
                new HttpEntity<>(request, headers);

        restTemplate.exchange(
                productServiceUrl + "/inventory/increase",
                HttpMethod.PUT,
                entity,
                Void.class
        );
    }
    // Fallback Methods

    public ProductResponse getProductFallback(
            Long productId,
            Exception ex) {

        if (ex instanceof HttpClientErrorException.NotFound) {
            throw new InsufficientStockException("Currently out of stock; Please try again later...");
        }
        throw new ProductServiceUnavailableException(
                "Product Service is currently unavailable. Please try again later."
        );
    }

    public void reduceStockFallback(
            Long productId,
            Integer qty,
            String token,
            Exception ex) {

        throw new ProductServiceUnavailableException(
                "Unable to reduce stock because Product Service is unavailable."
        );
    }

    public void increaseStockFallback(
            Long productId,
            Integer qty,
            String token,
            Exception ex) {

        throw new ProductServiceUnavailableException(
                "Unable to increase stock because Product Service is unavailable."
        );
    }
}